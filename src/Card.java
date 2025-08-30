import java.sql.*;

public class Card {
    private int id;
    private String title;
    private String description;
    private boolean blocked;
    private String blockReason;
    private String unblockReason;

    public Card(String title, String description) {
        this.title = title;
        this.description = description;
        this.blocked = false;
    }

    public void block(String reason) {
        this.blocked = true;
        this.blockReason = reason;
    }

    public void unblock(String reason) {
        this.blocked = false;
        this.unblockReason = reason;
    }

    public void save(int columnId) {
        try (Connection conn = Database.getConnection()) {
            String sql = "INSERT INTO cards (column_id, title, description, blocked, block_reason, unblock_reason) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, columnId);
            stmt.setString(2, title);
            stmt.setString(3, description);
            stmt.setBoolean(4, blocked);
            stmt.setString(5, blockReason);
            stmt.setString(6, unblockReason);
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                this.id = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
