package springprop;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class SystemScanController {

    private final DeploymentRepository deploymentRepository;
    private final SystemScanService systemScanService;

    public SystemScanController(DeploymentRepository deploymentRepository, SystemScanService systemScanService) {
        this.deploymentRepository = deploymentRepository;
        this.systemScanService = systemScanService;
    }

    @GetMapping("/scan")
    public List<SystemScanResult> scanCurrentSystem() {
        List<DeploymentRecord> records = deploymentRepository.findAll();
        return systemScanService.scanDeployments(records);
    }
}
