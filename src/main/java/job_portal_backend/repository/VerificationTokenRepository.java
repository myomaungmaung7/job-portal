package job_portal_backend.repository;

import job_portal_backend.entity.User;
import job_portal_backend.entity.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {

    Optional<VerificationToken> findByUser(User user);

    Optional<VerificationToken> findByOtp(String otp);
    void deleteByUser(User user);
}
