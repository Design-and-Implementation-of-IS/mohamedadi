package control;

import entity.Item;
import java.util.List;
import java.util.ArrayList;
import java.time.LocalDate;

public class InventoryAlertGenerator {

	public List<Item> checkLowStock(List<Item> items, int threshold) {
        List<Item> lowStockItems = new ArrayList<>();
        for (Item item : items) {
            if (item.getQuantity() < threshold) {
                lowStockItems.add(item);
            }
        }
        return lowStockItems;
    }
	
	  public List<Item> checkExpiringItems(List<Item> items) {
	        List<Item> expiringItems = new ArrayList<>();
	        LocalDate today = LocalDate.now();

	        for (Item item : items) {
	            if (item.getExpiryDate() != null && item.getExpiryDate().isBefore(today)) {
	                expiringItems.add(item);
	            }
	        }
	        return expiringItems;
	    }
	}

