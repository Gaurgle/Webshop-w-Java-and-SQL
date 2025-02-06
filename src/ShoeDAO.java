import java.sql.*;

import java.util.*;

public class ShoeDAO {

    // Finds all shoes in stock
    public static List<Shoe> shoesInStock() {
        List<Shoe> shoes = new ArrayList<>();
        String sql =                                            // regular statement
                """
                        SELECT s.shoe_id, b.brand_name, m.model_name, s.color, s.size, s.price, s.units_in_stock, s.sex, sc.category_name
                        FROM shoes s
                        JOIN models m ON s.model_id = m.model_id
                        JOIN brands b ON m.brand_id = b.brand_id
                        JOIN model_categories mc ON m.model_id = mc.model_id
                        JOIN shoe_categories sc ON mc.category_id = sc.category_id
                        WHERE s.units_in_stock > 0
                        ORDER BY s.shoe_id
                        """;

        Map<Integer, Shoe> shoeMap = new LinkedHashMap<>();
        try (
                Statement stmt = DbConnection.getConnection("Webshop").createStatement();
                ResultSet rs = stmt.executeQuery(sql)
        ) {
            while (rs.next()) {
                int shoe_id = rs.getInt("shoe_id");

                Shoe shoe = shoeMap.get(shoe_id);
                if (shoe == null) {
                    shoe = new Shoe(
                            rs.getInt("shoe_id"),
                            rs.getString("brand_name"),
                            rs.getString("model_name"),
                            rs.getString("color"),
                            rs.getDouble("size"),
                            rs.getDouble("price"),
                            rs.getString("sex"),
                            rs.getInt("units_in_stock")
                    );
                    shoeMap.put(shoe_id, shoe);

                }
                String categoryName = rs.getString("category_name");
                shoe.addCategory(categoryName);

            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while retrieving shoes", e);
        }
        return new ArrayList<>(shoeMap.values());
    }

    // finds shoes with brand name
    public static List<Shoe> getShoesByBrand(String searched_brand) {
        List<Shoe> shoes = new ArrayList<>();

        searched_brand = searched_brand.toLowerCase().trim();
        if (searched_brand.endsWith("s")) {
            searched_brand = searched_brand.substring(0, searched_brand.length() - 1);
        }
        String sql =                                        // prepared statement
                """
                        SELECT s.shoe_id, b.brand_name, m.model_name, s.color, s.size, s.price, s.units_in_stock, s.sex
                        FROM shoes s
                        JOIN models m ON s.model_id = m.model_id
                        JOIN brands b ON m.brand_id = b.brand_id
                        WHERE LOWER(b.brand_name) LIKE LOWER(?);
                        """;

        try (Connection conn = DbConnection.getConnection("Webshop");
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + searched_brand + "%");

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Shoe shoe = new Shoe(
                        rs.getInt("shoe_id"),
                        rs.getString("brand_name"),
                        rs.getString("model_name"),
                        rs.getString("color"),
                        rs.getDouble("size"),
                        rs.getDouble("price"),
                        rs.getString("sex"),
                        rs.getInt("units_in_stock")
                );
                shoes.add(shoe);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while retrieving shoes", e);
        }
        return shoes;
    }

    // finds shoe on category name
    public static List<Shoe> getShoesByCategory(String categoryName) {
        List<Shoe> shoes = new ArrayList<>();

        categoryName = categoryName.toLowerCase().trim();

        String sql =                                            // prepared statement
                """
                        SELECT s.shoe_id, b.brand_name, m.model_name, s.color, s.size, s.price, s.units_in_stock, s.sex
                        FROM shoes s
                             JOIN models m ON s.model_id = m.model_id
                             JOIN model_categories mc ON m.model_id = mc.model_id
                             JOIN shoe_categories sc ON mc.category_id = sc.category_id
                             JOIN brands b ON m.brand_id = b.brand_id
                        WHERE LOWER(sc.category_name) LIKE LOWER(?);
                        """;


        return getShoes(categoryName, shoes, sql);
    }

