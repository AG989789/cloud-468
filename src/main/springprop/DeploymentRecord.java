package springprop;

public class DeploymentRecord {
    private String applicationName;
    private String version;
    private String environment;
    private String status;

    public DeploymentRecord() {
    }

    public DeploymentRecord(String applicationName, String version, String environment, String status) {
        this.applicationName = applicationName;
        this.version = version;
        this.environment = environment;
        this.status = status;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
