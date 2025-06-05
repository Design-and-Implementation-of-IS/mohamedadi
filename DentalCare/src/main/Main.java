package main;

import boundary.AccessLoader;
import control.InventoryManager;
import entity.Item;
import entity.Supplier;

import java.time.LocalDate;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        // Load items from Access
        List<Item> items = AccessLoader.loadItems();
        System.out.println("=== Loaded Items from Access ===");
        for (Item item : items) {
            System.out.println(item); // Make sure toString() is overridden in Item
        }

        // Create InventoryManager instance
        InventoryManager manager = new InventoryManager();

        // Step 1: Add supplier with valid numeric ID (must exist before item insertion)
        Supplier supplier = new Supplier("821", "NewSupplier1", "0528888887", "newsupplier1@mail.com", "821 Elm Street");
        manager.addSupplier(supplier);

        // Step 2: Add a new item referencing that supplier
        Item newItem = new Item(4020, "Dental Mirror", "Used for inspection", "Tools", 15,
                LocalDate.of(2025, 12, 31), supplier);
        manager.addItem(newItem);

        // Step 3: Update stock for the new item
        manager.updateStock(4020, 25);

        // Step 4: Assign supplier to item (demonstration)
        manager.assignSupplier(4020, supplier);

        // Step 5: Generate inventory alerts
        System.out.println("=== Inventory Alerts ===");
        List<Item> alerts = manager.generateAlerts();
        for (Item alert : alerts) {
            System.out.println(alert);
        }
    }
}

