package job_portal_backend.service.job;

import job_portal_backend.dto.JobRequestDto;
import job_portal_backend.entity.Job;
import job_portal_backend.entity.User;
import job_portal_backend.entity.enums.Role;
import job_portal_backend.entity.enums.UserStatus;
import job_portal_backend.mapper.JobMapper;
import job_portal_backend.repository.JobRepository;
import job_portal_backend.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class JobServiceImpl implements JobService {

   private final JobRepository jobRepository;
    @Override
    public ApiResponse postNewJob(User user, JobRequestDto jobRequestDto) {


        if (user.getRole() != Role.EMPLOYER) {
            return ApiResponse.builder()
                    .success(0)
                    .code(403)
                    .message("Only employers can post jobs")
                    .build();
        }

        if (user.getStatus() != UserStatus.ACTIVE) {
            return ApiResponse.builder()
                    .success(0)
                    .code(403)
                    .message("Account is " + user.getStatus())
                    .build();
        }

        Job job = JobMapper.toEntity(jobRequestDto, user);
        jobRepository.save(job);

        return ApiResponse.builder()
                .success(1)
                .code(201)
                .message("Job posted successfully")
                .build();
    }

    @Override
    public ApiResponse updateJob(Long jobId, User user, JobRequestDto jobRequestDto) {
        Job job = jobRepository.findById(jobId)
                .orElse(null);

        if (job == null) {
            return ApiResponse.builder().success(0).code(404).message("Job not found").build();
        }

        if (!job.getEmployer().getId().equals(user.getId())) {
            return ApiResponse.builder().success(0).code(403).message("You do not have permission to update this job").build();
        }
       if(jobRequestDto.getJobType()!= null) job.setJobType(jobRequestDto.getJobType());
        if(jobRequestDto.getSalaryType()!= null)  job.setSalaryType(jobRequestDto.getSalaryType());
        if(jobRequestDto.getSalaryAmount()!= null) job.setSalaryAmount(jobRequestDto.getSalaryAmount());
        if(jobRequestDto.getLocation()!= null)job.setLocation(jobRequestDto.getLocation());
        if(jobRequestDto.getJobDescription()!= null)   job.setJobDescription(jobRequestDto.getJobDescription());
        if(jobRequestDto.getJobRequirement()!= null)  job.setJobRequirement(jobRequestDto.getJobRequirement());
        job.setUpdatedAt(LocalDateTime.now());

        jobRepository.save(job);

        return ApiResponse.builder()
                .success(1)
                .code(200)
                .message("Job updated successfully")
                .build();
    }
}
