package job_portal_backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import job_portal_backend.entity.enums.JobStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "job")
@Data
@EqualsAndHashCode(callSuper = true)
public class Job extends BaseEntity {

    @NotBlank @Column(name = "job_type")
    private String jobType;

    @NotBlank @Column(name = "salary_type")
    private String salaryType;

    @NotNull @Positive
    @Column(name = "salary_amount")
    private Double salaryAmount;

    @NotBlank
    @Column(name = "location")
    private String location;

    @Lob @Column(name = "job_description", columnDefinition = "TEXT")
    private String jobDescription;

    @Lob @Column(name = "job_requirement", columnDefinition = "TEXT")
    private String jobRequirement;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private JobStatus status = JobStatus.OPEN;

    @NotNull
    @Column(name = "employer_id")
    private Long employerId;
}
