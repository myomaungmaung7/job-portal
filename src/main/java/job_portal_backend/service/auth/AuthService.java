package job_portal_backend.service.auth;

import job_portal_backend.dto.LoginRequest;
import job_portal_backend.dto.RegisterRequest;
import job_portal_backend.dto.VerifyOtpRequest;
import job_portal_backend.response.ApiResponse;

public interface AuthService {
    ApiResponse register(RegisterRequest request);

    ApiResponse verifyOtp(VerifyOtpRequest otpRequest);

    ApiResponse login(LoginRequest request);

    ApiResponse resendOtp(String email);
}
