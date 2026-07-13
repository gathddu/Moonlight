package moonlight.handler;

import com.sun.net.httpserver.HttpExchange;
import moonlight.db.Database;
import moonlight.model.Node;

import java.util.List;
import java.util.stream.Collectors;

public class NodesHandler extends BaseHandler {
    private final Database db;

    public NodesHandler(Database db ) {
        this.db = db;
    }

    @Override
    protected void processRequest(HttpExchange exchange) throws Exception {
        if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendResponse(exchange, 405, "{\"error\":\"Method not allowed\"}");
            return;
        }

        // Check if a specific node is requested: /api/nodes/{identifier}
        String path = exchange.getRequestURI().getPath();
        String[] parts = path.split("/");

        if (parts.length > 3) {
            // /api/nodes/{identifier}
            String identifier = parts[3];
            Node node = db.getNodeByIdentifier(identifier);
            if (node != null) {
                sendResponse(exchange, 200, node.toJson());
            } else {
                sendResponse(exchange, 404, "{\"error\":\"Node not found\"}");
            }
        } else {
            // /api/nodes - list all
            List<Node> nodes = db.getAllNodes();
            String json = "[" + nodes.stream()
                    .map(Node::toJson)
                    .collect(Collectors.joining(",")) + "]";
            sendResponse(exchange, 200, json);
        }
    }
}
