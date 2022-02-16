package com.example.orderfood.Adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.orderfood.Model.ItemCart;
import com.example.orderfood.R;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class FoodDetailOrderAdapter extends RecyclerView.Adapter<FoodDetailOrderAdapter.FoodOrderViewHolder>{

    private final List<ItemCart> listFoodOrder;


    public FoodDetailOrderAdapter(List<ItemCart> listFoodOrder) {
        this.listFoodOrder = listFoodOrder;
    }

    @NonNull
    @Override
    public FoodOrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_food_detail_order,parent,false);
        return new FoodOrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodOrderViewHolder holder, int position) {
        ItemCart foodItem = listFoodOrder.get(position);
        if (foodItem != null)
        {
            holder.txt_Name_Food_Order.setText(foodItem.getFood().getNameFood());
            holder.txt_Quantity_Food_Order.setText(String.valueOf(foodItem.getQuantity()));
            Locale locale = new Locale("vn");
            NumberFormat numberFormat = NumberFormat.getInstance(locale);
            holder.txt_Price_Food_Order.setText(numberFormat.format(foodItem.getFood().getPriceFood()));

        }
    }

    @Override
    public int getItemCount() {
        if (listFoodOrder != null)
        {
            return listFoodOrder.size();
        }
        return 0;
    }

    public static class FoodOrderViewHolder extends RecyclerView.ViewHolder {
        TextView txt_Name_Food_Order,txt_Quantity_Food_Order,txt_Price_Food_Order;
        public FoodOrderViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_Name_Food_Order     = itemView.findViewById(R.id.txt_Name_Food_Order);
            txt_Quantity_Food_Order = itemView.findViewById(R.id.txt_Quantity_Food_Order);
            txt_Price_Food_Order    = itemView.findViewById(R.id.txt_Price_Food_Order);
        }
    }
}
