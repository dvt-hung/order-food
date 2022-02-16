package com.example.orderfood.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.orderfood.Adapter.CategoryAdapter;
import com.example.orderfood.Adapter.SlideAdapter;
import com.example.orderfood.Adapter.SuggestFoodAdapter;
import com.example.orderfood.Activity.CategoryDetailsActivity;
import com.example.orderfood.Activity.DetailsFood;
import com.example.orderfood.Model.Category;
import com.example.orderfood.Model.Food;
import com.example.orderfood.Model.ItemSlide;
import com.example.orderfood.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Fragment_Home extends Fragment {

    private List<ItemSlide> itemSlideList;
    private List<Category> categoryList;
    private List<Food> foodList;
    private SlideAdapter adapterSlide;
    private SliderView sliderView;
    private RecyclerView recyclerCategory, recyclerSuggest;
    private CategoryAdapter categoryAdapter;
    private SuggestFoodAdapter adapterSuggest;
    FirebaseDatabase database;
    DatabaseReference reference;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home,container,false);
        // init View
        initView(view);

        // Slider banner
        adapterSlide = new SlideAdapter(view.getContext(),getListItemSlider());
        sliderView.setSliderAdapter(adapterSlide);
        sliderView.setIndicatorAnimation(IndicatorAnimationType.WORM);
        sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        sliderView.startAutoCycle();


        // Category
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("Category");
        categoryAdapter = new CategoryAdapter(requireActivity(), new CategoryAdapter.IItemCategoryListener() {
            @Override
            public void onClickCategoryListener(Category category) {
                showDetailsCategory(category);
            }
        });
        recyclerCategory.setAdapter(categoryAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext(),RecyclerView.HORIZONTAL,false);
        recyclerCategory.setLayoutManager(layoutManager);
        getListCategory();


        // List Suggest Food
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext(),RecyclerView.HORIZONTAL,false);
        recyclerSuggest.setLayoutManager(linearLayoutManager);
        adapterSuggest = new SuggestFoodAdapter(requireActivity(), new SuggestFoodAdapter.ISuggestListener() {
            @Override
            public void onClickDetailsCategory(Food food) {
                showDetailsFood(food);
            }
        });
        recyclerSuggest.setAdapter(adapterSuggest);
        getListSuggestFood();
        
        return view;
    }

    private void getListSuggestFood() {
        foodList = new ArrayList<>();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("Food");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                foodList.clear();
                for (DataSnapshot data : snapshot.getChildren())
                {
                    Food foods = data.getValue(Food.class);
                    if (foods != null) {
                        foods.setKeyFood(data.getKey());
                    }
                    foodList.add(foods);
                }
                Collections.shuffle(foodList);
                List<Food> nListFood = foodList.subList(0,10);
                adapterSuggest.setDataSuggest(nListFood);
                }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void initView(View view) {
        recyclerCategory    = view.findViewById(R.id.recycle_category);
        recyclerSuggest     = view.findViewById(R.id.recycle_suggest);
        sliderView          = view.findViewById(R.id.imageSlider);
    }

    private void showDetailsCategory(Category category) {
        Intent intent = new Intent(getContext(), CategoryDetailsActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("category",category);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void getListCategory() {
        categoryList = new ArrayList<>();

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                categoryList.clear();
                for(DataSnapshot data : snapshot.getChildren())
                {
                    Category category = data.getValue(Category.class);
                    if (category != null)
                    {
                        String keyCategory = data.getKey();
                        String nameCategory = category.getNameCategory();
                        String imgCategory = category.getImgCategory();
                        categoryList.add(new Category(imgCategory,nameCategory,keyCategory));
                        categoryAdapter.setDataCategory(categoryList);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private List<ItemSlide> getListItemSlider() {
        itemSlideList = new ArrayList<>();
        itemSlideList.add(new ItemSlide(R.drawable.banner1));
        itemSlideList.add(new ItemSlide(R.drawable.banner2));
        itemSlideList.add(new ItemSlide(R.drawable.banner3));
        return itemSlideList;
    }
    public void showDetailsFood(Food food)
    {
        DetailsFood detailsFood = DetailsFood.getInstance(food);
        detailsFood.show(getActivity().getSupportFragmentManager(),detailsFood.getTag());
    }
}
