package entity;

import java.time.LocalDate;

public class InventoryRecord {
    private Item item;
    private LocalDate receivedDate;
    private int stockLevel;

    public InventoryRecord(Item item, LocalDate receivedDate, int stockLevel) {
        this.item = item;
        this.receivedDate = receivedDate;
        this.stockLevel = stockLevel;
    }

    @Override
    public String toString() {
        return "InventoryRecord [item=" + item + ", receivedDate=" + receivedDate + ", stockLevel=" + stockLevel + "]";
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public LocalDate getReceivedDate() {
        return receivedDate;
    }

    public void setReceivedDate(LocalDate receivedDate) {
        this.receivedDate = receivedDate;
    }

    public int getStockLevel() {
        return stockLevel;
    }

    public void setStockLevel(int stockLevel) {
        this.stockLevel = stockLevel;
    }
}
