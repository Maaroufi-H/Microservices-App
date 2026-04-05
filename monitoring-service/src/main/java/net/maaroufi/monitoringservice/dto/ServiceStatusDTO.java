package net.maaroufi.monitoringservice.dto;

/**
 * Health status of a microservice, derived from its actuator/health endpoint.
 */
public class ServiceStatusDTO {

    private String serviceName;
    private String status;   // "UP", "DOWN", "UNKNOWN"
    private String port;

    public ServiceStatusDTO() {}

    public ServiceStatusDTO(String serviceName, String status, String port) {
        this.serviceName = serviceName;
        this.status = status;
        this.port = port;
    }

    public String getServiceName() { return serviceName; }
    public void setServiceName(String serviceName) { this.serviceName = serviceName; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getPort() { return port; }
    public void setPort(String port) { this.port = port; }
}
