package job_portal_backend.entity;

import jakarta.persistence.*;
import job_portal_backend.entity.enums.JobStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "job")
@Data
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String jobType;
    private String salaryType;
    private Double salaryAmount;
    private String location;

    @Column(columnDefinition = "TEXT")
    private String jobDescription;

    @Column(columnDefinition = "TEXT")
    private String jobRequirement;

    @Enumerated(EnumType.STRING)
    private JobStatus status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "employer_id")
    private User employer;

    @OneToMany(mappedBy = "job")
    private List<Application> applications;
}
