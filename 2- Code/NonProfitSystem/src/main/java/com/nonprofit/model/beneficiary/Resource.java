package com.nonprofit.model.beneficiary;

import com.nonprofit.model.common.Identifiable;
import com.nonprofit.model.lookup.ResourceCategory;

/**
 * Something the organization gives out (food boxes, school kits ...).
 */
public class Resource implements Identifiable {

    private int id;
    private String name;
    private String unit;
    private int stockQuantity;
    private ResourceCategory category;

    public Resource(int id, String name, String unit, int stockQuantity, ResourceCategory category) {
        this.id = id;
        this.name = name;
        this.unit = unit;
        this.stockQuantity = stockQuantity;
        this.category = category;
    }

    public boolean hasStock(int quantity) {
        return quantity > 0 && stockQuantity >= quantity;
    }

    public void reduceStock(int quantity) {
        stockQuantity -= quantity;
    }

    public void addStock(int quantity) {
        stockQuantity += quantity;
    }

    @Override
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getUnit() {
        return unit;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public ResourceCategory getCategory() {
        return category;
    }
}
