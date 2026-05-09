package job_portal_backend.dto;

import lombok.Data;

@Data
public class JobRequestDto {

    private String jobType;
    private String salaryType;
    private Double salaryAmount;
    private String location;
    private String jobDescription;
    private String jobRequirement;

}
