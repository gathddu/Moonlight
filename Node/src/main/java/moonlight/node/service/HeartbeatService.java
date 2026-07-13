package moonlight.node.service;

import java.time.Instant;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class HeartbeatService {
    private final IpdsClient ipds;
    private final String nodeId;
    private final ScheduledExecutorService scheduler;
    private final Instant startTime;

    public HeartbeatService(IpdsClient ipds, String nodeId) {
        this.ipds = ipds;
        this.nodeId = nodeId;
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
        this.startTime = Instant.now();
    }

    public void start() {
        scheduler.scheduleAtFixedRate(this::sendHeartbeat, 30, 30, TimeUnit.SECONDS);
        System.out.println("[Heartbeat] Service started (interval: 30s)");
    }

    public void stop() {
        scheduler.shutdown();
        System.out.println("[Heartbeat] Service stopped");
    }

    private void sendHeartbeat() {
        long uptimeSeconds = Instant.now().getEpochSecond() - startTime.getEpochSecond();
        // simple uptime percentage: assume 100% since we're online right now
        // this would track disconnections over time
        double uptimePercentage = 100.0;

        boolean success = ipds.sendHeartbeat(nodeId, uptimePercentage);
        if (success) {
            System.out.println("[Heartbeat] Sent (uptime: " + uptimeSeconds + "s)");
        }
    }
}
