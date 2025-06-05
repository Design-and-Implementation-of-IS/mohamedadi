package main;

import java.util.List;

import entity.Item;

public class Main {
    public static void main(String[] args) {
        List<Item> items = boundary.AccessLoader.loadItems();
        for (Item item : items) {
            System.out.println(item); // Ensure toString() is overridden in Item
        }
    }
}