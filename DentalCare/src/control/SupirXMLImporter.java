package control;

import boundary.XMLParser;
import entity.Item;

import java.util.List;

public class SupirXMLImporter {
    private final XMLParser parser;
    private final InventoryManager inventoryManager;

    public SupirXMLImporter(XMLParser parser, InventoryManager inventoryManager) {
        this.parser = parser;
        this.inventoryManager = inventoryManager;
    }

    public void importFromSupir(String xmlFilePath) {
        List<Item> items = parser.parse(xmlFilePath);

        for (Item item : items) {
            inventoryManager.addSupplier(item.getSupplier()); // Checks for existing supplier
            inventoryManager.addItem(item);                   // Checks for duplicate item
        }

        System.out.println("✅ Supir XML data import completed: " + items.size() + " items imported.");
    }
}

