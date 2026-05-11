package job_portal_backend.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import job_portal_backend.dto.LoginRequest;
import job_portal_backend.dto.RegisterRequest;
import job_portal_backend.dto.VerifyOtpRequest;
import job_portal_backend.response.ApiResponse;
import job_portal_backend.service.auth.AuthService;
import job_portal_backend.util.ResponseUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> register(
            @Valid @RequestBody RegisterRequest request, HttpServletRequest httpRequest) {

        ApiResponse response = authService.register(request);

        return ResponseUtils.buildResponse(httpRequest, response);
    }

    @PostMapping("/verify")
    public ResponseEntity<ApiResponse> verify(
            @RequestBody VerifyOtpRequest otpRequest, HttpServletRequest httpRequest) {

        ApiResponse response = authService.verifyOtp(otpRequest);

        return ResponseUtils.buildResponse(httpRequest, response);
    }

    @PostMapping("/resend-otp")
    public ResponseEntity<ApiResponse> resendOtp(
            @RequestParam String email, HttpServletRequest httpRequest) {

        ApiResponse response = authService.resendOtp(email);

        return ResponseUtils.buildResponse(httpRequest, response);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(
            @RequestBody LoginRequest request, HttpServletRequest httpRequest) {

        ApiResponse response = authService.login(request);

        return ResponseUtils.buildResponse(httpRequest, response);
    }
}
