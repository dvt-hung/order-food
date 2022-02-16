package com.example.orderfood.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.orderfood.Model.Food;
import com.example.orderfood.R;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class FoodManagerAdapter extends RecyclerView.Adapter<FoodManagerAdapter.FoodManagerViewHolder>{

    private Context mContext;
    private List<Food> listFoodManager;
    private onClickFood onClickFood;

    public interface onClickFood{
        void onClickShowMenu(Food food);
    }
    public FoodManagerAdapter(Context mContext, onClickFood clickFood) {
        this.mContext = mContext;
        this.onClickFood = clickFood;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setListFoodManager(List<Food> list){
        this.listFoodManager = list;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public FoodManagerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_food_admin,parent,false);
        return new FoodManagerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodManagerViewHolder holder, int position) {
        Food food = listFoodManager.get(position);
        Locale locale = new Locale("vn");
        NumberFormat numberFormat = NumberFormat.getInstance(locale);
        if (food != null)
        {
            Glide.with(mContext).load(food.getImgFood()).into(holder.img_FoodManager);
            holder.txt_NameFoodManager.setText(food.getNameFood());
            holder.txt_PriceFoodManager.setText(String.format("%s đ", numberFormat.format(Long.valueOf(food.getPriceFood()))));
            holder.txt_DescriptionFoodManager.setText(food.getDescriptionFood());

            holder.layout_FoodManager.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickFood.onClickShowMenu(food);
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        if (listFoodManager != null)
        {
            return listFoodManager.size();
        }
        return 0;
    }

    public class FoodManagerViewHolder extends RecyclerView.ViewHolder {

        ImageView img_FoodManager;
        TextView txt_NameFoodManager, txt_PriceFoodManager, txt_DescriptionFoodManager;
        RelativeLayout layout_FoodManager;
        public FoodManagerViewHolder(@NonNull View itemView) {
            super(itemView);

            img_FoodManager = itemView.findViewById(R.id.img_FoodManager);
            txt_NameFoodManager = itemView.findViewById(R.id.txt_NameFoodManager);
            txt_PriceFoodManager = itemView.findViewById(R.id.txt_PriceFoodManager);
            txt_DescriptionFoodManager = itemView.findViewById(R.id.txt_DescriptionFoodManager);
            layout_FoodManager = itemView.findViewById(R.id.layout_FoodManager);

        }
    }
}
