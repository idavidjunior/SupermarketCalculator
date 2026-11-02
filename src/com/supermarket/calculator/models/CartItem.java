package com.supermarket.calculator.models;

public class CartItem {
    public static final int MAX_QUANTITY = 999;
    private String name;
    private double unitPrice;
    private int quantity;

    public CartItem(String name, double unitPrice, int quantity) {
        this.name = name;
        setUnitPrice(unitPrice);
        setQuantity(quantity);
    }

    public String getName() { return name; }
    public double getUnitPrice() { return unitPrice; }
    public int getQuantity() { return quantity; }
    public double getTotal() { return unitPrice * quantity; }

    public void setName(String name) { this.name = name; }
    public void setUnitPrice(double unitPrice) {
        this.unitPrice = Double.isFinite(unitPrice) && unitPrice >= 0 ? unitPrice : 0;
    }

    public void setQuantity(int quantity) {
        this.quantity = Math.min(MAX_QUANTITY, Math.max(1, quantity));
    }

    public void increment() { if (this.quantity < MAX_QUANTITY) this.quantity++; }
    public void decrement() { if (this.quantity > 1) this.quantity--; }
}
