package moonlight.handler;

import com.sun.net.httpserver.HttpExchange;
import moonlight.db.Database;

public class RegisterHandler extends BaseHandler {
    private final Database db;

    public RegisterHandler(Database db ) {
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
        String ipAddress = exchange.getRemoteAddress().getAddress().getHostAddress();
        int port = Integer.parseInt(extractJsonValue(body, "port", "8081"));
	int priority = Integer.parseInt(extractJsonValue(body, "leaderPriority", "1"));

        if (nodeIdentifier.isEmpty()) {
            sendResponse(exchange, 400, "{\"error\":\"nodeIdentifier is required\"}");
            return;
        }

        db.registerNode(nodeIdentifier, ipAddress, port, priority);
        sendResponse(exchange, 201, "{\"status\":\"registered\",\"nodeIdentifier\":\"" + nodeIdentifier + "\"}");
    }
}
