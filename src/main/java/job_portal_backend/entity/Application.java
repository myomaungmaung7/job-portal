package job_portal_backend.entity;

import jakarta.persistence.*;
import job_portal_backend.entity.enums.ApplicationStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "application")
@Data
public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime applyAt;
    private String cvForm;

    @Enumerated(EnumType.STRING)
    private ApplicationStatus status;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "job_id")
    private Job job;
}
