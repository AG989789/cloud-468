package springprop;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@Component
public class PresetDeploymentLoader implements CommandLineRunner {

    private final DeploymentRepository deploymentRepository;

    public PresetDeploymentLoader(DeploymentRepository deploymentRepository) {
        this.deploymentRepository = deploymentRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        ClassPathResource resource = new ClassPathResource("preset-deployments.csv");

        if (!resource.exists()) {
            System.out.println("Preset deployment file not found. Skipping preload.");
            return;
        }

        try (InputStream inputStream = resource.getInputStream();
             BufferedReader reader = new BufferedReader(
                     new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {

            String line;
            boolean isHeader = true;

            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }

                if (isHeader) {
                    isHeader = false;
                    continue;
                }

                String[] parts = line.split(",", -1);

                if (parts.length < 5) {
                    System.out.println("Skipping invalid preset line: " + line);
                    continue;
                }

                String applicationName = parts[0].trim();
                String version = parts[1].trim();
                String environment = parts[2].trim();
                String status = parts[3].trim();
                String scanTarget = parts[4].trim();

                boolean alreadyExists = deploymentRepository
                        .findByApplicationNameAndEnvironment(applicationName, environment)
                        .isPresent();

                if (!alreadyExists) {
                    DeploymentRecord record = new DeploymentRecord();
                    record.setApplicationName(applicationName);
                    record.setVersion(version);
                    record.setEnvironment(environment);
                    record.setStatus(status);
                    record.setScanTarget(scanTarget);

                    deploymentRepository.save(record);
                    System.out.println("Loaded preset deployment: " + applicationName);
                }
            }
        }
    }
}
