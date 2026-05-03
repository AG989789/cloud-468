package springprop;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SystemScanService {

    private final Map<String, String[]> allowedCommands = new HashMap<>();

    @PostConstruct
    public void loadApprovedScanTargets() {
        try {
            ClassPathResource resource = new ClassPathResource("approved-scan-targets.csv");

            if (!resource.exists()) {
                System.out.println("approved-scan-targets.csv not found. No scan targets loaded.");
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

                    if (parts.length < 3) {
                        System.out.println("Skipping invalid scan target line: " + line);
                        continue;
                    }

                    String scanTarget = parts[0].trim().toLowerCase();
                    String command = parts[1].trim();
                    String argument = parts[2].trim();

                    allowedCommands.put(scanTarget, new String[]{command, argument});
                }
            }

            System.out.println("Loaded approved scan targets: " + allowedCommands.keySet());

        } catch (Exception e) {
            System.out.println("Error loading approved scan targets: " + e.getMessage());
        }
    }

    public List<SystemScanResult> scanDeployments(List<DeploymentRecord> records) {
        List<SystemScanResult> results = new ArrayList<>();

        for (DeploymentRecord record : records) {
            String scanTarget = record.getScanTarget();

            if (scanTarget == null || scanTarget.isBlank()) {
                results.add(new SystemScanResult(
                        record.getApplicationName(),
                        "",
                        false,
                        "No scan target provided"
                ));
                continue;
            }

            results.add(runSafeScan(record.getApplicationName(), scanTarget));
        }

        return results;
    }

    private SystemScanResult runSafeScan(String applicationName, String scanTarget) {
        String[] command = getAllowedCommand(scanTarget);

        if (command == null) {
            return new SystemScanResult(
                    applicationName,
                    scanTarget,
                    false,
                    "Scan target not approved"
            );
        }

        try {
            Process process = new ProcessBuilder(command)
                    .redirectErrorStream(true)
                    .start();

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream())
            );

            StringBuilder output = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                output.append(line).append(" ");
            }

            int exitCode = process.waitFor();
            boolean installed = exitCode == 0;

            String details = output.toString().trim();
            if (details.isBlank()) {
                details = installed
                        ? "Detected in current runtime environment"
                        : "Not detected in current runtime environment";
            }

            return new SystemScanResult(applicationName, scanTarget, installed, details);

        } catch (Exception e) {
            return new SystemScanResult(
                    applicationName,
                    scanTarget,
                    false,
                    "Command not found in current runtime environment"
            );
        }
    }

    private String[] getAllowedCommand(String scanTarget) {
        if (scanTarget == null) {
            return null;
        }

        return allowedCommands.get(scanTarget.toLowerCase());
    }
}
