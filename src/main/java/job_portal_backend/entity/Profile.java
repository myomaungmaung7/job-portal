package job_portal_backend.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "profile")
@Data
public class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String profileImage;
    private String businessName;
    private String location;
    private String companyBackground;
    private String crn;

    private String skill;
    private Integer age;
    private String address;

    private String educationBackground;
    private String experience;

    private String nrcFront;
    private String nrcBack;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
}
