package job_portal_backend.service.verificationMail;

public interface EmailService {

    void sendEmail(String email, String verificationCode, String htmlContent);
}
