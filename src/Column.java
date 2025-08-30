import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Column {
    private int id;
    private String name;
    private String type;
    private int position;
    private List<Card> cards = new ArrayList<>();

    public Column(String name, String type, int position) {
        this.name = name;
        this.type = type;
        this.position = position;
    }

    public void addCard(Card card) { cards.add(card); }
    public List<Card> getCards() { return cards; }
    public String getName() { return name; }
    public String getType() { return type; }

    public void save(int boardId) {
        try (Connection conn = Database.getConnection()) {
            String sql = "INSERT INTO columns (board_id, name, type, position) VALUES (?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, boardId);
            stmt.setString(2, name);
            stmt.setString(3, type);
            stmt.setInt(4, position);
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                this.id = rs.getInt(1);
            }

            for (Card card : cards) {
                card.save(this.id);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<Column> getColumnsByBoardId(int boardId) {
        List<Column> columns = new ArrayList<>();
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM columns WHERE board_id = ? ORDER BY position ASC")) {

            stmt.setInt(1, boardId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Column col = new Column(rs.getString("name"), rs.getString("type"), rs.getInt("position"));
                col.id = rs.getInt("id");
                col.cards = Card.getCardsByColumnId(col.id);
                columns.add(col);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return columns;
    }
}
