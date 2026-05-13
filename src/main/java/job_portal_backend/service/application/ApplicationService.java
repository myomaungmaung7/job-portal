package job_portal_backend.service.application;

import job_portal_backend.entity.User;
import job_portal_backend.response.ApiResponse;
import org.springframework.web.multipart.MultipartFile;

public interface ApplicationService {
    ApiResponse applyJob(Long jobId, MultipartFile cvFile, User loggedInUser);
}
