package com.example.orderfood.Model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.List;

public class Order implements Serializable {
    private String idOrder;
    private User user;
    private List<ItemCart> itemCartList;
    private int statusOrder;
    private long totalOrder;
    private String addressOder;
    private String phoneOrder;
    private String noteOrder;
    private long dateOrder;
    private boolean isSeen;

    public Order() {
    }


    public Order(User user, List<ItemCart> itemCartList, int statusOrder, long totalOrder, String addressOder, String phoneOrder, String noteOrder, long dateOrder, boolean isSeen) {
        this.user = user;
        this.itemCartList = itemCartList;
        this.statusOrder = statusOrder;
        this.totalOrder = totalOrder;
        this.addressOder = addressOder;
        this.phoneOrder = phoneOrder;
        this.noteOrder = noteOrder;
        this.dateOrder = dateOrder;
        this.isSeen = isSeen;
    }

    public boolean isSeen() {
        return isSeen;
    }

    public void setSeen(boolean seen) {
        isSeen = seen;
    }

    public String getNoteOrder() {
        return noteOrder;
    }

    public void setNoteOrder(String noteOrder) {
        this.noteOrder = noteOrder;
    }

    public long getDateOrder() {
        return dateOrder;
    }

    public void setDateOrder(long dateOrder) {
        this.dateOrder = dateOrder;
    }

    public String getPhoneOrder() {
        return phoneOrder;
    }

    public void setPhoneOrder(String phoneOrder) {
        this.phoneOrder = phoneOrder;
    }

    public String getAddressOder() {
        return addressOder;
    }

    public void setAddressOder(String addressOder) {
        this.addressOder = addressOder;
    }

    public long getTotalOrder() {
        return totalOrder;
    }

    public void setTotalOrder(long totalOrder) {
        this.totalOrder = totalOrder;
    }

    public String getIdOrder() {
        return idOrder;
    }

    public void setIdOrder(String idOrder) {
        this.idOrder = idOrder;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<ItemCart> getItemCartList() {
        return itemCartList;
    }

    public void setItemCartList(List<ItemCart> itemCartList) {
        this.itemCartList = itemCartList;
    }

    public int getStatusOrder() {
        return statusOrder;
    }

    public void setStatusOrder(int statusOrder) {
        this.statusOrder = statusOrder;
    }


}
