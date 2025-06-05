package boundary;

import entity.Item;
import entity.Supplier;

import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;

public class AccessLoader {

    // Access database path (use forward slashes or escaped backslashes)
    private static final String DB_PATH = "jdbc:ucanaccess://C:/Users/mhema/database2016b.accdb";

    /**
     * Load all suppliers from the Access database into a Map
     */
    public static Map<String, Supplier> loadSuppliers() {
        Map<String, Supplier> suppliers = new HashMap<>();
        try (
            Connection conn = DriverManager.getConnection(DB_PATH);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM TblSuppliers")
        ) {
            while (rs.next()) {
                String id = rs.getString("supplierID");
                String name = rs.getString("name");
                String contactNumber = rs.getString("contactNumber");
                String email = rs.getString("email");
                String address = rs.getString("address");

                Supplier supplier = new Supplier(id, name, contactNumber, email, address);
                suppliers.put(id, supplier);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return suppliers;
    }

    /**
     * Load all items from the Access database and link with suppliers
     */
    public static List<Item> loadItems() {
        List<Item> items = new ArrayList<>();
        Map<String, Supplier> suppliers = loadSuppliers();

        try (
            Connection conn = DriverManager.getConnection(DB_PATH);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM TblItems")
        ) {
            while (rs.next()) {
                String serialNumber = rs.getString("serialNumber");
                String name = rs.getString("itemName");
                String description = rs.getString("description");
                String category = rs.getString("category");
                int quantity = rs.getInt("quantity");

                // Convert java.sql.Date → java.time.LocalDate
                Date sqlDate = rs.getDate("expirationDate");
                LocalDate expiryDate = (sqlDate != null) ? sqlDate.toLocalDate() : null;

                String supplierID = rs.getString("supplierID");
                Supplier supplier = suppliers.getOrDefault(supplierID, null);

                Item item = new Item(serialNumber, name, description, category, quantity, expiryDate, supplier);
                items.add(item);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return items;
    }
}
