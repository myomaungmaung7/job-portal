package job_portal_backend.service.application;

import job_portal_backend.entity.Application;
import job_portal_backend.entity.User;
import job_portal_backend.entity.enums.ApplicationStatus;
import job_portal_backend.entity.enums.Role;
import job_portal_backend.entity.enums.UserStatus;
import job_portal_backend.repository.ApplicationRepository;
import job_portal_backend.response.ApiResponse;
import job_portal_backend.util.FileUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ApplicationServiceImpl implements ApplicationService {

    @Value("${app.file.path.cvForm.absolutePath}")
    private String cvAbs;

    @Value("${app.file.path.cvForm.relativePath}")
    private String cvRel;

    private final ApplicationRepository applicationRepository;
    private final FileUtil fileUtil;

    @Override
    @Transactional
    public ApiResponse applyJob(Long jobId, MultipartFile cvFile, User loggedInUser) {

        if (loggedInUser.getRole() != Role.EMPLOYEE) {
            return ApiResponse.builder()
                    .success(0)
                    .code(403)
                    .message("Only employees can apply for jobs")
                    .build();
        }

        if (loggedInUser.getStatus() != UserStatus.ACTIVE) {
            return ApiResponse.builder()
                    .success(0)
                    .code(403)
                    .message("Your account is " + loggedInUser.getStatus())
                    .build();
        }

        if (applicationRepository.existsByUserIdAndJobId(loggedInUser.getId(), jobId)) {
            return ApiResponse.builder()
                    .success(0)
                    .code(403)
                    .message("You have already applied for this job.")
                    .build();
        }

        try {
            Application application = new Application();
            application.setUserId(loggedInUser.getId());
            application.setJobId(jobId);
            application.setApplyAt(LocalDateTime.now());
            application.setStatus(ApplicationStatus.PENDING);

            if (cvFile != null && !cvFile.isEmpty()) {
                if (!cvFile.getContentType().equalsIgnoreCase("application/pdf")) {
                    return ApiResponse.builder().success(0).code(400).message("Only PDF files are allowed").build();
                }

                String filePath = fileUtil.writeMediaFile(cvFile, cvAbs, cvRel);
                application.setCvForm(filePath);
            } else {
                return ApiResponse.builder().success(0).code(400).message("CV file is required").build();
            }

            applicationRepository.save(application);

            return ApiResponse.builder()
                    .success(1)
                    .code(201)
                    .message("Job apply submitted successfully")
                    .build();
        } catch (IOException ex) {
            throw new RuntimeException("Error uploading CV file", ex);
        }
    }
}
