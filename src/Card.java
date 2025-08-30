import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Card {
    int id;
    private String title;
    private String description;
    private boolean blocked;
    String blockReason;
    String unblockReason;

    public Card(String title, String description) {
        this.title = title;
        this.description = description;
        this.blocked = false;
    }

    public String getTitle() { return title; }
    public boolean isBlocked() { return blocked; }

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

    public static List<Card> getCardsByColumnId(int columnId) {
        List<Card> cards = new ArrayList<>();
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM cards WHERE column_id = ?")) {

            stmt.setInt(1, columnId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Card card = new Card(rs.getString("title"), rs.getString("description"));
                card.id = rs.getInt("id");
                card.blocked = rs.getBoolean("blocked");
                card.blockReason = rs.getString("block_reason");
                card.unblockReason = rs.getString("unblock_reason");
                cards.add(card);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cards;
    }
}
