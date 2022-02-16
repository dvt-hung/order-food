package com.example.orderfood.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.chauthai.swipereveallayout.ViewBinderHelper;
import com.example.orderfood.DataLocal.DataLocalPreferences;
import com.example.orderfood.Fragment.Fragment_Cart;
import com.example.orderfood.Model.Food;
import com.example.orderfood.Model.ItemCart;
import com.example.orderfood.R;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder>{
    private final Context mContext;
    private List<ItemCart> itemCartList;
    private final ViewBinderHelper viewBinderHelper = new ViewBinderHelper();
    private long t = 0;

    public CartAdapter(Context mContext) {
        this.mContext = mContext;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setDataItemCart(List<ItemCart> itemList)
    {
        this.itemCartList = itemList;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart,parent,false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, @SuppressLint("RecyclerView") int position) {
        ItemCart item = itemCartList.get(position);
        Locale locale = new Locale("vn");
        NumberFormat numberFormat = NumberFormat.getInstance(locale);
        if (item != null)
        {
            Food food = item.getFood();
            viewBinderHelper.bind(holder.layoutItemCart, food.getKeyFood());
            Glide.with(mContext).load(food.getImgFood()).into(holder.imgItemCart);
            holder.txtNameItemCart.setText(food.getNameFood());
            holder.txtPriceItemCart.setText(String.format("%s đ", numberFormat.format(Long.valueOf(food.getPriceFood()))));
            holder.txtDescriptionItemCart.setText(food.getDescriptionFood());
            holder.txtQuantityItemCart.setText(String.valueOf(item.getQuantity()));
            // Tăng giảm số lượng

            holder.btnAddQuantityItemCart.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onClick(View v) {

                    int current = item.getQuantity();
                    item.setQuantity(current + 1);
                    holder.txtQuantityItemCart.setText(item.getQuantity()+"");
                    Fragment_Cart.total += food.getPriceFood();
                    Fragment_Cart.loadTotalPrice();
                }
            });
            holder.btnMinusQuantityItemCart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int current = item.getQuantity();
                    if (current > 1)
                    {
                        item.setQuantity(current - 1);
                        holder.txtQuantityItemCart.setText(String.valueOf(item.getQuantity()));
                        Fragment_Cart.total -= food.getPriceFood();
                        Fragment_Cart.loadTotalPrice();
                    }


                }
            });

            holder.deleteLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    long temp = item.getFood().getPriceFood() * item.getQuantity();
                    Fragment_Cart.total -= temp;
                    itemCartList.remove(holder.getAdapterPosition());
                    notifyItemRemoved(holder.getAdapterPosition());
                    Fragment_Cart.loadTotalPrice();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if (itemCartList != null)
        {
            return itemCartList.size();
        }
        return 0;
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        SwipeRevealLayout layoutItemCart;
        LinearLayout deleteLayout;
        ImageView imgItemCart;
        TextView txtNameItemCart,txtPriceItemCart,txtDescriptionItemCart,txtQuantityItemCart;
        ImageButton btnAddQuantityItemCart, btnMinusQuantityItemCart;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);

            layoutItemCart = itemView.findViewById(R.id.layoutItemCart);
            deleteLayout = itemView.findViewById(R.id.deleteItemCart);
            imgItemCart = itemView.findViewById(R.id.imgItemCart);
            txtNameItemCart = itemView.findViewById(R.id.txtNameItemCart);
            txtPriceItemCart = itemView.findViewById(R.id.txtPriceItemCart);
            txtDescriptionItemCart = itemView.findViewById(R.id.txtDescriptionItemCart);
            txtQuantityItemCart = itemView.findViewById(R.id.txtQuantityItemCart);
            btnAddQuantityItemCart = itemView.findViewById(R.id.btn_AddQuantityItemCart);
            btnMinusQuantityItemCart = itemView.findViewById(R.id.btn_MinusQuantityItemCart);
        }
    }
}
