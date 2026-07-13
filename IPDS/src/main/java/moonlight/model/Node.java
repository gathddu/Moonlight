package moonlight.model;

import java.time.LocalDateTime;

public class Node {
    private String id;
    private String nodeIdentifier;
    private String ipAddress;
    private int port;
    private String status;
    private String role;
    private double uptimePercentage;
    private long totalUptime;
    private long storageCapacity;
    private long storageUsed;
    private LocalDateTime lastHeartbeat;
    private LocalDateTime registeredAt;

    public Node() {}

    // getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNodeIdentifier() { return nodeIdentifier; }
    public void setNodeIdentifier(String nodeIdentifier) { this.nodeIdentifier = nodeIdentifier; }

    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }

    public int getPort() { return port; }
    public void setPort(int port) { this.port = port; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public double getUptimePercentage() { return uptimePercentage; }
    public void setUptimePercentage(double uptimePercentage) { this.uptimePercentage = uptimePercentage; }

    public long getTotalUptime() { return totalUptime; }
    public void setTotalUptime(long totalUptime) { this.totalUptime = totalUptime; }

    public long getStorageCapacity() { return storageCapacity; }
    public void setStorageCapacity(long storageCapacity) { this.storageCapacity = storageCapacity; }

    public long getStorageUsed() { return storageUsed; }
    public void setStorageUsed(long storageUsed) { this.storageUsed = storageUsed; }

    public LocalDateTime getLastHeartbeat() { return lastHeartbeat; }
    public void setLastHeartbeat(LocalDateTime lastHeartbeat) { this.lastHeartbeat = lastHeartbeat; }

    public LocalDateTime getRegisteredAt() { return registeredAt; }
    public void setRegisteredAt(LocalDateTime registeredAt) { this.registeredAt = registeredAt; }

    public String toJson() {
        return String.format(
            "{\"id\":\"%s\",\"nodeIdentifier\":\"%s\",\"ipAddress\":\"%s\",\"port\":%d," +
            "\"status\":\"%s\",\"role\":\"%s\",\"uptimePercentage\":%.2f,\"totalUptime\":%d," +
            "\"storageCapacity\":%d,\"storageUsed\":%d,\"lastHeartbeat\":\"%s\",\"registeredAt\":\"%s\"}",
            id, nodeIdentifier, ipAddress, port, status, role,
            uptimePercentage, totalUptime, storageCapacity, storageUsed,
            lastHeartbeat != null ? lastHeartbeat.toString() : "",
            registeredAt != null ? registeredAt.toString() : ""
        );
    }
}
