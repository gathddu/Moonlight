import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class Server {
    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(80), 0);
        server.createContext("/", new Handler());
        server.setExecutor(null);
        System.out.println("--- Starting Server ---");
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
public class MySQLConector {
    public static void main(String[] args) {
        String connectionUrl = "jdbc:mysql://localhost:3306";
        Connection conn = DriverManager.
            getConnection(
                 connectionUrl,
                 "root",
                 "labubuntu"
            );
    }
}
