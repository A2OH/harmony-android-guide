package com.example.mockdonalds;

/** POJO: menu item with id, name, description, price, category. */
public class MenuItem {
    public final int id;
    public final String name;
    public final String description;
    public final double price;
    public final String category;

    public MenuItem(int id, String name, String description, double price, String category) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
    }

    public String getPriceString() {
        return String.format("$%.2f", price);
    }

    @Override
    public String toString() {
        return name + " " + getPriceString();
    }
}
