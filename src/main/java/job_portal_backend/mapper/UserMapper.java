package job_portal_backend.mapper;

import job_portal_backend.dto.RegisterRequest;
import job_portal_backend.entity.User;
import job_portal_backend.entity.enums.Role;
import job_portal_backend.entity.enums.UserStatus;

import java.time.LocalDateTime;

public class UserMapper {

    public static User toEntity(RegisterRequest request, String encodedPassword) {
        User user = new User();
        user.setUserName(request.getUserName());
        user.setEmail(request.getEmail());
        user.setPassword(encodedPassword);
        user.setPhoneNumber(request.getPhoneNumber());

        Role role = request.getRole() == null ? Role.EMPLOYEE : request.getRole();
        user.setRole(role);

        user.setStatus(UserStatus.ACTIVE);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        return user;
    }
}
