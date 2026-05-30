package job_portal_backend.service.scheduler;

import job_portal_backend.entity.Application;
import job_portal_backend.entity.User;
import job_portal_backend.entity.enums.ApplicationStatus;
import job_portal_backend.repository.ApplicationRepository;
import job_portal_backend.repository.UserRepository;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
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
        List<Application> acceptedApplications = applicationRepository.findByStatus(ApplicationStatus.ACCEPTED);

        if (acceptedApplications.isEmpty()) {
            System.out.println("No newly accepted applications found. Skipping batch.");
            return;
        }

        for (Application app : acceptedApplications) {
            try {

                User applicant = userRepository.findById(app.getUserId()).orElse(null);

                if (applicant != null && applicant.getEmail() != null) {

                    SimpleMailMessage message = new SimpleMailMessage();
                    message.setFrom("jobportal@gmail.com");
                    message.setTo(applicant.getEmail());
                    message.setSubject("Congratulations! Interview Invitation");
                    message.setText("Dear " + applicant.getUsername() + ",\n\n" +
                            "We are thrilled to inform you that your application form has been reviewed and accepted by the employer!\n" +
                            "You are cordially invited for an interview. The HR team will contact you shortly to lock down the exact time.\n\n" +
                            "Best regards,\nJob Portal Team");

                    mailSender.send(message);
                    app.setStatus(ApplicationStatus.NOTIFIED);
                }
            } catch (Exception e) {
                System.err.println("Failed to send interview email for Application ID: " + app.getId() + " - " + e.getMessage());
            }
        }

        applicationRepository.saveAll(acceptedApplications);
        System.out.println("✅ Batch completed successfully! Notified " + acceptedApplications.size() + " candidates.");
    }
}