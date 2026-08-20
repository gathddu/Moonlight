package moonlight.node;

import moonlight.node.service.IpdsClient;
import moonlight.node.service.HeartbeatService;
import moonlight.node.sync.SyncService;

public class NodeApp {
    private static final String NODE_ID = System.getenv().getOrDefault("NODE_ID", "node-" + System.currentTimeMillis());
    private static final String IPDS_URL = System.getenv().getOrDefault("IPDS_URL", "http://localhost:8080" );
    private static final int NODE_PORT = Integer.parseInt(System.getenv().getOrDefault("NODE_PORT", "8081"));
    private static final int LEADER_PRIORITY = Integer.parseInt(System.getenv().getOrDefault("LEADER_PRIORITY", "1"));

    public static void main(String[] args) {
        System.out.println("=================================");
        System.out.println("  Moonlight Node: " + NODE_ID);
        System.out.println("=================================");

        // register with IPDS
        IpdsClient ipds = new IpdsClient(IPDS_URL);
        registerWithRetry(ipds);

        // start heartbeat (sends pulse to IPDS every 30 seconds)
        HeartbeatService heartbeat = new HeartbeatService(ipds, NODE_ID);
        heartbeat.start();

        // sync service (watches for file changes)
        SyncService sync = new SyncService(NODE_ID, ipds);
        sync.start();

        System.out.println("[Node] All services running.");

        // keep main thread alive
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("[Node] Shutting down...");
            heartbeat.stop();
            sync.stop();
        }));

        try {
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private static void registerWithRetry(IpdsClient ipds) {
        int maxRetries = 10;
        int delay = 5;

        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            boolean registered = ipds.register(NODE_ID, NODE_PORT, LEADER_PRIORITY);
            if (registered) {
                System.out.println("[Node] Registered with IPDS.");
                return;
            }
            System.out.println("[Node] Waiting for IPDS... attempt " + attempt + "/" + maxRetries);
            try {
                Thread.sleep(delay * 1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
        System.err.println("[Node] Could not register after " + maxRetries + " attempts. Running in offline mode.");
    }
}
