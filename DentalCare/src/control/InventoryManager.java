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
            // Check if supplier already exists
            String checkSql = "SELECT COUNT(*) FROM TblSuppliers WHERE supplierId = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setInt(1, Integer.parseInt(supplier.getId()));
            ResultSet rs = checkStmt.executeQuery();
            rs.next();
            int count = rs.getInt(1);
            rs.close();
            checkStmt.close();

            if (count == 0) {
                String sql = "INSERT INTO TblSuppliers (supplierId, supplierName, email, phoneNum, address) VALUES (?, ?, ?, ?, ?)";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setInt(1, Integer.parseInt(supplier.getId()));
                stmt.setString(2, supplier.getName());
                stmt.setString(3, supplier.getEmail());
                stmt.setString(4, supplier.getContactNumber());
                stmt.setString(5, supplier.getAddress());
                stmt.executeUpdate();
                stmt.close();
            } else {
                System.out.println("Supplier already exists with ID " + supplier.getId());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addItem(Item item) {
        try (Connection conn = DriverManager.getConnection(DB_PATH)) {
            // Check if item already exists
            String checkSql = "SELECT COUNT(*) FROM TblInventoryItems WHERE serialNum = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setInt(1, item.getId());
            ResultSet rs = checkStmt.executeQuery();
            rs.next();
            int count = rs.getInt(1);
            rs.close();
            checkStmt.close();

            if (count == 0) {
                String sql = "INSERT INTO TblInventoryItems (serialNum, itemName, description, quantityInStock, supplierId, expDate) VALUES (?, ?, ?, ?, ?, ?)";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setInt(1, item.getId());
                stmt.setString(2, item.getName());
                stmt.setString(3, item.getDescription());
                stmt.setInt(4, item.getQuantity());

                if (item.getSupplier() != null) {
                    stmt.setInt(5, Integer.parseInt(item.getSupplier().getId()));
                } else {
                    stmt.setNull(5, Types.INTEGER);
                }

                if (item.getExpiryDate() != null) {
                    stmt.setDate(6, Date.valueOf(item.getExpiryDate()));
                } else {
                    stmt.setNull(6, Types.DATE);
                }

                stmt.executeUpdate();
                stmt.close();
            } else {
                System.out.println("❌ Item already exists with ID " + item.getId());
            }
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
                System.out.println("❌ Item not found: " + itemId);
            }
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void assignSupplier(int itemId, Supplier supplier) {
        try (Connection conn = DriverManager.getConnection(DB_PATH)) {
            String sql = "UPDATE TblInventoryItems SET supplierId = ? WHERE serialNum = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, Integer.parseInt(supplier.getId()));
            stmt.setInt(2, itemId);
            int rows = stmt.executeUpdate();
            if (rows == 0) {
                System.out.println("Item not found.");
            }
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Item> generateAlerts() {
        List<Item> lowStockItems = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DB_PATH)) {
            String sql = "SELECT * FROM TblInventoryItems WHERE quantityInStock < ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, 10);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("serialNum");
                String name = rs.getString("itemName");
                String description = rs.getString("description");
                int quantity = rs.getInt("quantityInStock");
                Date sqlDate = rs.getDate("expDate");
                LocalDate expiryDate = sqlDate != null ? sqlDate.toLocalDate() : null;

                // Adjust constructor: removed 'category', set to null
                Item item = new Item(id, name, description, null, quantity, expiryDate, null);
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

