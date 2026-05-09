package job_portal_backend.dto;

import lombok.Data;

@Data
public class ProfileRequestDto {

    private String userName;
    private String phoneNumber;

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
