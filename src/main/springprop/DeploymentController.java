package springprop;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class DeploymentController {

    private final DeploymentRepository deploymentRepository;

    public DeploymentController(DeploymentRepository deploymentRepository) {
        this.deploymentRepository = deploymentRepository;
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
