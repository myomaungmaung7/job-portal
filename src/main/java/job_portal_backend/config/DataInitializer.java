package job_portal_backend.config;

import jakarta.annotation.PostConstruct;
import job_portal_backend.entity.User;
import job_portal_backend.entity.enums.Role;
import job_portal_backend.entity.enums.UserStatus;
import job_portal_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class DataInitializer {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @PostConstruct
    public void init() {

        if (!userRepository.existsByEmail("admin@gmail.com")) {
            User admin = new User();
            admin.setUserName("Admin");
            admin.setEmail("admin@gmail.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole(Role.ADMIN);
            admin.setStatus(UserStatus.ACTIVE);
            admin.setCreatedAt(LocalDateTime.now());
            admin.setUpdatedAt(LocalDateTime.now());

            userRepository.save(admin);
        }
    }
}
