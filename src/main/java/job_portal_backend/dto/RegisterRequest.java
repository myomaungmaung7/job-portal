package job_portal_backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import job_portal_backend.entity.enums.Role;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotBlank
    private String userName;

    @Email
    private String email;

    private String phoneNumber;

    @NotBlank
    private String password;

    private Role role;
}
