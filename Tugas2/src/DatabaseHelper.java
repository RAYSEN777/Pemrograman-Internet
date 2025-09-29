import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper {
    private static final String URL = "jdbc:mysql://localhost:3306/download?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root"; 
    private static final String PASS = "";

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); 
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void insertLog(String url, String size, String status, String finishedAt) {
        String sql = "INSERT INTO download_logs (url, size, status, finished_at) VALUES (?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, url);
            ps.setString(2, size);
            ps.setString(3, status);
            ps.setString(4, finishedAt);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<String[]> getLogs() {
        List<String[]> logs = new ArrayList<>();
        String sql = "SELECT url, size, status, finished_at FROM download_logs ORDER BY id DESC";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String url = rs.getString("url");
                String size = rs.getString("size");
                String status = rs.getString("status");
                String finishedAt = rs.getString("finished_at");
                logs.add(new String[]{url, size, status, finishedAt});
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return logs;
    }
}
