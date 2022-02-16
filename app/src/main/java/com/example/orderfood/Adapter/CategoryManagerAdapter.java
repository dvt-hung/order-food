package com.example.orderfood.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.orderfood.Fragment.AdminFragment_Category;
import com.example.orderfood.Model.Category;
import com.example.orderfood.Model.Food;
import com.example.orderfood.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CategoryManagerAdapter extends RecyclerView.Adapter<CategoryManagerAdapter.CategoryManagerViewHolder> {

    private Context mContext;
    private List<Category> listCategoryManager;
    public FoodManagerAdapter foodManagerAdapter;
    private IEditCategory editCategory;
    private DatabaseReference referenceDBFood;


    public interface IEditCategory
    {
        void showBottomMenu(Category category);

    }


    public CategoryManagerAdapter(Context mContext,IEditCategory editCategory) {
        this.mContext = mContext;
        this.editCategory = editCategory;
    }
    @SuppressLint("NotifyDataSetChanged")
    public void setDataCategoryManager(List<Category> list)
    {
        this.listCategoryManager = list;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public CategoryManagerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category_admin,parent,false);
        return new CategoryManagerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryManagerViewHolder holder, int position) {
        Category category = listCategoryManager.get(position);
        if (category != null)
        {
            holder.txt_NameCategoryManager.setText(category.getNameCategory());
            Glide.with(mContext).load(category.getImgCategory()).into(holder.img_CategoryManager);


            holder.layout_CategoryManager.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    editCategory.showBottomMenu(category);
                }
            });
        }
    }


    @Override
    public int getItemCount() {
        if (listCategoryManager != null)
        {
            return listCategoryManager.size();
        }
        return 0;
    }

    public class CategoryManagerViewHolder extends RecyclerView.ViewHolder {

        ImageView img_CategoryManager;
        TextView txt_NameCategoryManager;
        LinearLayout layout_CategoryManager;
        public CategoryManagerViewHolder(@NonNull View itemView) {
            super(itemView);
            img_CategoryManager = itemView.findViewById(R.id.img_CategoryManager);
            txt_NameCategoryManager = itemView.findViewById(R.id.txt_NameCategoryManager);
            layout_CategoryManager = itemView.findViewById(R.id.layout_CategoryManager);
        }
    }



}

