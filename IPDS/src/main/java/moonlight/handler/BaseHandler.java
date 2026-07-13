package moonlight.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

public abstract class BaseHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange ) throws IOException {
        // CORS headers on every response
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type, Authorization");
        exchange.getResponseHeaders().add("Content-Type", "application/json");

        // Handle preflight requests
        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        try {
            processRequest(exchange);
        } catch (Exception e) {
            System.err.println("[HTTP] Error: " + e.getMessage());
            sendResponse(exchange, 500, "{\"error\":\"Internal server error\"}");
        }
    }

    protected abstract void processRequest(HttpExchange exchange) throws Exception;

    protected void sendResponse(HttpExchange exchange, int statusCode, String body) throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    protected String readRequestBody(HttpExchange exchange) throws IOException {
        try (InputStream is = exchange.getRequestBody();
             BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            return reader.lines().collect(Collectors.joining("\n"));
        }
    }

    protected String extractJsonValue(String json, String key) {
        return extractJsonValue(json, key, "");
    }

    protected String extractJsonValue(String json, String key, String defaultValue) {
        String search = "\"" + key + "\"";
        int keyIndex = json.indexOf(search);
        if (keyIndex == -1) return defaultValue;

        int colonIndex = json.indexOf(":", keyIndex + search.length());
        if (colonIndex == -1) return defaultValue;

        int start = colonIndex + 1;
        // Skip whitespace and opening quote
        while (start < json.length() && (json.charAt(start) == ' ' || json.charAt(start) == '"')) {
            start++;
        }

        int end = start;
        while (end < json.length() && json.charAt(end) != '"' && json.charAt(end) != ',' && json.charAt(end) != '}') {
            end++;
        }

        return json.substring(start, end).trim();
    }
}
