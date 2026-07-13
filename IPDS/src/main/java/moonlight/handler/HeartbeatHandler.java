package moonlight.handler;

import com.sun.net.httpserver.HttpExchange;
import moonlight.db.Database;

public class HeartbeatHandler extends BaseHandler {
    private final Database db;

    public HeartbeatHandler(Database db ) {
        this.db = db;
    }

    @Override
    protected void processRequest(HttpExchange exchange) throws Exception {
        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendResponse(exchange, 405, "{\"error\":\"Method not allowed\"}");
            return;
        }

        String body = readRequestBody(exchange);
        String nodeIdentifier = extractJsonValue(body, "nodeIdentifier");
        double uptime = Double.parseDouble(extractJsonValue(body, "uptimePercentage", "100.0"));

        if (nodeIdentifier.isEmpty()) {
            sendResponse(exchange, 400, "{\"error\":\"nodeIdentifier is required\"}");
            return;
        }

        db.updateHeartbeat(nodeIdentifier, uptime);
        sendResponse(exchange, 200, "{\"status\":\"heartbeat_received\",\"nodeIdentifier\":\"" + nodeIdentifier + "\"}");
    }
}
