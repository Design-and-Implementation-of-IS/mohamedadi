package control;

import entity.Item;
import entity.Supplier;
import entity.InventoryRecord;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class InventoryManager {
    private List<Item> items;
    private List<InventoryRecord> inventoryRecords;

    public InventoryManager(List<Item> items, List<InventoryRecord> inventoryRecords) {
        this.items = items != null ? items : new ArrayList<>();
        this.inventoryRecords = inventoryRecords != null ? inventoryRecords : new ArrayList<>();
    }

    public void addItem(Item item) {
        items.add(item);
        InventoryRecord record = new InventoryRecord(item, LocalDate.now(), item.getQuantity());
        inventoryRecords.add(record);
    }
    public void updateStock(String itemId, int newQuantity) {
    	for (InventoryRecord record : inventoryRecords) {
    		if (record.getItem().getId().equals(itemId)) {
    			record.setStockLevel(newQuantity);
    			return;
    		}
    	}
    }
    public void assignSupplier(String itemId, Supplier supplier) {
        for (Item item : items) {
            if (item.getId().equals(itemId)) {
                item.setSupplier(supplier);
                return;
            }
        }
    }
    public List<Item> generateAlerts() {
        InventoryAlertGenerator alertGen = new InventoryAlertGenerator();
        List<Item> alerts = new ArrayList<>();
        alerts.addAll(alertGen.checkLowStock(items, 10));  // Example threshold
        alerts.addAll(alertGen.checkExpiringItems(items));
        return alerts;
    }

    public List<Item> getItems() {
        return items;
    }

    public List<InventoryRecord> getInventoryRecords() {
        return inventoryRecords;
    }
}

