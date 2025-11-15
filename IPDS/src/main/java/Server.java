import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.sql.*;

import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;



public class Server {
    public static void main(String[] args) throws Exception {
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
            String ips = "";
            try {
                Connector.saveIP(clientIp);
                ips = Connector.getIps();
            } catch(Exception ex) {
                ips = "deu erro";
                System.out.println(ex);
            } finally {
                t.sendResponseHeaders(200, ips.length());
                OutputStream os = t.getResponseBody();
                os.write(ips.getBytes());
                os.close();
            }
        }
    }
}

class Connector {
    public static Connection connect() throws Exception {
        String url = "jdbc:mysql://localhost:3306/ip_hit_history";
        String user = "root";
        String password = "labubuntu";
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection conn = DriverManager.getConnection(url, user, password);
        return conn;
    }
    public static String getIps() throws Exception {
        Connection connection = connect();
        String selectQuery = "SELECT * FROM history;";
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(selectQuery);

        String results = "";
        while (resultSet.next()) {
            String ip = resultSet.getString("ip");
            results = results + ip + "\n";
        }
        return results;
    }
    public static void saveIP(String ip) throws Exception {
        Connection connection = connect();

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        String time  = dtf.format(now);

        try {
            String insert = String.format("INSERT INTO history (ip, last_hit) VALUES ('%s', '%s');", ip, time);
            PreparedStatement ps = connection.prepareStatement(insert);
            ps.execute();
        } catch (Exception ex) {
            String update = String.format(
                "UPDATE history SET last_hit = '%s' WHERE ip='%s';",
                time, ip
            );
            PreparedStatement ps = connection.prepareStatement(update);
            ps.execute();
        }
    }
}
