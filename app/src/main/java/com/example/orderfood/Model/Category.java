package com.example.orderfood.Model;

import com.example.orderfood.Adapter.CategoryAdapter;

import java.io.Serializable;

public class Category implements Serializable {
    String imgCategory, nameCategory, keyCategory;

    public Category() {
    }

    public Category(String imgCategory, String nameCategory) {
        this.imgCategory = imgCategory;
        this.nameCategory = nameCategory;
    }

    public Category(String imgCategory, String nameCategory, String keyCategory) {
        this.imgCategory = imgCategory;
        this.nameCategory = nameCategory;
        this.keyCategory = keyCategory;
    }


    public String getKeyCategory() {
        return keyCategory;
    }

    public void setKeyCategory(String keyCategory) {
        this.keyCategory = keyCategory;
    }

    public String getImgCategory() {
        return imgCategory;
    }

    public void setImgCategory(String imgCategory) {
        this.imgCategory = imgCategory;
    }

    public String getNameCategory() {
        return nameCategory;
    }

    public void setNameCategory(String nameCategory) {
        this.nameCategory = nameCategory;
    }
}
