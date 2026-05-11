package job_portal_backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import job_portal_backend.entity.enums.ApplicationStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Entity
@Table(name = "application")
@Data
@EqualsAndHashCode(callSuper = true)
public class Application extends BaseEntity {

    @Column(name = "apply_at")
    private LocalDateTime applyAt = LocalDateTime.now();

    @Column(name = "cv_form")
    private String cvForm;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ApplicationStatus status;

    @NotNull
    @Column(name = "user_id")
    private Long userId;

    @NotNull @Column(name = "job_id")
    private Long jobId;
}
