package springprop;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class DeploymentController {

    private final List<DeploymentRecord> deployments = new ArrayList<>();

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
        return deployments;
    }

    @PostMapping("/deployments")
    public DeploymentRecord addDeployment(@RequestBody DeploymentRecord record) {
        deployments.add(record);
        return record;
    }
}
