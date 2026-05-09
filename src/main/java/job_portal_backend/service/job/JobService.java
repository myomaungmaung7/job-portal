package job_portal_backend.service.job;

import job_portal_backend.dto.JobRequestDto;
import job_portal_backend.entity.User;
import job_portal_backend.response.ApiResponse;

public interface JobService {
    ApiResponse postNewJob(User user, JobRequestDto jobRequestDto);
    ApiResponse updateJob(Long jobId,User user, JobRequestDto jobRequestDto);

}
