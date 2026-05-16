package job_portal_backend.dto;

import job_portal_backend.entity.enums.ApplicationStatus;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class EmployerApplicationViewDto {
    private Long applicationId;
    private Long jobId;
    private Long applicantId;
    private String applicantName;
    private String applicantEmail;
    private String applicantPhone;
    private String cvFileUrl;
    private ApplicationStatus status;
    private LocalDateTime applyAt;
}