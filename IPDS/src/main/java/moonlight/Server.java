package moonlight;

import com.sun.net.httpserver.HttpServer;
import moonlight.db.Database;
import moonlight.handler.*;

import java.net.InetSocketAddress;

public class Server {
    private static final int PORT = 8080;

    public static void main(String[] args ) throws Exception {
        System.out.println("=================================");
        System.out.println("  Moonlight IPDS Server v1.0.0");
        System.out.println("=================================");

        // initialize database
        Database db = new Database();
        db.initialize();

        // create HTTP server
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);

        // register routes
        server.createContext("/api/nodes/register", new RegisterHandler(db));
        server.createContext("/api/nodes/heartbeat", new HeartbeatHandler(db));
        server.createContext("/api/nodes/leader", new LeaderHandler(db));
        server.createContext("/api/nodes", new NodesHandler(db));

        server.setExecutor(null);
        server.start();

        System.out.println("[IPDS] Server running on port " + PORT);
        System.out.println("[IPDS] Endpoints:");
        System.out.println("  GET  /api/nodes           - List all nodes");
        System.out.println("  GET  /api/nodes/{id}      - Get node by identifier");
        System.out.println("  POST /api/nodes/register  - Register a node");
        System.out.println("  POST /api/nodes/heartbeat - Send heartbeat");
        System.out.println("  GET  /api/nodes/leader    - Get current leader");
    }
}