    private static List<Shoe> getShoes(String categoryName, List<Shoe> shoes, String sql) {
        try (Connection conn = DbConnection.getConnection("Webshop");
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, categoryName);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Shoe shoe = new Shoe(
                        rs.getInt("shoe_id"),
                        rs.getString("brand_name"),
                        rs.getString("model_name"),
                        rs.getString("color"),
                        rs.getDouble("size"),
                        rs.getDouble("price"),
                        rs.getString("sex"),
                        rs.getInt("units_in_stock")
                );
                shoes.add(shoe);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while retrieving shoes", e);
        }
        return shoes;
    }

    // gets all brands
    public static List<String> getAllBrands() {
        List<String> brands = new ArrayList<>();
        String sql = "SELECT brand_name FROM brands ";

        try (Connection conn = DbConnection.getConnection("Webshop");
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                brands.add(rs.getString("brand_name"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return brands;
    }

    // gets all categories
    public static List<String> getAllCategories() {
        List<String> categories = new ArrayList<>();
        String sql = "SELECT category_name FROM shoe_categories";

        try (Connection conn = DbConnection.getConnection("Webshop");
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                categories.add(rs.getString("category_name"));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while retrieving categories", e);
        }
        return categories;
    }

    public static boolean addToCart(int customerId, Integer orderId, int shoeId) throws SQLException {
        String sql = "{CALL AddToCart(?, ?, ?)}";               // callable statement

        try (
                Connection conn = DbConnection.getConnection("Webshop");
                CallableStatement stmt = conn.prepareCall(sql)) {

            stmt.setInt(1, customerId);

            if (orderId == null) {
                stmt.setNull(2, java.sql.Types.INTEGER);
            } else {
                stmt.setInt(2, orderId);
            }
            stmt.setInt(3, shoeId);

            stmt.execute();
            return true;

        } catch (SQLException e) {
            throw e;
        }
    }

    public static Cart getActiveCart(int customerId) throws SQLException {
        Cart cart = new Cart();

        Integer activeOrderId = getActiveOrderId(customerId);
        if (activeOrderId == null) {
            return cart;
        }

        String sql = """
                SELECT oi.shoe_id, oi.qty, oi.item_price,
                s.color, s.size, s.price AS shoePrice, s.sex, 
                m.model_name, b.brand_name
                FROM order_items oi
                JOIN shoes s ON oi.shoe_id = s.shoe_id
                JOIN models m ON s.model_id = m.model_id
                JOIN brands b ON m.brand_id = b.brand_id
                WHERE oi.order_id = ?
                """;

        try (Connection conn = DbConnection.getConnection("Webshop");
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, activeOrderId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Shoe item = new Shoe(
                            rs.getInt("shoe_id"),
                            rs.getString("brand_name"),
                            rs.getString("model_name"),
                            rs.getString("color"),
                            rs.getDouble("size"),
                            rs.getDouble("shoePrice"),
                            rs.getString("sex"),
                            rs.getInt("qty")
                    );
                    cart.addItem(item);
                }
            }
        }
        return cart;
    }

    public static Integer getActiveOrderId(int customerId) throws SQLException {
        String sql = """
                SELECT order_id
                FROM orders
                WHERE customer_id = ?
                    AND payment = 'ACTIVE'
                LIMIT 1;
                """;
        try (Connection conn = DbConnection.getConnection("Webshop");
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, customerId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("order_id");
                }
            }
        }
        return null;
    }

    public static boolean payOrder(int customerId) throws SQLException {
        String sql = "{CALL PayOrder(?)}";

        try (Connection conn = DbConnection.getConnection("Webshop");
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, customerId);
            stmt.execute();
            System.out.println("Payment went through!\n\n");
            return true;

        } catch (SQLException e) {
            if (e.getMessage().contains("no active order found")) {
                System.out.println("You got nothing to pay!");
            } else {
                System.out.println("Error while paying order: " + e.getMessage());
            }
            return false;
        }
    }
}
