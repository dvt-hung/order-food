package com.example.orderfood.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.orderfood.Model.Category;
import com.example.orderfood.R;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private final Context mContext;
    private List<Category> categoryList;
    private final IItemCategoryListener categoryListener;
    public interface IItemCategoryListener{
        void onClickCategoryListener(Category category);
    }

    public CategoryAdapter(Context Context, IItemCategoryListener listener) {
        this.mContext = Context;
        this.categoryListener = listener;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setDataCategory(List<Category> List) {
        this.categoryList = List;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = categoryList.get(position);
        if (category != null) {
            Glide.with(mContext).load(category.getImgCategory()).into(holder.imgCategory);
            holder.txtNameCategory.setText(category.getNameCategory());
            holder.itemCategory.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    categoryListener.onClickCategoryListener(category);
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        if (categoryList != null) {
            return categoryList.size();

        }
        return 0;
    }


    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        ImageView imgCategory;
        TextView txtNameCategory;
        LinearLayout itemCategory;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            imgCategory = itemView.findViewById(R.id.img_Category);
            txtNameCategory = itemView.findViewById(R.id.txt_nameCategory);
            itemCategory = itemView.findViewById(R.id.itemCategory);
        }
    }
}
