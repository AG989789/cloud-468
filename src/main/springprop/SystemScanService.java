package springprop;

import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Service
public class SystemScanService {

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
                    "Scan target not allowed"
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
                details = installed ? "Detected" : "Not detected";
            }

            return new SystemScanResult(applicationName, scanTarget, installed, details);

        } catch (Exception e) {
            return new SystemScanResult(
                    applicationName,
                    scanTarget,
                    false,
                    "Error running scan"
            );
        }
    }

    private String[] getAllowedCommand(String scanTarget) {
        return switch (scanTarget.toLowerCase()) {
            case "google-chrome" -> new String[]{"google-chrome", "--version"};
            case "google-chrome-stable" -> new String[]{"google-chrome-stable", "--version"};
            case "chromium" -> new String[]{"chromium", "--version"};
            case "java" -> new String[]{"java", "--version"};
            case "python3" -> new String[]{"python3", "--version"};
            case "git" -> new String[]{"git", "--version"};
            case "node" -> new String[]{"node", "--version"};
            case "psql" -> new String[]{"psql", "--version"};
            default -> null;
        };
    }
}
