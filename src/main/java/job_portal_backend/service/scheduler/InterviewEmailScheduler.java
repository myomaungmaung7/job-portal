package job_portal_backend.service.scheduler;

import job_portal_backend.entity.Application;
import job_portal_backend.entity.User;
import job_portal_backend.entity.enums.ApplicationStatus;
import job_portal_backend.repository.ApplicationRepository;
import job_portal_backend.repository.UserRepository;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class InterviewEmailScheduler {
    private final ApplicationRepository applicationRepository;
    private final UserRepository userRepository;
    private final JavaMailSender mailSender;

    public InterviewEmailScheduler(ApplicationRepository applicationRepository,
                                   UserRepository userRepository,
                                   JavaMailSender mailSender) {
        this.applicationRepository = applicationRepository;
        this.userRepository = userRepository;
        this.mailSender = mailSender;
    }

    @Scheduled(fixedRate = 10000)
    @Transactional
    public void sendBatchInterviewEmails() {
        System.out.println("⏰ Scheduled Job Started: Processing batch interview emails...");
        List<Application> acceptedApplications = applicationRepository.findByStatusWithUsers(ApplicationStatus.ACCEPTED);
        if (acceptedApplications.isEmpty()) {
            System.out.println("No newly accepted applications found. Skipping batch.");
            return;
        }
        for (Application app : acceptedApplications) {
            User applicant = userRepository.findById(app.getUserId()).orElse(null);

            if (applicant != null && applicant.getEmail() != null) {
                sendEmailAsync(applicant.getEmail(), applicant.getUsername());
                app.setStatus(ApplicationStatus.NOTIFIED);
            }
        }
        applicationRepository.saveAll(acceptedApplications);
        System.out.println("✅ Batch processed instantly! Handed " + acceptedApplications.size() + " emails to background threads.");
    }

    @Async
    public void sendEmailAsync(String toEmail, String username) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("jobportal@gmail.com");
            message.setTo(toEmail);
            message.setSubject("Congratulations! Interview Invitation");
            message.setText("Dear " + username + ",\n\n" +
                    "We are thrilled to inform you that your application form has been reviewed and accepted by the employer!\n" +
                    "You are cordially invited for an interview. The HR team will contact you shortly to lock down the exact time.\n\n" +
                    "Best regards,\nJob Portal Team");
            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("Async email failed for: " + toEmail + " - " + e.getMessage());
        }
    }
}