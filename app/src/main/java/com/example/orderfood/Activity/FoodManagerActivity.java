package com.example.orderfood.Activity;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.orderfood.Adapter.FoodManagerAdapter;
import com.example.orderfood.Model.Category;
import com.example.orderfood.Model.Food;
import com.example.orderfood.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

public class FoodManagerActivity extends AppCompatActivity {

    private TextView txtNameCategoryDetails;
    private RecyclerView recyclerFoodManager;
    private FloatingActionButton btn_AddFood;
    private FoodManagerAdapter foodManagerAdapter;
    private List<Food> listFood;
    private Uri uriImageGallery;
    private ImageView imgGallery;
    private FirebaseStorage storageFood;
    private DatabaseReference referenceDBFood;
    private StorageReference referenceStorageFood;

    private final ActivityResultLauncher<Intent> intentGallery = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null)
                    {
                        imgGallery.setImageURI(result.getData().getData());
                        uriImageGallery = result.getData().getData();
                    }
                }
            });

    private final ActivityResultLauncher<String> galleryRequest = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
        @Override
        public void onActivityResult(Boolean result) {
            if (!result)
            {
                Toast.makeText(getApplicationContext(), "Không cho phép", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(getApplicationContext(), "Đã cho phép", Toast.LENGTH_SHORT).show();
                openGallery();
            }
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_manager);

        // init Database
         FirebaseDatabase database = FirebaseDatabase.getInstance();
         referenceDBFood = database.getReference("Food");

         // init Storage
        storageFood = FirebaseStorage.getInstance("gs://order-food-1f7a5.appspot.com");
        referenceStorageFood = storageFood.getReference();
        // init View
        initView();

        // Get Category
        Intent intent = getIntent();
        Category category = (Category) intent.getSerializableExtra("obj_category");
        // Set name
        txtNameCategoryDetails.setText(category.getNameCategory());

        // Adapter Food Manager
        foodManagerAdapter = new FoodManagerAdapter(this, new FoodManagerAdapter.onClickFood() {
            @Override
            public void onClickShowMenu(Food food) {
                bottomMenuFood(food);
            }
        });
        recyclerFoodManager.setAdapter(foodManagerAdapter);

        // Layout Manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(this,RecyclerView.VERTICAL,false);
        recyclerFoodManager.setLayoutManager(layoutManager);


        //Load Food
        loadFood(category);


        // Button Add Food
        btn_AddFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogAddFood(category);
            }
        });

    }


    // ============  Bottom Sheet Food ============
    private void bottomMenuFood(Food food) {

        BottomSheetDialog bottomMenuFood = new BottomSheetDialog(this);
        bottomMenuFood.setContentView(R.layout.menu_food_bottom_sheet);

        // init view
        LinearLayout bottom_menu_UpdateFood = bottomMenuFood.findViewById(R.id.bottom_menu_UpdateFood);
        LinearLayout bottom_menu_DeleteFood = bottomMenuFood.findViewById(R.id.bottom_menu_DeleteFood);


        // Update Food
        if (bottom_menu_UpdateFood != null) {
            bottom_menu_UpdateFood.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDialogUpdateFood(food);
                }
            });
        }

        // Delete Food
        if (bottom_menu_DeleteFood != null) {
            bottom_menu_DeleteFood.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDialogDeleteFood(food);
                }
            });
        }
        bottomMenuFood.show();

    }

    // ============  Dialog Update Food ============
    private void showDialogDeleteFood(Food food) {

        // Progress dialog
        ProgressDialog progressDialogDelete = new ProgressDialog(FoodManagerActivity.this);
        progressDialogDelete.setMessage("Đang xóa dữ liệu");
        progressDialogDelete.show();

        // Alert dialog
        new AlertDialog.Builder(FoodManagerActivity.this)
                .setTitle("Xóa dữ liệu")
                .setMessage("Bạn có muốn xóa dữ liệu này không ?")
                .setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        referenceDBFood.child(food.getKeyFood()).removeValue(new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                Toast.makeText(getApplicationContext(), "Đã xóa thành công", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                                progressDialogDelete.dismiss();
                            }
                        });
                    }
                })
                .setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        progressDialogDelete.dismiss();
                    }
                })
                .show();
    }

    // ============  Dialog Update Food ============
    private void showDialogUpdateFood(Food food) {
        Dialog dialogUpdateFood = new Dialog(this);
        dialogUpdateFood.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogUpdateFood.setCanceledOnTouchOutside(false);
        dialogUpdateFood.setContentView(R.layout.dialog_add_food);

        Window windowAddFood = dialogUpdateFood.getWindow();
        if (windowAddFood == null)
        {
            return;
        }
        windowAddFood.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        windowAddFood.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        imgGallery                                  = dialogUpdateFood.findViewById(R.id.img_AddFood);
        TextInputEditText edt_NameUpdateFood           = dialogUpdateFood.findViewById(R.id.edt_NameAddFood);
        TextInputEditText edt_PriceUpdateFood          = dialogUpdateFood.findViewById(R.id.edt_PriceAddFood);
        TextInputEditText edt_DescriptionUpdateFood    = dialogUpdateFood.findViewById(R.id.edt_DescriptionAddFood);
        Button btn_CancelUpdateFood                    = dialogUpdateFood.findViewById(R.id.btn_CancelAddFood);
        Button btn_ConfirmUpdateFood                   = dialogUpdateFood.findViewById(R.id.btn_ConfirmAddFood);
        TextInputLayout layoutNameFood              = dialogUpdateFood.findViewById(R.id.edtLayout_NameFood);
        TextInputLayout layoutPriceFood             = dialogUpdateFood.findViewById(R.id.edtLayout_PriceFood);
        TextInputLayout layoutDescriptionFood       = dialogUpdateFood.findViewById(R.id.edtLayout_DescriptionFood);

        // Set Old Value
        String nameFood = food.getNameFood().trim();
        long priceFood = food.getPriceFood();
        String desFood = food.getDescriptionFood().trim();


        Glide.with(this).load(food.getImgFood()).into(imgGallery);
        edt_NameUpdateFood.setText(nameFood);
        edt_PriceUpdateFood.setText(String.valueOf(priceFood));
        edt_DescriptionUpdateFood.setText(desFood);


        // Check value
        checkValueAddFood(layoutNameFood,edt_NameUpdateFood);
        checkValueAddFood(layoutPriceFood,edt_PriceUpdateFood);
        checkValueAddFood(layoutDescriptionFood,edt_DescriptionUpdateFood);

        // Choose Image For Food
        imgGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPermissionGallery();
            }
        });


        // Button Cancel
        btn_CancelUpdateFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogUpdateFood.dismiss();
            }
        });

        // Button Confirm
        btn_ConfirmUpdateFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = Objects.requireNonNull(edt_NameUpdateFood.getText()).toString().trim();
                String price = Objects.requireNonNull(edt_PriceUpdateFood.getText()).toString().trim();
                String des = Objects.requireNonNull(edt_DescriptionUpdateFood.getText()).toString().trim();
                updateFood(food,name,Long.parseLong(price),des);
            }
        });
        dialogUpdateFood.show();
    }

    // =========== F Update Food =========
    private void updateFood(Food food, String nameFood, long priceFood, String desFood) {

        ProgressDialog dialogUpdate = new ProgressDialog(FoodManagerActivity.this);
        dialogUpdate.setMessage("Đang chỉnh sửa dữ liệu");
        dialogUpdate.show();
        if (uriImageGallery == null)
        {
            Food foodNew = new Food(nameFood,food.getImgFood(),priceFood,food.getCategoryID(),desFood);
            referenceDBFood.child(food.getKeyFood()).setValue(foodNew).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    dialogUpdate.dismiss();
                    Toast.makeText(getApplicationContext(), "Cập nhật thành công 1" + food.getKeyFood(), Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    dialogUpdate.dismiss();
                    Toast.makeText(getApplicationContext(), "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
                }
            });
        }
        else {
            //1. Xóa hình cũ
            StorageReference referenceUpdateFood = storageFood.getReferenceFromUrl(food.getImgFood());
            referenceUpdateFood.delete();

            // 2. Update ảnh mới
            StorageReference referenceNewImg = referenceStorageFood.child("ImgFood/" + Calendar.getInstance().getTimeInMillis());
            referenceNewImg.putFile(uriImageGallery).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    referenceNewImg.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            food.setImgFood(uri.toString());
                            food.setNameFood(nameFood);
                            food.setPriceFood(priceFood);
                            food.setDescriptionFood(desFood);
                            // update DB
                            Food foodNew = new Food(food.getNameFood(),food.getImgFood(),food.getPriceFood(),food.getCategoryID(),food.getDescriptionFood());
                            referenceDBFood.child(food.getKeyFood()).setValue(foodNew);

                            uriImageGallery = null;

                            dialogUpdate.dismiss();
                            Toast.makeText(getApplicationContext(), "Cập nhật thành công", Toast.LENGTH_SHORT).show();


                        }
                    });

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    dialogUpdate.dismiss();
                    Toast.makeText(getApplicationContext(), "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    // ============  Dialog Add Food ============
    private void showDialogAddFood(Category category) {
        Dialog dialogAddFood = new Dialog(this);
        dialogAddFood.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogAddFood.setCanceledOnTouchOutside(false);
        dialogAddFood.setContentView(R.layout.dialog_add_food);

        Window windowAddFood = dialogAddFood.getWindow();
        if (windowAddFood == null)
        {
            return;
        }
        windowAddFood.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        windowAddFood.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // Init view in dialog
        imgGallery                                  = dialogAddFood.findViewById(R.id.img_AddFood);
        TextInputEditText edt_NameAddFood           = dialogAddFood.findViewById(R.id.edt_NameAddFood);
        TextInputEditText edt_PriceAddFood          = dialogAddFood.findViewById(R.id.edt_PriceAddFood);
        TextInputEditText edt_DescriptionAddFood    = dialogAddFood.findViewById(R.id.edt_DescriptionAddFood);
        Button btn_CancelAddFood                    = dialogAddFood.findViewById(R.id.btn_CancelAddFood);
        Button btn_ConfirmAddFood                   = dialogAddFood.findViewById(R.id.btn_ConfirmAddFood);
        TextInputLayout layoutNameFood              = dialogAddFood.findViewById(R.id.edtLayout_NameFood);
        TextInputLayout layoutPriceFood             = dialogAddFood.findViewById(R.id.edtLayout_PriceFood);

        // Check value
        checkValueAddFood(layoutNameFood,edt_NameAddFood);
        checkValueAddFood(layoutPriceFood,edt_PriceAddFood);

        // Choose Image For Food
        imgGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPermissionGallery();
            }
        });

        // Button Confirm Add Food
        btn_ConfirmAddFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String nameFood = Objects.requireNonNull(edt_NameAddFood.getText()).toString();
                String priceFood = Objects.requireNonNull(edt_PriceAddFood.getText()).toString();
                String desFood = Objects.requireNonNull(edt_DescriptionAddFood.getText()).toString();

                if (nameFood.isEmpty() || priceFood.isEmpty() || uriImageGallery == null)
                {
                    Toast.makeText(getApplicationContext(), "Vui lòng không bỏ trống dữ liệu", Toast.LENGTH_SHORT).show();
                }
                else {
                    dialogAddFood.dismiss();
                    addNewFood(category,nameFood,Long.parseLong(priceFood),desFood);
                }

            }
        });

        // Button Cancel Add Food
        btn_CancelAddFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogAddFood.dismiss();
            }
        });
        // Show dialog add fodd
        dialogAddFood.show();
    }

    // ============  Add Food ============
    private void addNewFood(Category category, String nameFood, long priceFood, String desFood) {

        // Progress Dialog
        ProgressDialog progressAddFood = new ProgressDialog(this);
        progressAddFood.setMessage("Đang thêm dữ liệu");
        progressAddFood.show();

        // Key food
        String keyFood = "F" + Calendar.getInstance().getTimeInMillis();

        // Push image to storage
        StorageReference referenceFood = referenceStorageFood.child("imgFood/" + Calendar.getInstance().getTimeInMillis());

        referenceFood.putFile(uriImageGallery).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                // Get link image
                referenceFood.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String imgFood = uri.toString();
                        // New food
                        Food foodAdd = new Food(nameFood,imgFood,priceFood,category.getKeyCategory(),desFood);
                        referenceDBFood.child(keyFood).setValue(foodAdd);
                        progressAddFood.dismiss();
                        Toast.makeText(getApplicationContext(), "Thêm thành công", Toast.LENGTH_SHORT).show();
                        uriImageGallery = null;
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressAddFood.dismiss();
                        Toast.makeText(getApplicationContext(), "Thêm thất bại", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

    }

    // ============ Check Value Add Food ============
    private void checkValueAddFood(TextInputLayout layout, TextInputEditText editText){

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0)
                {
                    layout.setError("Không được bỏ trống");
                    editText.setFocusable(true);
                }
                else {
                    layout.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    // ========== REQUEST PERMISSION GALLERY ============
    private void requestPermissionGallery() {
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.READ_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED)
        {
            openGallery();
        } else if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_EXTERNAL_STORAGE)) {
            new AlertDialog.Builder(this)
                    .setTitle("Quyền truy cập thư viên ảnh")
                    .setMessage("Hãy chấp quyền truy cập thư viên để có thể chọn ảnh")
                    .setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            galleryRequest.launch(
                                    Manifest.permission.READ_EXTERNAL_STORAGE);
                        }
                    })
                    .setNegativeButton("Không đồng ý", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .show();
        } else {
            galleryRequest.launch(
                    Manifest.permission.READ_EXTERNAL_STORAGE);
        }
    }

    // ========== OPEN GALLERY ============
    private void openGallery(){
        Intent iGallery = new Intent(Intent.ACTION_GET_CONTENT);
        iGallery.setType("image/*");
        intentGallery.launch(iGallery);
    }

    // ========== LOAD FOOD ============
    private void loadFood(Category category){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("Food");

        listFood = new ArrayList<>();
        reference.orderByChild("categoryID").equalTo(category.getKeyCategory()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listFood.clear();
                for (DataSnapshot data : snapshot.getChildren())
                {
                    String keyFood = data.getKey();
                    Food food = data.getValue(Food.class);
                    if (food != null)
                    {
                        food.setKeyFood(keyFood);

                    }
                    listFood.add(food);
                    foodManagerAdapter.setListFoodManager(listFood);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // ========== INIT VIEW ============
    private void initView() {
        txtNameCategoryDetails = findViewById(R.id.txtNameCategoryDetailsManager);
        recyclerFoodManager = findViewById(R.id.recycle_CategoryDetailsManager);
        btn_AddFood = findViewById(R.id.btn_AddFoodManager);
    }
}