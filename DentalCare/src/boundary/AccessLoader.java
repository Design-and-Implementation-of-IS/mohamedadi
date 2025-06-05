package boundary;

import entity.Item;
import entity.Supplier;

import java.io.File;
import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;

public class AccessLoader {

    private static final String DB_PATH;

    static {
        File dbFile = new File("C:/Users/mhema/DentalCareData.accdb");

        System.out.println("[DEBUG] DB absolute path: " + dbFile.getAbsolutePath());
        System.out.println("[DEBUG] DB exists? " + dbFile.exists());

        DB_PATH = "jdbc:ucanaccess://" + dbFile.getAbsolutePath();
    }





    /**
     * Load all suppliers from TblSuppliers (Map version, used for lookup)
     */
    public static Map<String, Supplier> loadSuppliers() {
        Map<String, Supplier> suppliers = new HashMap<>();
        try (
            Connection conn = DriverManager.getConnection(DB_PATH);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM TblSuppliers")
        ) {
            while (rs.next()) {
                String id = rs.getString("supplierId");
                String name = rs.getString("supplierName");
                String contactNumber = rs.getString("phoneNum");
                String email = rs.getString("email");
                String address = rs.getString("address");

                Supplier supplier = new Supplier(id, name, contactNumber, email, address);
                suppliers.put(id, supplier);
                System.out.println("[DEBUG] Loaded supplier: " + id + " - " + name);
            }
        } catch (Exception e) {
            System.out.println("[ERROR] Failed to load suppliers");
            e.printStackTrace();
        }
        return suppliers;
    }

    /**
     * NEW: Load all suppliers as a List (for display)
     */
    public static List<Supplier> loadSuppliersAsList() {
        return new ArrayList<>(loadSuppliers().values());
    }

    /**
     * Load all items from TblInventoryItems and attach suppliers
     */
    public static List<Item> loadItems() {
        List<Item> items = new ArrayList<>();
        Map<String, Supplier> suppliers = loadSuppliers();

        try (
            Connection conn = DriverManager.getConnection(DB_PATH);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM TblInventoryItems")
        ) {
            while (rs.next()) {
                int serialNumber = rs.getInt("serialNum");
                String name = rs.getString("itemName");
                String description = rs.getString("description");
                String category = rs.getString("description"); // Used in place of category
                int quantity = rs.getInt("quantityInStock");

                Date sqlDate = rs.getDate("expDate");
                LocalDate expiryDate = (sqlDate != null) ? sqlDate.toLocalDate() : null;

                String supplierID = rs.getString("supplierId");
                Supplier supplier = suppliers.getOrDefault(supplierID, null);

                Item item = new Item(serialNumber, name, description, category, quantity, expiryDate, supplier);
                items.add(item);

                System.out.println("[DEBUG] Loaded item: " + name + " (Qty: " + quantity + ")");
            }
        } catch (Exception e) {
            System.out.println("[ERROR] Failed to load inventory items");
            e.printStackTrace();
        }

        return items;
    }
}
