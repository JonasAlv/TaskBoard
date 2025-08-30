import java.sql.*;

public class Database {
    private static final String URL = "jdbc:mysql://localhost:3306/task_board?serverTimezone=UTC";
    private static final String USER = "board_user";
    private static final String PASSWORD = "senha123";

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // garante que o driver seja carregado
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void initialize() {
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS boards (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "name VARCHAR(255) NOT NULL" +
                    ");");

            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS columns (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "board_id INT," +
                    "name VARCHAR(255) NOT NULL," +
                    "type VARCHAR(50) NOT NULL," +
                    "position INT NOT NULL," +
                    "FOREIGN KEY (board_id) REFERENCES boards(id) ON DELETE CASCADE" +
                    ");");

            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS cards (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "column_id INT," +
                    "title VARCHAR(255) NOT NULL," +
                    "description TEXT," +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                    "blocked BOOLEAN DEFAULT FALSE," +
                    "block_reason TEXT," +
                    "unblock_reason TEXT," +
                    "FOREIGN KEY (column_id) REFERENCES columns(id) ON DELETE CASCADE" +
                    ");");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
