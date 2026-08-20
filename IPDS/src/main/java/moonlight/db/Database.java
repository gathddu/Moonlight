package moonlight.db;

import moonlight.model.Node;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Database {
    private final String url;
    private final String user;
    private final String password;

    public Database() {
        String host = System.getenv().getOrDefault("DB_HOST", "localhost");
        String port = System.getenv().getOrDefault("DB_PORT", "3306");
        String dbName = System.getenv().getOrDefault("DB_NAME", "ipds_db");
        this.user = System.getenv().getOrDefault("DB_USER", "root");
        this.password = System.getenv().getOrDefault("DB_PASSWORD", "labubuntu");
        this.url = "jdbc:mysql://" + host + ":" + port + "/" + dbName;
    }

    private Connection connect() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL driver not found", e);
        }
        return DriverManager.getConnection(url, user, password);
    }

    public void initialize() {
    	int maxRetries = 20;
        int delay = 3;

        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
                String createTable = """
                    CREATE TABLE IF NOT EXISTS nodes (
                        id VARCHAR(36) PRIMARY KEY,
                        node_identifier VARCHAR(255) UNIQUE NOT NULL,
                        ip_address VARCHAR(50) NOT NULL,
                        port INT NOT NULL,
                        status VARCHAR(20) DEFAULT 'ONLINE',
                        role VARCHAR(20) DEFAULT 'FOLLOWER',
                        uptime_percentage DOUBLE DEFAULT 100.0,
                        total_uptime BIGINT DEFAULT 0,
                        storage_capacity BIGINT DEFAULT 0,
                        storage_used BIGINT DEFAULT 0,
			leader_priority INT DEFAULT 1,
                        last_heartbeat TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        registered_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                    )
                    """;
                stmt.execute(createTable);
                System.out.println("[DB] Tables initialized");
                return;
            } catch (Exception e) {
                System.out.println("[DB] Waiting for database... attempt " + attempt + "/" + maxRetries);
                try {
                    Thread.sleep(delay * 1000);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }
        System.err.println("[DB] Failed to connect after " + maxRetries + " attempts");
    }


    public List<Node> getAllNodes() {
        List<Node> nodes = new ArrayList<>();
        String query = "SELECT * FROM nodes ORDER BY uptime_percentage DESC";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                nodes.add(mapResultSetToNode(rs));
            }
        } catch (SQLException e) {
            System.err.println("[DB] Error fetching nodes: " + e.getMessage());
        }
        return nodes;
    }

    public Node getNodeByIdentifier(String nodeIdentifier) {
        String query = "SELECT * FROM nodes WHERE node_identifier = ?";

        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, nodeIdentifier);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return mapResultSetToNode(rs);
            }
        } catch (SQLException e) {
            System.err.println("[DB] Error fetching node: " + e.getMessage());
        }
        return null;
    }

    public Node getLeader() {
        String query = "SELECT * FROM nodes WHERE status = 'ONLINE' ORDER BY leader_priority DESC, uptime_percentage DESC LIMIT 1";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            if (rs.next()) {
                return mapResultSetToNode(rs);
            }
        } catch (SQLException e) {
            System.err.println("[DB] Error fetching leader: " + e.getMessage());
        }
        return null;
    }

    public void registerNode(String nodeIdentifier, String ipAddress, int port, int priority) {
        String query = """
            INSERT INTO nodes (id, node_identifier, ip_address, port, status, role, uptime_percentage, leader_priority, last_heartbeat)
            VALUES (?, ?, ?, ?, 'ONLINE', 'FOLLOWER', 100.0, ?, NOW())
            ON DUPLICATE KEY UPDATE ip_address = ?, port = ?, leader_priority = ?, status = 'ONLINE', last_heartbeat = NOW()
            """;

        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, UUID.randomUUID().toString());
            ps.setString(2, nodeIdentifier);
            ps.setString(3, ipAddress);
            ps.setInt(4, port);
	    ps.setInt(5, priority);
            ps.setString(6, ipAddress);
            ps.setInt(7, port);
	    ps.setInt(8, priority);
            ps.execute();
            System.out.println("[DB] Node registered: " + nodeIdentifier);
        } catch (SQLException e) {
            System.err.println("[DB] Error registering node: " + e.getMessage());
        }
    }

    public void updateHeartbeat(String nodeIdentifier, double uptimePercentage) {
        String query = "UPDATE nodes SET last_heartbeat = NOW(), status = 'ONLINE', uptime_percentage = ? WHERE node_identifier = ?";

        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setDouble(1, uptimePercentage);
            ps.setString(2, nodeIdentifier);
            ps.execute();
        } catch (SQLException e) {
            System.err.println("[DB] Error updating heartbeat: " + e.getMessage());
        }
    }

    private Node mapResultSetToNode(ResultSet rs) throws SQLException {
        Node node = new Node();
        node.setId(rs.getString("id"));
        node.setNodeIdentifier(rs.getString("node_identifier"));
        node.setIpAddress(rs.getString("ip_address"));
        node.setPort(rs.getInt("port"));
        node.setStatus(rs.getString("status"));
        node.setRole(rs.getString("role"));
        node.setUptimePercentage(rs.getDouble("uptime_percentage"));
        node.setTotalUptime(rs.getLong("total_uptime"));
        node.setStorageCapacity(rs.getLong("storage_capacity"));
        node.setStorageUsed(rs.getLong("storage_used"));

        Timestamp lastHeartbeat = rs.getTimestamp("last_heartbeat");
        if (lastHeartbeat != null) node.setLastHeartbeat(lastHeartbeat.toLocalDateTime());

        Timestamp registeredAt = rs.getTimestamp("registered_at");
        if (registeredAt != null) node.setRegisteredAt(registeredAt.toLocalDateTime());

        return node;
    }
}
