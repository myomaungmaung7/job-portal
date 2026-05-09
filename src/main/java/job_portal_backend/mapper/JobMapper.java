package job_portal_backend.mapper;


import job_portal_backend.dto.JobRequestDto;
import job_portal_backend.entity.Job;
import job_portal_backend.entity.User;
import job_portal_backend.entity.enums.JobStatus;

import java.time.LocalDateTime;


public interface JobMapper {


    public static Job toEntity(JobRequestDto dto, User employer) {
        if (dto == null) {
            return null;
        }

        Job job = new Job();
        job.setJobType(dto.getJobType());
        job.setSalaryType(dto.getSalaryType());
        job.setSalaryAmount(dto.getSalaryAmount());
        job.setLocation(dto.getLocation());
        job.setJobDescription(dto.getJobDescription());
        job.setJobRequirement(dto.getJobRequirement());
        job.setEmployer(employer);
        job.setStatus(JobStatus.OPEN);
        job.setCreatedAt(LocalDateTime.now());


        return job;
    }
}
