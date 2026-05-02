package springprop;

public class SystemScanResult {

    private String applicationName;
    private String scanTarget;
    private boolean installed;
    private String details;

    public SystemScanResult() {
    }

    public SystemScanResult(String applicationName, String scanTarget, boolean installed, String details) {
        this.applicationName = applicationName;
        this.scanTarget = scanTarget;
        this.installed = installed;
        this.details = details;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public String getScanTarget() {
        return scanTarget;
    }

    public void setScanTarget(String scanTarget) {
        this.scanTarget = scanTarget;
    }

    public boolean isInstalled() {
        return installed;
    }

    public void setInstalled(boolean installed) {
        this.installed = installed;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}
