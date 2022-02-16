package com.example.orderfood.DataLocal;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.orderfood.Model.ItemCart;
import com.example.orderfood.Model.User;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class FoodPreferences {

    private static final String FOOD_PREFERENCES = "FOOD_PREFERENCES";
    private Context mContext;

    public FoodPreferences(Context mContext) {
        this.mContext = mContext;
    }

    public void putLongValue(String key,long value)
    {
        SharedPreferences mShared = mContext.getSharedPreferences(FOOD_PREFERENCES,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mShared.edit();
        editor.putLong(key,value);
        editor.apply();
    }

    public long getLongValue(String key)
    {
        SharedPreferences mShared = mContext.getSharedPreferences(FOOD_PREFERENCES,Context.MODE_PRIVATE);
        return mShared.getLong(key,0);
    }

    public void putStringValue(String key, String value)
    {
        SharedPreferences mShared = mContext.getSharedPreferences(FOOD_PREFERENCES,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mShared.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public String getStringValue(String key)
    {
        SharedPreferences mShared = mContext.getSharedPreferences(FOOD_PREFERENCES,Context.MODE_PRIVATE);
        return mShared.getString(key,"");
    }



}
