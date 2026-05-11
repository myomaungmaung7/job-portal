package job_portal_backend.repository;

import job_portal_backend.entity.User;
import job_portal_backend.entity.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {

    Optional<VerificationToken> findByUserId(Long userId);

    Optional<VerificationToken> findByOtp(String otp);

    @Modifying
    @Transactional
    void deleteByUserId(Long id);
}
