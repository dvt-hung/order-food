package com.example.orderfood.Model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Food  implements Serializable {
    private String nameFood,imgFood,categoryID,descriptionFood,keyFood;
    private long priceFood;

    public Food() {
    }


    public Food(String nameFood, String imgFood, long priceFood, String categoryID, String descriptionFood) {
        this.nameFood = nameFood;
        this.imgFood = imgFood;
        this.priceFood = priceFood;
        this.categoryID = categoryID;
        this.descriptionFood = descriptionFood;
    }



    public String getDescriptionFood() {
        return descriptionFood;
    }

    public void setDescriptionFood(String descriptionFood) {
        this.descriptionFood = descriptionFood;
    }

    public String getNameFood() {
        return nameFood;
    }

    public void setNameFood(String nameFood) {
        this.nameFood = nameFood;
    }

    public String getImgFood() {
        return imgFood;
    }

    public void setImgFood(String imgFood) {
        this.imgFood = imgFood;
    }

    public long getPriceFood() {
        return priceFood;
    }

    public void setPriceFood(long priceFood) {
        this.priceFood = priceFood;
    }

    public String getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(String categoryID) {
        this.categoryID = categoryID;
    }

    public String getKeyFood() {
        return keyFood;
    }

    public void setKeyFood(String keyFood) {
        this.keyFood = keyFood;
    }


}
