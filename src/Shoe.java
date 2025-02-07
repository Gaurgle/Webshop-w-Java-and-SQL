import java.util.ArrayList;
import java.util.List;

public class Shoe {
    private final int shoeId;
    private final String brand;
    private final String model;
    private final String color;
    private final Double size;
    private final Double price;
    private final String sex;
    private final int qty;
    private final List<String> categories;

    public Shoe(int id, String brand, String model, String color, Double size, Double price, String sex, int qty) {
        this.shoeId = id;
        this.brand = brand;
        this.model = model;
        this.color = color;
        this.size = size;
        this.price = price;
        this.sex = sex;
        this.qty = qty;
        this.categories = new ArrayList<>();
    }

    @Override
    public String toString() {
        return String.format("%s, Model: %s, Color: %s, Size: %.1f, Sex: %s, Price: %.2f SEK, Units in stock: %d) - Categories: %s",
                brand, model, color, size, sex, price, qty, categories
        );
    }

    public void addCategory(String category) {
        this.categories.add(category);
    }

    public List<String> getCategories() {
        return categories;
    }

    public int numberOfItems() {
        return categories.size();
    }

    public int getShoeId() {
        return shoeId;
    }

    public String getModel() {
        return model;
    }

    public double pricePerUnit() {
        return this.price;
//        return this.price * this.qty;
    }

    public Double getPrice() {
        return price;
    }

    public String getBrand() {
        return brand;
    }

    public String getColor() {
        return color;
    }

    public Double getSize() {
        return size;
    }

    public String getSex() {
        return sex;
    }

    public int getQty() {
        return qty;
    }
}
