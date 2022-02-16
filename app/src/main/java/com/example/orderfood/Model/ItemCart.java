package com.example.orderfood.Model;

import androidx.annotation.Nullable;

import java.io.Serializable;

public class ItemCart implements Serializable {
    private Food food;
    private int Quantity;
    private long totalPrice;

    public ItemCart() {
    }

    public ItemCart(Food food, int quantity) {
        this.food = food;
        Quantity = quantity;
    }

    public long getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(long totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Food getFood() {
        return food;
    }

    public void setFood(Food food) {
        this.food = food;
    }

    public int getQuantity() {
        return Quantity;
    }

    public void setQuantity(int quantity) {
        Quantity = quantity;
    }

}
