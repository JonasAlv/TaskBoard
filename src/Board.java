import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Board {
    private int id;
    private String name;
    private List<Column> columns = new ArrayList<>();

    public Board(String name) {
        this.name = name;
    }

    public void addColumn(Column column) {
        columns.add(column);
    }

    public String getName() { return name; }
    public int getId() { return id; }

    public List<Column> getColumns() {
        return columns;
    }

    public void save() {
        try (Connection conn = Database.getConnection()) {
            String sql = "INSERT INTO boards (name) VALUES (?)";
            PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, name);
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                this.id = rs.getInt(1);
            }

            for (Column col : columns) {
                col.save(this.id);
            }

            System.out.println("Board e colunas salvos no MySQL!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
