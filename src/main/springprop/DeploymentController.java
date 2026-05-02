package springprop;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class DeploymentController {

    private final DeploymentRepository deploymentRepository;

    public DeploymentController(DeploymentRepository deploymentRepository) {
        this.deploymentRepository = deploymentRepository;
    }

    @GetMapping("/")
    public String home() {
        return "Software Deployment Registry is running.";
    }

    @GetMapping("/health")
    public String health() {
        return "OK";
    }

    @GetMapping("/deployments")
    public List<DeploymentRecord> getDeployments() {
        return deploymentRepository.findAll();
    }

    @PostMapping("/deployments")
    public DeploymentRecord addDeployment(@RequestBody DeploymentRecord record) {
        return deploymentRepository.save(record);
    }
}
