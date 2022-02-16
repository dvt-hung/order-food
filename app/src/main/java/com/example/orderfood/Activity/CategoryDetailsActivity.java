package com.example.orderfood.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.TextView;

import com.example.orderfood.Adapter.DetailsCategoryAdapter;
import com.example.orderfood.Model.Category;
import com.example.orderfood.Model.Food;
import com.example.orderfood.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CategoryDetailsActivity extends AppCompatActivity {

    private TextView txtNameCategory;
    private RecyclerView recyclerDetailsCategory;
    private DetailsCategoryAdapter adapterDetailsCategory;
    private List<Food> listFood;
    private FirebaseDatabase database;
    private DatabaseReference reference;
    private String categoryID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_details);

        // init View
        initView();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("Food");
        Bundle bundle = getIntent().getExtras();
        Category category = (Category) bundle.get("category");
        txtNameCategory.setText(category.getNameCategory());
        categoryID = category.getKeyCategory();
        // GirdLayout Manager
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,2);
        recyclerDetailsCategory.setLayoutManager(gridLayoutManager);
        // Adapter Details Category
        adapterDetailsCategory = new DetailsCategoryAdapter(this, new DetailsCategoryAdapter.IItemDetailsCategoryListener() {
            @Override
            public void onClickDetailsCategory(Food food) {
                showDetailsFood(food);
            }
        });
        recyclerDetailsCategory.setAdapter(adapterDetailsCategory);

        // Load List Food By CategoryID
        loadListFood();
    }

    private void loadListFood() {
        listFood = new ArrayList<>();
        // Read data
        reference.orderByChild("categoryID").equalTo(categoryID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listFood.clear();
                for (DataSnapshot data : snapshot.getChildren())
                {
                    Food food = data.getValue(Food.class);
                    if (food != null)
                    {
                        food.setKeyFood(data.getKey());
                    }
                    listFood.add(food);
                    adapterDetailsCategory.setDataDetailsCategory(listFood);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void initView() {
        txtNameCategory = (TextView) findViewById(R.id.txtNameCategory);
        recyclerDetailsCategory = (RecyclerView) findViewById(R.id.recycleDetailsCategory);
    }
    private void showDetailsFood(Food food)
    {
        DetailsFood detailsFood = DetailsFood.getInstance(food);
        detailsFood.show(getSupportFragmentManager(),detailsFood.getTag());
    }
}