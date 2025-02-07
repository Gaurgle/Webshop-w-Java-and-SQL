import java.util.ArrayList;
import java.util.List;

public class Cart {
    private final List<Shoe> items;

    public Cart() {
        this.items = new ArrayList<>();
    }

    public void addItem(Shoe item) {
        this.items.add(item);
    }

    public List<Shoe> getItems() {
        return items;
    }


    public double getCartTotal() {
        double sum = 0.0;
        for (Shoe shoe : items) {
            sum += shoe.pricePerUnit() * shoe.getQty();
        }
        return sum;
    }

    @Override
    public String toString() {
        if (items.isEmpty()) {
            return "No items in cart.";
        }
        StringBuilder sb = new StringBuilder("\n=== Your Cart ===\n");
        sb.append("------------------------------------------------------------------------------------------\n");
        sb.append(String.format("| %-3s | %-10s | %-20s | %-7s | %-4s | %-6s | %-6s | %-10s |\n",
                "#", "Brand", "Model", "Color", "Size", "Sex", "Qty", "Per unit"));
        sb.append("------------------------------------------------------------------------------------------\n");

        for (int i = 0; i < items.size(); i++) {
            Shoe shoe = items.get(i);
            sb.append(String.format("| %-3d | %-10s | %-20s | %-7s | %-4.1f | %-6s | %-6d | %6.0f SEK |\n",
                    (i + 1), shoe.getBrand(), shoe.getModel(), shoe.getColor(),
                    shoe.getSize(), shoe.getSex(), shoe.getQty(), shoe.pricePerUnit()));
        }
        sb.append("------------------------------------------------------------------------------------------\n");
        sb.append(String.format("| %-65s | %-1s SUM: | %6.0f SEK |\n", "Total Price:", "", getCartTotal()));
        sb.append("------------------------------------------------------------------------------------------\n");

        return sb.toString();
    }
}