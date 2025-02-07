import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class MainMenu {
    public static void main(String[] args) throws InterruptedException, SQLException {
        Scanner scanner = new Scanner(System.in);
        int customerId = UserSession.authenticateUser();

        while (true) {
            System.out.println("\nNavigate:");
            System.out.println("1. View all shoes in stock");
            System.out.println("2. View all brands");
            System.out.println("3. View all categories");
            System.out.println("4. Search by brands");
            System.out.println("5. Search by category");
            System.out.println("6. Show cart");
            System.out.println("7. Pay order");
            System.out.println("8. Exit");


            if (!scanner.hasNextInt()) {
                System.out.println("Invalid option");
                scanner.nextLine();
                continue;
            }

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 -> ShoeSearchUtil.viewAllShoes();
                case 2 -> ShoeSearchUtil.viewAllBrands();
                case 3 -> ShoeSearchUtil.viewAllCategories();
                case 4 -> {
                    System.out.println("Search by brand: ");
                    String brand = scanner.nextLine();
                    List<Shoe> foundShoes = ShoeDAO.getShoesByBrand(brand);

                    ShoeSearchUtil.searchShoe("brand: " + brand, foundShoes, scanner, customerId);
                }
                case 5 -> {
                    System.out.println("Search by category: ");
                    String category = scanner.nextLine();
                    List<Shoe> foundShoes = ShoeDAO.getShoesByCategory(category);

                    ShoeSearchUtil.searchShoe("brand: " + category, foundShoes, scanner, customerId);
                }

                case 6 -> {
                    try {
                        Cart userCart = ShoeDAO.getActiveCart(customerId);
                        if (userCart.getItems().isEmpty()) {
                            System.out.println("\nYour cart is empty!\n\n");
                        } else {
                            System.out.println(userCart);
                        }
                    } catch (SQLException e) {
                        System.out.println("Error getting cart" + e.getMessage());
                    }
                }

                case 7 -> {
                    System.out.print("Paying");
                    for (int i = 0; i < 5; i++) {
                        System.out.print(".");
                        Thread.sleep(300);
                    }
                    System.out.println("\n");
                    ShoeDAO.payOrder(customerId);
                }
                case 8 -> {
                    System.out.println("Exiting store, good bye!");
                    for (int i = 0; i < 5; i++) {
                        System.out.print(".");
                        Thread.sleep(300);
                    }
                    System.out.println("\nExited");
                    DbConnection.closeConnection();
                    scanner.close();
                    System.exit(0);
                }
                default -> System.out.println("Invalid choice");
            }
        }
    }
}
