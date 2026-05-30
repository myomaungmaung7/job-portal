package job_portal_backend.repository;

import job_portal_backend.entity.Application;
import job_portal_backend.entity.enums.ApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {
    boolean existsByUserIdAndJobId(Long id, Long jobId);
    List<Application> findByJobId(Long jobId);
    List<Application> findByStatus(ApplicationStatus status);

}
