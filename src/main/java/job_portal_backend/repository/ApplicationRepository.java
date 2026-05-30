package job_portal_backend.repository;

import job_portal_backend.entity.Application;
import job_portal_backend.entity.enums.ApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {
    boolean existsByUserIdAndJobId(Long id, Long jobId);
    List<Application> findByJobId(Long jobId);
    @Query("SELECT a FROM Application a JOIN User u ON a.userId = u.id WHERE a.status = :status")
    List<Application> findByStatusWithUsers(@Param("status") ApplicationStatus status);

}
