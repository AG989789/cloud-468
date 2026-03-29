package springprop;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DeploymentController {

    @GetMapping("/")
    public String home() {
        return "Software Deployment Registry is running.";
    }

    @GetMapping("/health")
    public String health() {
        return "OK";
    }
}
