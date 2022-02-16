package com.example.orderfood.DataLocal;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.orderfood.Model.ItemCart;
import com.example.orderfood.Model.User;
import com.google.gson.Gson;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DataLocalPreferences {

    private static final String USER_PREFERENCES = "USER_PREFERENCES";
    private static final String CART_PREFERENCES = "CART_PREFERENCES";
    private static final String TOTAL_CART = "TOTAL_CART";

    private static DataLocalPreferences instance;
    private FoodPreferences foodPreferences;

    public static void init(Context mContext)
    {
        instance = new DataLocalPreferences();
        instance.foodPreferences = new FoodPreferences(mContext);
    }

    public static DataLocalPreferences getInstance()
    {
        if (instance == null)
        {
            instance = new DataLocalPreferences();
        }
        return instance;
    }


    public static void saveTotal(long total)
    {
        DataLocalPreferences.getInstance().foodPreferences.putLongValue(TOTAL_CART,total);
    }

    public static long getTotal()
    {
        return DataLocalPreferences.getInstance().foodPreferences.getLongValue(TOTAL_CART);
    }




    public static void saveListItemCart(List<ItemCart> cartList)
    {
        Gson gson = new Gson();
        JsonArray jsonCart = gson.toJsonTree(cartList).getAsJsonArray();
        String strJsonArrayCart = jsonCart.toString();
        DataLocalPreferences.getInstance().foodPreferences.putStringValue(CART_PREFERENCES, strJsonArrayCart);
    }

    public static List<ItemCart> getListItemCart()
    {
        List<ItemCart> cartList = new ArrayList<>();
        Gson gson = new Gson();
        String strJsonArrayCart = DataLocalPreferences.getInstance().foodPreferences.getStringValue(CART_PREFERENCES);

        try {
            JSONArray jsonArray = new JSONArray(strJsonArrayCart);
            JSONObject jsonObject;
            ItemCart item;

            for (int i = 0; i < jsonArray.length(); i++)
            {
                jsonObject = jsonArray.getJSONObject(i);
                item = gson.fromJson(jsonObject.toString(),ItemCart.class);
                cartList.add(item);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return cartList;
    }
}
