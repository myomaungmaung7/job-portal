package job_portal_backend.service.application;

import job_portal_backend.dto.EmployerApplicationViewDto;
import job_portal_backend.entity.Application;
import job_portal_backend.entity.User;
import job_portal_backend.entity.Job;
import job_portal_backend.entity.enums.ApplicationStatus;
import job_portal_backend.entity.enums.Role;
import job_portal_backend.entity.enums.UserStatus;
import job_portal_backend.repository.ApplicationRepository;
import job_portal_backend.repository.JobRepository;
import job_portal_backend.repository.UserRepository;
import job_portal_backend.response.ApiResponse;
import job_portal_backend.util.FileUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ApplicationServiceImpl implements ApplicationService {

    @Value("${app.file.path.cvForm.absolutePath}")
    private String cvAbs;

    @Value("${app.file.path.cvForm.relativePath}")
    private String cvRel;

    private final ApplicationRepository applicationRepository;
    private final FileUtil fileUtil;
    private final UserRepository userRepository;
    private final JobRepository jobRepository;

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
    @Override
    @Transactional
    public ApiResponse getAllApplicationsForJob(Long jobId, User loggedInUser) {

        var job = jobRepository.findById(jobId).orElse(null);
        if (job == null) {
            return ApiResponse.builder().success(0).code(404).message("Job not found").build();
        }
        if (!job.getEmployerId().equals(loggedInUser.getId())) {
            return ApiResponse.builder().success(0).code(403).message("You do not own this job listing").build();
        }

        List<Application> applications = applicationRepository.findByJobId(jobId);

        boolean statusChanged = false;
        for (Application app : applications) {
            if (app.getStatus() == ApplicationStatus.PENDING) {
                app.setStatus(ApplicationStatus.RECEIVED);
                statusChanged = true;
            }
        }

        // 2. Quietly commit changes to the database
        if (statusChanged) {
            applicationRepository.saveAllAndFlush(applications);
        }

        List<EmployerApplicationViewDto> dtoList = applications.stream().map(app -> {
            User applicant = userRepository.findById(app.getUserId()).orElse(null);
            return EmployerApplicationViewDto.builder()
                    .applicationId(app.getId())
                    .jobId(app.getJobId())
                    .applicantId(app.getUserId())
                    .applicantName(applicant != null ? applicant.getUsername() : "Unknown")
                    .applicantEmail(applicant != null ? applicant.getEmail() : "Unknown")
                    .applicantPhone(applicant != null ? applicant.getPhoneNumber() : "Unknown")
                    .cvFileUrl(app.getCvForm())
                    .status(app.getStatus())
                    .applyAt(app.getApplyAt())
                    .build();
        }).toList();

        return ApiResponse.builder()
                .success(1)
                .code(200)
                .data(dtoList)
                .message("Applications retrieved successfully")
                .build();
    }

    @Override
    @Transactional // Standard transactional since we are updating the single row's status
    public ApiResponse getApplicationFormById(Long applicationId, User loggedInUser) {

        // 1. Find the specific application form by its ID
        Application app = applicationRepository.findById(applicationId).orElse(null);
        if (app == null) {
            return ApiResponse.builder().success(0).code(404).message("Application form not found").build();
        }

        // 2. Fetch the associated job listing to verify ownership
        var job = jobRepository.findById(app.getJobId()).orElse(null);
        if (job == null) {
            return ApiResponse.builder().success(0).code(404).message("Associated job listing not found").build();
        }

        // 3. Security Check: Make sure the logged-in Employer owns the job this application belongs to
        if (!job.getEmployerId().equals(loggedInUser.getId())) {
            return ApiResponse.builder().success(0).code(403).message("You do not own the job listing for this application").build();
        }

        // 4. Automation Step: If it was PENDING or RECEIVED, change it to VIEWED
        if (app.getStatus() == ApplicationStatus.RECEIVED) {
            app.setStatus(ApplicationStatus.VIEWED);
            applicationRepository.save(app); // Quietly save just this row
        }

        // 5. Fetch the applicant's account details to build the profile view
        User applicant = userRepository.findById(app.getUserId()).orElse(null);

        // 6. Map the single form details into your existing View DTO
        EmployerApplicationViewDto dto = EmployerApplicationViewDto.builder()
                .applicationId(app.getId())
                .jobId(app.getJobId())
                .applicantId(app.getUserId())
                .applicantName(applicant != null ? applicant.getUsername() : "Unknown")
                .applicantEmail(applicant != null ? applicant.getEmail() : "Unknown")
                .applicantPhone(applicant != null ? applicant.getPhoneNumber() : "Unknown")
                .cvFileUrl(app.getCvForm())
                .status(app.getStatus()) // This will cleanly display "VIEWED"
                .applyAt(app.getApplyAt())
                .build();

        return ApiResponse.builder()
                .success(1)
                .code(200)
                .data(dto) // Returns just this single object, not a list!
                .message("Application form retrieved and marked as VIEWED")
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public Resource downloadCvFile(Long applicationId, User loggedInUser) {


        if (loggedInUser.getStatus() != UserStatus.ACTIVE) {
            throw new RuntimeException("Access Denied: Your account status is " + loggedInUser.getStatus());
        }

        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application record with ID " + applicationId + " not found."));

        Job job = jobRepository.findById(application.getJobId())
                .orElseThrow(() -> new RuntimeException("The job listing associated with this application no longer exists."));


        if (!job.getEmployerId().equals(loggedInUser.getId())) {
            throw new RuntimeException("Access Denied: You do not have permission to access files for this job post.");
        }

        try {

            String dbPath = application.getCvForm();
            if (dbPath == null || dbPath.isEmpty()) {
                throw new RuntimeException("This application record does not contain a valid CV file path link.");
            }
            String fileName = dbPath.substring(dbPath.lastIndexOf("/") + 1);


            Path filePath = Paths.get(cvAbs).resolve(fileName).normalize();
            System.out.println("👉 JAVA IS SEARCHING IN THIS EXACT PATH: " + filePath.toAbsolutePath());
            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                throw new RuntimeException("The requested CV document does not physically exist on the server disk.");
            }

            return resource;

        } catch (Exception ex) {
            throw new RuntimeException("An internal error occurred while parsing the secure file stream: " + ex.getMessage());
        }
    }
    @Override
    @Transactional
    public ApiResponse acceptApplication(Long applicationId, User loggedInUser) {

        // 1. Fetch the targeted application record
        Application application = applicationRepository.findById(applicationId).orElse(null);
        if (application == null) {
            return ApiResponse.builder().success(0).code(404).message("Application not found").build();
        }

        // 2. Fetch the linked job to perform the ownership check
        var job = jobRepository.findById(application.getJobId()).orElse(null);
        if (job == null) {
            return ApiResponse.builder().success(0).code(404).message("Associated job listing not found").build();
        }

        // 3. Security: Ensure the logged-in Employer owns this job post
        if (!job.getEmployerId().equals(loggedInUser.getId())) {
            return ApiResponse.builder().success(0).code(403).message("You do not have permission to update this application").build();
        }

        // 4. Update the status to ACCEPTED
        // (Ensure ApplicationStatus.ACCEPTED matches your exact enum name)
        application.setStatus(ApplicationStatus.ACCEPTED);
        applicationRepository.save(application);

        return ApiResponse.builder()
                .success(1)
                .code(200)
                .message("Application has been accepted successfully")
                .build();
    }


}
