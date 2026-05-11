package job_portal_backend.service.auth;

import job_portal_backend.common.middleware.JWTUtil;
import job_portal_backend.common.util.ServerUtil;
import job_portal_backend.dto.LoginRequest;
import job_portal_backend.dto.RegisterRequest;
import job_portal_backend.dto.VerifyOtpRequest;
import job_portal_backend.entity.Profile;
import job_portal_backend.entity.User;
import job_portal_backend.entity.VerificationToken;
import job_portal_backend.entity.enums.Role;
import job_portal_backend.entity.enums.UserStatus;
import job_portal_backend.mapper.ProfileMapper;
import job_portal_backend.mapper.UserMapper;
import job_portal_backend.repository.ProfileRepository;
import job_portal_backend.repository.UserRepository;
import job_portal_backend.repository.VerificationTokenRepository;
import job_portal_backend.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService{

    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final VerificationTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final ServerUtil serverUtil;
    private final AuthenticationManager authenticationManager;

    @Override
    @Transactional
    public ApiResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        if (request.getRole() == Role.ADMIN) {
            throw new RuntimeException("Admin cannot register");
        }

        // create user(inactive)
        User user = UserMapper.toEntity(
                request,
                passwordEncoder.encode(request.getPassword())
        );

        user.setStatus(UserStatus.SUSPEND);
        userRepository.save(user);

        // create profile
        Profile profile = ProfileMapper.toEntity(user);
        profileRepository.save(profile);

        serverUtil.sendCodeToEmail(user, 15, "verifyAccountMail");

        return ApiResponse.builder()
                .success(1)
                .code(201)
                .message("Verification code sent to email.")
                .build();
    }

    @Override
    @Transactional
    public ApiResponse verifyOtp(VerifyOtpRequest otpRequest) {

        // find user
        User user = userRepository.findByEmail(otpRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // find OTP
        VerificationToken token = tokenRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("OTP not found"));

        // check OTP
        if (!token.getOtp().equals(otpRequest.getOtp())) {
            throw new RuntimeException("Invalid OTP");
        }

        // check expiry
        if (token.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("OTP expired");
        }

        // activate user
        user.setStatus(UserStatus.ACTIVE);
        userRepository.save(user);

        // delete token
        tokenRepository.delete(token);

        return ApiResponse.builder()
                .success(1)
                .code(200)
                .message("Account activated successfully. You can now login.")
                .build();
    }

    @Override
    public ApiResponse login(LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );

            User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            String accessToken = serverUtil.generateToken(user);
            String refreshToken = serverUtil.generateRefreshToken(user);

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("accessToken", accessToken);
            responseData.put("refreshToken", refreshToken);
            responseData.put("role", user.getRole().name());
            responseData.put("email", user.getEmail());

            return ApiResponse.builder()
                    .success(1)
                    .code(200)
                    .message("Login successful")
                    .data(responseData)
                    .build();
        } catch (Exception e) {
            return ApiResponse.builder()
                    .success(0)
                    .code(401)
                    .message("Invalid email or password")
                    .build();
        }
    }

    @Override
    @Transactional
    public ApiResponse resendOtp(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getStatus() == UserStatus.ACTIVE) {
            throw new RuntimeException("Account already active");
        }

        tokenRepository.deleteByUserId(user.getId());
        serverUtil.sendCodeToEmail(user, 15, "verifyAccountMail");

        return ApiResponse.builder()
                .success(1)
                .code(200)
                .message("Verification code resent successfully")
                .build();
    }
}
