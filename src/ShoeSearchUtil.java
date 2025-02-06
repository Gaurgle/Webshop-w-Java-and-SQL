import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class ShoeSearchUtil {

    public static void searchShoe(String searchType, List<Shoe> foundShoes, Scanner scanner, int customerId) {
        if (foundShoes.isEmpty()) {
            System.out.println("No shoes found for " + searchType);
            return;
        }
        System.out.println("\nSearch Results for " + searchType + ":");
        System.out.println("------------------------------------------------------------------------------------------------------------");
        System.out.printf("| %-3s | %-15s | %-25s | %-15s | %-4s | %-6s | %-6s | %-10s |\n",
                "#", "Brand", "Model", "Color", "Size", "Sex", "Units", "Price");
        System.out.println("------------------------------------------------------------------------------------------------------------");
        for (int i = 0; i < foundShoes.size(); i++) {
            Shoe shoe = foundShoes.get(i);
            System.out.printf("| %-3d | %-15s | %-25s | %-15s | %-4.1f | %-6s | %-6s | %6.0f SEK |\n",
                    (i + 1), shoe.getBrand(), shoe.getModel(), shoe.getColor(),
                    shoe.getSize(), shoe.getSex(), shoe.getQty(), shoe.getPrice());
        }
        System.out.println("-------------------------------------------------------------------------------------------------------------");

        System.out.println("Enter index nr to add shoe to basket (0 to skip)");
        int selectIndex = scanner.nextInt();
        scanner.nextLine();

        if (selectIndex == 0) {
            System.out.println("Back to menu.");
            return;
        }

        Shoe chosenShoe = foundShoes.get(selectIndex - 1);
        try {
            boolean success = ShoeDAO.addToCart(customerId, null, chosenShoe.getShoeId());
            if (success) {
                System.out.println("Added a pair of " + chosenShoe.getModel() + "'s to your cart.\n\n");
            }
        } catch (SQLException e) {
            String errorMessage = e.getMessage().toLowerCase();
            if (errorMessage.contains("out of stock")) {
                System.out.println("Sorry, " + chosenShoe + " is out of stock.\n\n");
            } else {
                System.out.println("unexpected error: " + e.getMessage());
            }
        }
    }

    public static void viewAllShoes() {
        List<Shoe> shoes = ShoeDAO.shoesInStock();

        if (shoes == null || shoes.isEmpty()) {
            System.out.println("No shoes in stock.");
            return;
        }
        System.out.println("---------------------------------------------------------------------------------------------------------------------------------------------");
        System.out.printf("| %-3s | %-15s | %-25s | %-15s | %-4s | %-6s | %-6s | %-10s | %-30s |\n",
                "#", "Brand", "Model", "Color", "Size", "Sex", "Qty", "Price", "Categories");
        System.out.println("---------------------------------------------------------------------------------------------------------------------------------------------");

        for (int i = 0; i < shoes.size(); i++) {
            Shoe shoe = shoes.get(i);
            System.out.printf("| %-3d | %-15s | %-25s | %-15s | %-4.1f | %-6s | %-6d | %6.0f SEK | %-30s |\n",
                    (i + 1),
                    shoe.getBrand(),
                    shoe.getModel(),
                    shoe.getColor(),
                    shoe.getSize(),
                    shoe.getSex(),
                    shoe.getQty(),     // 'units_in_stock' or 'quantity in cart' depending on context
                    shoe.getPrice(),    // Single‐pair price
                    shoe.getCategories()
            );
        }
        System.out.println("---------------------------------------------------------------------------------------------------------------------------------------------");
    }

    public static void viewAllCategories() {
        List<String> categories = ShoeDAO.getAllCategories();

        if (categories.isEmpty()) {
            System.out.println("No categories to show.");
            return;
        }
        System.out.println("\n==== Categories ====");
        for (int i = 0; i < categories.size(); i++) {
            System.out.printf("%d. %s\n", (i + 1), categories.get(i));

        }
    }

    public static void viewAllBrands() {
        List<String> categories = ShoeDAO.getAllBrands();

        if (categories.isEmpty()) {
            System.out.println("No categories to show.");
            return;
        }
        System.out.println("\n==== Brands ====");
        for (int i = 0; i < categories.size(); i++) {
            System.out.printf("%d. %s\n", (i + 1), categories.get(i));

        }
    }
}
