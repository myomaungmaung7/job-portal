package job_portal_backend.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class ProfileRequestDto {

    private String profileImage;
    private String nrcFront;
    private String nrcBack;

    private MultipartFile profileFile;
    private MultipartFile nrcFrontFile;
    private MultipartFile nrcBackFile;

    private String businessName;
    private String location;
    private String companyBackground;
    private String crn;
    private String skill;
    private Integer age;
    private String address;
    private String educationBackground;
    private String experience;


}
