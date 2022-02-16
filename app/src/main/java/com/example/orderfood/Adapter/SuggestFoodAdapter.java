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
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class SuggestFoodAdapter extends RecyclerView.Adapter<SuggestFoodAdapter.SuggestViewHolder>{

    private Context mContext;
    private List<Food> foodList;
    private ISuggestListener listener;

    public interface ISuggestListener {
        void onClickDetailsCategory(Food food);
    }
    public SuggestFoodAdapter(Context mContext, ISuggestListener listener) {
        this.mContext = mContext;
        this.listener = listener;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setDataSuggest(List<Food> List)
    {
        this.foodList = List;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public SuggestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_suggest_food,parent,false);
        return new SuggestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SuggestViewHolder holder, int position) {

        if (position <= 10)
        {
            Food food = foodList.get(position);
            Locale locale = new Locale("vn");
            NumberFormat numberFormat = NumberFormat.getInstance(locale);
            if (food != null)
            {
                holder.txtNameSuggest.setText(food.getNameFood());
                holder.txtPriceSuggest.setText(String.format("%s đ", numberFormat.format(Long.valueOf(food.getPriceFood()))));
                Glide.with(mContext).load(food.getImgFood()).into(holder.imgSuggest);
                holder.itemSuggest.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onClickDetailsCategory(food);
                    }
                });
            }
        }

    }

    @Override
    public int getItemCount() {
        if (foodList != null)
        {
            return 10;
        }
        return 0;
    }

    public class SuggestViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imgSuggest;
        private final TextView txtNameSuggest;
        private final TextView txtPriceSuggest;
        private final CardView itemSuggest;
        public SuggestViewHolder(@NonNull View itemView) {
            super(itemView);
            imgSuggest          = itemView.findViewById(R.id.imgSuggest);
            txtNameSuggest      = itemView.findViewById(R.id.txtNameSuggest);
            txtPriceSuggest     = itemView.findViewById(R.id.txtPriceSuggest);
            itemSuggest         = itemView.findViewById(R.id.itemSuggest);
        }
    }
}
