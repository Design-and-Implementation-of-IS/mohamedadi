package entity;

import java.time.LocalDate;

public class Item {
    private int id;
    private String name;
    private String description;
    private String category;
    private int quantity;
    private LocalDate expiryDate;
    private Supplier supplier;

    public Item(int id, String name, String description, String category, int quantity, LocalDate expiryDate, Supplier supplier) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.category = category;
        this.quantity = quantity;
        this.expiryDate = expiryDate;
        this.supplier = supplier;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getCategory() { return category; }
    public int getQuantity() { return quantity; }
    public LocalDate getExpiryDate() { return expiryDate; }
    public Supplier getSupplier() { return supplier; }

    @Override
    public String toString() {
        return "Item{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", category='" + category + '\'' +
                ", quantity=" + quantity +
                ", expiryDate=" + expiryDate +
                ", supplier=" + (supplier != null ? supplier.getName() : "N/A") +
                '}';
    }
}
