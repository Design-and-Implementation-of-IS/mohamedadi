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
    public void updateStock()

    public List<Item> getItems() {
        return items;
    }

    public List<InventoryRecord> getInventoryRecords() {
        return inventoryRecords;
    }
}

