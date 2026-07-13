package moonlight.node.service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class IpdsClient {
    private final String baseUrl;
    private final HttpClient client;

    public IpdsClient(String baseUrl ) {
        this.baseUrl = baseUrl;
        this.client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();
    }

    public boolean register(String nodeIdentifier, int port) {
        String json = String.format("{\"nodeIdentifier\":\"%s\",\"port\":%d}", nodeIdentifier, port);

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/api/nodes/register"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode() == 201 || response.statusCode() == 200;
        } catch (Exception e) {
            System.err.println("[IpdsClient] Registration failed: " + e.getMessage());
            return false;
        }
    }

    public boolean sendHeartbeat(String nodeIdentifier, double uptimePercentage) {
        String json = String.format("{\"nodeIdentifier\":\"%s\",\"uptimePercentage\":%.2f}", nodeIdentifier, uptimePercentage);

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/api/nodes/heartbeat"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode() == 200;
        } catch (Exception e) {
            System.err.println("[IpdsClient] Heartbeat failed: " + e.getMessage());
            return false;
        }
    }

    public String getLeader() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/api/nodes/leader"))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return response.body();
            }
        } catch (Exception e) {
            System.err.println("[IpdsClient] Failed to get leader: " + e.getMessage());
        }
        return null;
    }
}
