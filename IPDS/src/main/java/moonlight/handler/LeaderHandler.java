package moonlight.handler;

import com.sun.net.httpserver.HttpExchange;
import moonlight.db.Database;
import moonlight.model.Node;

public class LeaderHandler extends BaseHandler {
    private final Database db;

    public LeaderHandler(Database db ) {
        this.db = db;
    }

    @Override
    protected void processRequest(HttpExchange exchange) throws Exception {
        if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendResponse(exchange, 405, "{\"error\":\"Method not allowed\"}");
            return;
        }

        Node leader = db.getLeader();
        if (leader != null) {
            leader.setRole("LEADER");
            sendResponse(exchange, 200, leader.toJson());
        } else {
            sendResponse(exchange, 404, "{\"error\":\"No leader available\"}");
        }
    }
}
