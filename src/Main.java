import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Database.initialize();
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\n=== Menu Principal ===");
            System.out.println("1 - Criar novo board");
            System.out.println("2 - Sair");
            System.out.print("Escolha uma opção: ");
            int option = sc.nextInt();
            sc.nextLine();

            switch (option) {
                case 1 -> {
                    System.out.print("Nome do board: ");
                    String name = sc.nextLine();
                    Board board = new Board(name);

                    board.addColumn(new Column("Backlog", "Inicial", 1));
                    board.addColumn(new Column("Em Andamento", "Pendente", 2));
                    board.addColumn(new Column("Concluído", "Final", 3));
                    board.addColumn(new Column("Cancelado", "Cancelamento", 4));

                    Card card = new Card("Card 1", "Descrição do card 1");
                    board.getColumns().get(0).addCard(card); // adiciona à primeira coluna

                    board.save();
                }
                case 2 -> {
                    System.out.println("Saindo...");
                    System.exit(0);
                }
                default -> System.out.println("Opção inválida!");
            }
        }
    }
}
