package job_portal_backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "profile")
@Data
@EqualsAndHashCode(callSuper = true)
public class Profile extends BaseEntity {

    @Column(name = "profile_image")
    private String profileImage;
    @Column(name = "business_name")
    private String businessName;
    private String location;

    @Column(columnDefinition = "TEXT")
    private String companyBackground;
    private String crn;
    private String skill;
    private Integer age;
    private String address;

    @Column(columnDefinition = "TEXT")
    private String educationBackground;
    @Column(columnDefinition = "TEXT")
    private String experience;

    private String nrcFront;
    private String nrcBack;

    @NotNull
    @Column(name = "user_id", unique = true)
    private Long userId;
}
