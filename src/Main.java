import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Database.initialize();
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\n=== Menu Principal ===");
            System.out.println("1 - Criar novo board");
            System.out.println("2 - Selecionar board");
            System.out.println("3 - Excluir boards");
            System.out.println("4 - Sair");
            System.out.print("Escolha uma opção: ");
            int option = sc.nextInt();
            sc.nextLine();

            switch (option) {
                case 1 -> createBoard(sc);
                case 2 -> selectBoard(sc);
                case 3 -> deleteBoards(sc);
                case 4 -> System.exit(0);
                default -> System.out.println("Opção inválida!");
            }
        }
    }

    private static void createBoard(Scanner sc) {
        System.out.print("Nome do board: ");
        String name = sc.nextLine();
        Board board = new Board(name);

        board.addColumn(new Column("Backlog", "Inicial", 1));
        board.addColumn(new Column("Em Andamento", "Pendente", 2));
        board.addColumn(new Column("Concluído", "Final", 3));
        board.addColumn(new Column("Cancelado", "Cancelamento", 4));

        board.save();
    }

    private static void selectBoard(Scanner sc) {
        List<Board> boards = Board.getAllBoards();
        if (boards.isEmpty()) {
            System.out.println("Nenhum board disponível.");
            return;
        }

        System.out.println("Boards disponíveis:");
        for (int i = 0; i < boards.size(); i++) {
            System.out.printf("%d - %s\n", i + 1, boards.get(i).getName());
        }

        System.out.print("Escolha um board: ");
        int choice = sc.nextInt() - 1;
        sc.nextLine();

        if (choice < 0 || choice >= boards.size()) {
            System.out.println("Escolha inválida.");
            return;
        }

        boardMenu(boards.get(choice), sc);
    }

    private static void boardMenu(Board board, Scanner sc) {
        while (true) {
            System.out.println("\n=== Board: " + board.getName() + " ===");
            System.out.println("1 - Criar card");
            System.out.println("2 - Mover card");
            System.out.println("3 - Bloquear card");
            System.out.println("4 - Desbloquear card");
            System.out.println("5 - Cancelar card");
            System.out.println("6 - Voltar ao menu principal");
            System.out.print("Escolha uma opção: ");
            int opt = sc.nextInt();
            sc.nextLine();

            switch (opt) {
                case 1 -> createCard(board, sc);
                case 2 -> moveCard(board, sc);
                case 3 -> blockCard(board, sc);
                case 4 -> unblockCard(board, sc);
                case 5 -> cancelCard(board, sc);
                case 6 -> { return; }
                default -> System.out.println("Opção inválida!");
            }
        }
    }

    private static void createCard(Board board, Scanner sc) {
        Column initial = board.getColumns().stream()
                .filter(c -> c.getType().equals("Inicial")).findFirst().orElse(null);
        if (initial == null) { System.out.println("Coluna inicial não encontrada."); return; }

        System.out.print("Título do card: "); String title = sc.nextLine();
        System.out.print("Descrição: "); String desc = sc.nextLine();

        Card card = new Card(title, desc);
        initial.addCard(card);
        initial.save(board.getId());

        System.out.println("Card criado na coluna inicial!");
    }

    private static void moveCard(Board board, Scanner sc) {
        List<Column> columns = board.getColumns();
        for (int i = 0; i < columns.size(); i++) {
            System.out.printf("%d - %s (%d cards)\n", i + 1, columns.get(i).getName(), columns.get(i).getCards().size());
        }

        System.out.print("Escolha a coluna de origem: ");
        int colIndex = sc.nextInt() - 1; sc.nextLine();
        if (colIndex < 0 || colIndex >= columns.size()) { System.out.println("Coluna inválida."); return; }

        Column from = columns.get(colIndex);
        List<Card> cards = from.getCards();
        if (cards.isEmpty()) { System.out.println("Não há cards nesta coluna."); return; }

        for (int i = 0; i < cards.size(); i++) {
            System.out.printf("%d - %s\n", i + 1, cards.get(i).getTitle());
        }

        System.out.print("Escolha o card: ");
        int cardIndex = sc.nextInt() - 1; sc.nextLine();
        if (cardIndex < 0 || cardIndex >= cards.size()) { System.out.println("Card inválido."); return; }

        Card card = cards.get(cardIndex);
        if (card.isBlocked()) { System.out.println("Este card está bloqueado. Desbloqueie antes de mover."); return; }

        Column to = null;
        for (int i = colIndex + 1; i < columns.size(); i++) {
            if (!columns.get(i).getType().equals("Cancelamento")) { to = columns.get(i); break; }
        }

        if (to == null) { System.out.println("Card já está na última coluna."); return; }

        from.getCards().remove(card);
        to.addCard(card);
        from.save(board.getId());
        to.save(board.getId());

        System.out.println("Card movido para " + to.getName());
    }

    private static void blockCard(Board board, Scanner sc) {
        Card card = selectCard(board, sc);
        if (card == null) return;

        System.out.print("Motivo do bloqueio: "); String reason = sc.nextLine();
        card.block(reason);
        saveCardState(card);
        System.out.println("Card bloqueado!");
    }

    private static void unblockCard(Board board, Scanner sc) {
        Card card = selectCard(board, sc);
        if (card == null) return;

        System.out.print("Motivo do desbloqueio: "); String reason = sc.nextLine();
        card.unblock(reason);
        saveCardState(card);
        System.out.println("Card desbloqueado!");
    }

    private static void cancelCard(Board board, Scanner sc) {
        Card card = selectCard(board, sc);
        if (card == null) return;

        Column cancelCol = board.getColumns().stream()
                .filter(c -> c.getType().equals("Cancelamento")).findFirst().orElse(null);
        if (cancelCol == null) { System.out.println("Coluna de cancelamento não encontrada."); return; }

        for (Column c : board.getColumns()) c.getCards().remove(card);
        cancelCol.addCard(card);

        for (Column c : board.getColumns()) c.save(board.getId());

        System.out.println("Card cancelado e movido para a coluna 'Cancelado'.");
    }

    private static Card selectCard(Board board, Scanner sc) {
        List<Column> columns = board.getColumns();
        for (int i = 0; i < columns.size(); i++) {
            System.out.printf("%d - %s (%d cards)\n", i + 1, columns.get(i).getName(), columns.get(i).getCards().size());
        }

        System.out.print("Escolha a coluna: ");
        int colIndex = sc.nextInt() - 1; sc.nextLine();
        if (colIndex < 0 || colIndex >= columns.size()) { System.out.println("Coluna inválida."); return null; }

        Column column = columns.get(colIndex);
        List<Card> cards = column.getCards();
        if (cards.isEmpty()) { System.out.println("Nenhum card nesta coluna."); return null; }

        for (int i = 0; i < cards.size(); i++)
            System.out.printf("%d - %s%s\n", i + 1, cards.get(i).getTitle(), cards.get(i).isBlocked() ? " (Bloqueado)" : "");

        System.out.print("Escolha o card: ");
        int cardIndex = sc.nextInt() - 1; sc.nextLine();
        if (cardIndex < 0 || cardIndex >= cards.size()) { System.out.println("Card inválido."); return null; }

        return cards.get(cardIndex);
    }

    private static void saveCardState(Card card) {
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "UPDATE cards SET blocked = ?, block_reason = ?, unblock_reason = ? WHERE id = ?")) {
            stmt.setBoolean(1, card.isBlocked());
            stmt.setString(2, card.isBlocked() ? card.blockReason : null);
            stmt.setString(3, !card.isBlocked() ? card.unblockReason : null);
            stmt.setInt(4, card.id);
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void deleteBoards(Scanner sc) {
        List<Board> boards = Board.getAllBoards();
        if (boards.isEmpty()) { System.out.println("Nenhum board disponível."); return; }

        System.out.println("Boards disponíveis:");
        for (int i = 0; i < boards.size(); i++)
            System.out.printf("%d - %s\n", i + 1, boards.get(i).getName());

        System.out.print("Escolha o board para excluir: ");
        int choice = sc.nextInt() - 1; sc.nextLine();
        if (choice < 0 || choice >= boards.size()) { System.out.println("Escolha inválida."); return; }

        Board boardToDelete = boards.get(choice);
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM boards WHERE id = ?")) {
            stmt.setInt(1, boardToDelete.getId());
            int rows = stmt.executeUpdate();
            if (rows > 0) System.out.println("Board '" + boardToDelete.getName() + "' excluído com sucesso!");
            else System.out.println("Erro ao excluir board.");
        } catch (Exception e) { e.printStackTrace(); }
    }
}
