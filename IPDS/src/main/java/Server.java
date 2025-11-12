import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Server {
    public static void main(String[] args) throws Exception {
        System.out.println("Connection to db...");
        Connector connector = new Connector();
        connector.connect();

        System.out.println("--- Starting Server ---");
        HttpServer server = HttpServer.create(new InetSocketAddress(5432), 0);
        server.createContext("/", new Handler());
        server.setExecutor(null);
        server.start();
    }

    static class Handler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            String clientIp = t.getRemoteAddress().getAddress().getHostAddress();
            t.sendResponseHeaders(200, clientIp.length());
            OutputStream os = t.getResponseBody();
            os.write(clientIp.getBytes());
            os.close();
      }
    }
}

class Connector {
    public static void connect() {
        String url = "jdbc:mysql://localhost:3306/ip_hit_history";
        String user = "root";
        String password = "labubuntu";
        System.out.println("in");

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            if (conn != null) {
                System.out.println("Connected to MySQL database!");
            }
        } catch (SQLException e) {
            System.err.println("Connection failed:");
            e.printStackTrace();
        }
    }
}
