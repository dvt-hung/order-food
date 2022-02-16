package com.example.orderfood.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.orderfood.Model.Food;
import com.example.orderfood.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class DetailsCategoryAdapter extends RecyclerView.Adapter<DetailsCategoryAdapter.DetailsCategoryViewHolder> {

    private final Context mContext;
    private List<Food> listFood;
    private final IItemDetailsCategoryListener detailsCategoryListener;
    public interface IItemDetailsCategoryListener {
        void onClickDetailsCategory(Food food);
    }

    public DetailsCategoryAdapter(Context mContext,IItemDetailsCategoryListener detailsCategoryListener ) {
        this.mContext = mContext;
        this.detailsCategoryListener = detailsCategoryListener;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setDataDetailsCategory(List<Food> list)
    {
        this.listFood = list;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public DetailsCategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_details_category,parent,false);
        return new DetailsCategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DetailsCategoryViewHolder holder, int position) {

        Food food = listFood.get(position);

        if (food != null)
        {
            Locale locale = new Locale("vn");
            NumberFormat numberFormat = NumberFormat.getInstance(locale);
            holder.txtNameDetailsCategory.setText(food.getNameFood());
            holder.txtPriceDetailsCategory.setText(String.format("%s đ", numberFormat.format(Long.valueOf(food.getPriceFood()))));
            Glide.with(mContext).load(food.getImgFood()).into(holder.imgDetailsCategory);
            holder.itemDetailsCategory.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    detailsCategoryListener.onClickDetailsCategory(food);
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        if (listFood != null)
        {
            return listFood.size();
        }
        return 0;
    }

    public static class DetailsCategoryViewHolder extends RecyclerView.ViewHolder {

        private final ImageView imgDetailsCategory;
        private final TextView txtNameDetailsCategory;
        private final TextView txtPriceDetailsCategory;
        private final CardView itemDetailsCategory;

        public DetailsCategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            imgDetailsCategory          = itemView.findViewById(R.id.imgDetailsCategory);
            txtNameDetailsCategory      = itemView.findViewById(R.id.txtNameDetailsCategory);
            txtPriceDetailsCategory     = itemView.findViewById(R.id.txtPriceDetailsCategory);
            itemDetailsCategory         = itemView.findViewById(R.id.itemDetailsCategory);
        }
    }
}
