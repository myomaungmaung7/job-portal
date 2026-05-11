package job_portal_backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Entity
@Table(name = "saved_job")
@Data
@EqualsAndHashCode(callSuper = true)
public class SavedJob extends BaseEntity {

    @Column(name = "save_at")
    private LocalDateTime saveAt = LocalDateTime.now();

    @NotNull
    @Column(name = "user_id")
    private Long userId;

    @NotNull
    @Column(name = "job_id")
    private Long jobId;
}
