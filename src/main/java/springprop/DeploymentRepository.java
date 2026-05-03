package springprop;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DeploymentRepository extends JpaRepository<DeploymentRecord, Long> {
    Optional<DeploymentRecord> findByApplicationNameAndEnvironment(String applicationName, String environment);
}
