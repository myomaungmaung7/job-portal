package job_portal_backend.entity;

import jakarta.persistence.*;
import job_portal_backend.entity.enums.Role;
import job_portal_backend.entity.enums.UserStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "users")
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userName;
    private String email;
    private String password;
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Enumerated(EnumType.STRING)
    private UserStatus status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "user")
    private List<Application> applications;

    @OneToMany(mappedBy = "user")
    private List<SavedJob> savedJobs;
}
