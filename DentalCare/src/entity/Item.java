package entity;

import java.time.LocalDate;

public class Item {
    private String id;
    private String name;
    private String description;
    private String category;
    private int quantity;
    private LocalDate expiryDate;
    private Supplier supplier;

    public Item(String id, String name, String description, String category,
                int quantity, LocalDate expiryDate, Supplier supplier) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.category = category;
        this.quantity = quantity;
        this.expiryDate = expiryDate;
        this.supplier = supplier;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getCategory() {
        return category;
    }

    public int getQuantity() {
        return quantity;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }

    public boolean isExpired() {
        return expiryDate != null && expiryDate.isBefore(LocalDate.now());
    }

    @Override
    public String toString() {
        return "Item [id=" + id + ", name=" + name + ", category=" + category +
               ", quantity=" + quantity + ", expiryDate=" + expiryDate + ", supplier=" + supplier.getName() + "]";
    }
}
