// Updated InventoryManager.java to fix supplier insertion and item FK errors
package control;

import entity.Item;
import entity.Supplier;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class InventoryManager {
    private static final String DB_PATH = "jdbc:ucanaccess://C:/Users/mhema/database2016b.accdb";

    public void addSupplier(Supplier supplier) {
        try (Connection conn = DriverManager.getConnection(DB_PATH)) {
            String sql = "INSERT INTO TblSuppliers (supplierId, supplierNam, email, phoneNum, address) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);

            // ✅ supplierId must be an integer
            stmt.setInt(1, Integer.parseInt(supplier.getId()));               // supplierId
            stmt.setString(2, supplier.getName());                            // supplierNam
            stmt.setString(3, supplier.getEmail());                           // email
            stmt.setString(4, supplier.getContactNumber());                           // phoneNum
            stmt.setString(5, supplier.getAddress());                         // address

            stmt.executeUpdate();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (NumberFormatException e) {
            System.err.println("❌ supplierId must be numeric. You gave: " + supplier.getId());
        }
    }


    public void addItem(Item item) {
        try (Connection conn = DriverManager.getConnection(DB_PATH)) {
            String sql = "INSERT INTO TblInventoryItems (serialNum, itemName, description, quantityInStock, supplierId, expDate) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, item.getId());
            stmt.setString(2, item.getName());
            stmt.setString(3, item.getDescription());
            stmt.setInt(4, item.getQuantity());

            if (item.getSupplier() != null && item.getSupplier().getId() != null && !item.getSupplier().getId().isBlank()) {
                stmt.setInt(5, Integer.parseInt(item.getSupplier().getId().trim()));
            } else {
                stmt.setNull(5, Types.INTEGER);
            }

            stmt.setDate(6, item.getExpiryDate() != null ? Date.valueOf(item.getExpiryDate()) : null);
            stmt.executeUpdate();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void assignSupplier(int itemId, Supplier supplier) {
        try (Connection conn = DriverManager.getConnection(DB_PATH)) {
            String sql = "UPDATE TblInventoryItems SET supplierId = ? WHERE serialNum = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, Integer.parseInt(supplier.getId().trim()));
            stmt.setInt(2, itemId);
            int rows = stmt.executeUpdate();
            if (rows == 0) {
                System.out.println("Item not found: " + itemId);
            }
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateStock(int itemId, int newQuantity) {
        try (Connection conn = DriverManager.getConnection(DB_PATH)) {
            String sql = "UPDATE TblInventoryItems SET quantityInStock = ? WHERE serialNum = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, newQuantity);
            stmt.setInt(2, itemId);
            int rows = stmt.executeUpdate();
            if (rows == 0) {
                System.out.println("Item not found: " + itemId);
            }
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Item> generateAlerts() {
        List<Item> lowStockItems = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DB_PATH)) {
            String sql = "SELECT * FROM TblInventoryItems WHERE quantityInStock < 10";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("serialNum");
                String name = rs.getString("itemName");
                String description = rs.getString("description");
                int quantity = rs.getInt("quantityInStock");
                Date sqlDate = rs.getDate("expDate");
                LocalDate expiryDate = sqlDate != null ? sqlDate.toLocalDate() : null;
                Item item = new Item(id, name, description, "", quantity, expiryDate, null);
                lowStockItems.add(item);
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lowStockItems;
    }
}


