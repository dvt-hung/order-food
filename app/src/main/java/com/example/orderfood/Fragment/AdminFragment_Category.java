package com.example.orderfood.Fragment;


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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.orderfood.Adapter.CategoryManagerAdapter;
import com.example.orderfood.Activity.FoodManagerActivity;
import com.example.orderfood.Model.Category;
import com.example.orderfood.Model.Food;
import com.example.orderfood.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
import java.util.Date;
import java.util.List;

public class AdminFragment_Category extends Fragment {

    private RecyclerView recycler_CategoryAdmin;
    private final List<Category> listItemCategory = new ArrayList<>();
    private CategoryManagerAdapter categoryManagerAdapter;
    private FloatingActionButton btnAddCategoryManager;
    private ImageView imgGallery;
    public Uri uriImageGallery;
    private DatabaseReference referenceDB;
    private FirebaseStorage storage;
    private StorageReference storageReferenceCategory;

    private final ActivityResultLauncher<Intent> intentGallery = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        imgGallery.setImageURI(result.getData().getData());
                        uriImageGallery = result.getData().getData();
                    }
                }
            });

    private final ActivityResultLauncher<String> galleryRequest = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
        @Override
        public void onActivityResult(Boolean result) {
            if (!result) {
                Toast.makeText(getActivity(), "Không cho phép", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), "Đã cho phép", Toast.LENGTH_SHORT).show();
                openGallery();
            }
        }
    });


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.admin_fragment_category, container, false);

        // initView
        initView(view);
        // init Firebase database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        referenceDB = database.getReference("Category");
        // init Storage
        storage = FirebaseStorage.getInstance("gs://order-food-1f7a5.appspot.com");
        storageReferenceCategory = storage.getReference();

        // layout manager for recycler
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        recycler_CategoryAdmin.setLayoutManager(layoutManager);

        // Adapter
        categoryManagerAdapter = new CategoryManagerAdapter(getActivity(), new CategoryManagerAdapter.IEditCategory() {

            @Override
            public void showBottomMenu(Category category) {
                showBottomSheet(category);
            }
        });

        // set adapter for recyclerView
        recycler_CategoryAdmin.setAdapter(categoryManagerAdapter);

        // load Category
        loadCategory();

        // Button Add Category
        btnAddCategoryManager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogAddCategory();
            }
        });

        return view;
    }

    // ============ Bottom Sheet ============
    private void showBottomSheet(Category category) {
        BottomSheetDialog bottomMenu = new BottomSheetDialog(requireActivity());
        bottomMenu.setContentView(R.layout.menu_category_bottom_sheet);

        LinearLayout bottom_menu_Details = bottomMenu.findViewById(R.id.bottom_menu_Details);
        LinearLayout bottom_menu_UpdateCategory = bottomMenu.findViewById(R.id.bottom_menu_UpdateCategory);
        LinearLayout bottom_menu_DeleteCategory = bottomMenu.findViewById(R.id.bottom_menu_DeleteCategory);


        if (bottom_menu_Details != null) {
            bottom_menu_Details.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), FoodManagerActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("obj_category", category);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    bottomMenu.dismiss();

                }
            });
        }

        if (bottom_menu_UpdateCategory != null) {
            bottom_menu_UpdateCategory.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDialogUpdateCategory(category);
                    bottomMenu.dismiss();
                }
            });
        }

        if (bottom_menu_DeleteCategory != null) {
            bottom_menu_DeleteCategory.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    delete_Category(category);
                    bottomMenu.dismiss();

                }
            });
        }
        bottomMenu.show();
    }

    // ======= Delete Category =======
    private void delete_Category(Category category) {
        // Alert Dialog
        new AlertDialog.Builder(getActivity())
                .setTitle("Xóa danh mục")
                .setMessage("Bạn có chắc muốn xóa danh mục " + category.getNameCategory() + " không ?")
                .setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        StorageReference deleteStorage = storage.getReferenceFromUrl(category.getImgCategory());

                        ProgressDialog dialog_Delete = new ProgressDialog(getActivity());
                        dialog_Delete.setMessage("Đang xóa dữ liệu");
                        dialog_Delete.show();

                        deleteStorage.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                referenceDB.child(category.getKeyCategory()).removeValue();

                                // delete food in category
                                deleteFoodByCategory(category.getKeyCategory());
                                Toast.makeText(getActivity(), "Đã xóa thành công ", Toast.LENGTH_SHORT).show();
                                dialog_Delete.dismiss();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getActivity(), "Xóa thất bại ", Toast.LENGTH_SHORT).show();
                                dialog_Delete.dismiss();
                            }
                        });
                    }
                })
                .setNegativeButton("Không", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    private void deleteFoodByCategory(String keyCategory) {
        DatabaseReference referenceFood = FirebaseDatabase.getInstance().getReference("Food");

        referenceFood.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot data : snapshot.getChildren()) {
                    Food food = data.getValue(Food.class);
                    assert food != null;
                    food.setKeyFood(data.getKey());
                    if (food.getCategoryID().equals(keyCategory)) {
                        // delete in database
                        referenceFood.child(food.getKeyFood()).removeValue();

                        // delete image
                        StorageReference deleteStorage = storage.getReferenceFromUrl(food.getImgFood());
                        deleteStorage.delete();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    // ======= Dialog Update Category =======
    private void showDialogUpdateCategory(Category category) {

        Dialog dialogUpdateCategory = new Dialog(getActivity());
        dialogUpdateCategory.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogUpdateCategory.setCanceledOnTouchOutside(true);
        dialogUpdateCategory.setContentView(R.layout.dialog_update_category);

        Window windowUpdate = dialogUpdateCategory.getWindow();
        if (windowUpdate == null) {
            return;
        }
        windowUpdate.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        windowUpdate.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // View in dialog
        imgGallery = dialogUpdateCategory.findViewById(R.id.img_UpdateCategory);
        EditText edt_NameUpdateCategory = dialogUpdateCategory.findViewById(R.id.edt_NameUpdateCategory);
        Button btn_CancelUpdateCategory = dialogUpdateCategory.findViewById(R.id.btn_CancelUpdateCategory);
        Button btn_ConfirmUpdateCategory = dialogUpdateCategory.findViewById(R.id.btn_ConfirmUpdateCategory);

        // Set dữ liệu ban đầu
        Glide.with(requireActivity()).load(category.getImgCategory()).into(imgGallery);
        edt_NameUpdateCategory.setText(category.getNameCategory());

        // Choose Image Update Category
        imgGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPermissionGallery();
            }
        });

        // Button Cancel
        btn_CancelUpdateCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogUpdateCategory.dismiss();
            }
        });

        // Button Confirm
        btn_ConfirmUpdateCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Progress dialog
                ProgressDialog dialogUpdate = new ProgressDialog(getActivity());
                dialogUpdate.setMessage("Đang chỉnh sửa dữ liệu");
                dialogUpdate.show();
                // check data
                if (uriImageGallery == null) {
                    referenceDB.child(category.getKeyCategory()).child("nameCategory").setValue(edt_NameUpdateCategory.getText().toString().trim())
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(getActivity(), "Đã chỉnh sửa thành công", Toast.LENGTH_SHORT).show();
                                    dialogUpdate.dismiss();
                                }
                            });
                } else {
                    // 1. Xóa Image hiện tại
                    StorageReference deleteImageReference = storage.getReferenceFromUrl(category.getImgCategory());
                    deleteImageReference.delete();

                    // 2.Thêm hình mới + đặt tên mới
                    StorageReference imgNewReference = storageReferenceCategory.child("ImgCategory/" + Calendar.getInstance().getTimeInMillis());
                    imgNewReference.putFile(uriImageGallery).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            imgNewReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    category.setNameCategory(edt_NameUpdateCategory.getText().toString().trim());
                                    category.setImgCategory(uri.toString());
                                    // new category
                                    Category nCategory = new Category(category.getImgCategory(), category.getNameCategory());
                                    referenceDB.child(category.getKeyCategory())
                                            .setValue(nCategory);
                                    uriImageGallery = null;
                                    // Dismiss dialog
                                    dialogUpdate.dismiss();
                                    dialogUpdateCategory.dismiss();
                                }
                            });


                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            dialogUpdate.dismiss();
                            Toast.makeText(getActivity(), "Failed", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
        dialogUpdateCategory.show();
    }

    // ============ Dialog Add Category ============
    private void showDialogAddCategory() {
        Dialog dialogAddCategory = new Dialog(getActivity());
        dialogAddCategory.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogAddCategory.setContentView(R.layout.dialog_add_category);
        dialogAddCategory.setCanceledOnTouchOutside(false);
        Window window = dialogAddCategory.getWindow();
        if (window == null) {
            return;
        }
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        imgGallery = dialogAddCategory.findViewById(R.id.img_chooseCategory);
        EditText edt_NameCategory = dialogAddCategory.findViewById(R.id.edt_NameCategory);
        Button btn_CancelAddCategory = dialogAddCategory.findViewById(R.id.btn_CancelAddCategory);
        Button btn_ConfirmAddCategory = dialogAddCategory.findViewById(R.id.btn_ConfirmAddCategory);


        // Choose Image
        imgGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPermissionGallery();
            }
        });

        // Button Cancel
        btn_CancelAddCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogAddCategory.dismiss();
            }
        });
        // Button Confirm
        btn_ConfirmAddCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currentNameCategory = edt_NameCategory.getText().toString().trim();
                if (uriImageGallery == null || currentNameCategory.isEmpty()) {
                    Toast.makeText(getActivity(), "Bạn đã trống ảnh hoặc tên của danh mục.", Toast.LENGTH_SHORT).show();
                } else {
                    ProgressDialog progressDialog = new ProgressDialog(getActivity());
                    progressDialog.setMessage("Vui lòng chờ...");
                    progressDialog.show();
                    Calendar calendar = Calendar.getInstance();

                    StorageReference referenceStore = storageReferenceCategory.child("ImgCategory/" + calendar.getTimeInMillis());

                    referenceStore.putFile(uriImageGallery).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            referenceStore.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri DownloadUri) {
                                    Date date = new Date();
                                    String keyCategory = "C" + date.getTime();
                                    String imgCategory = DownloadUri.toString();
                                    referenceDB.child(keyCategory).setValue(new Category(imgCategory, currentNameCategory));
                                    uriImageGallery = null;
                                }
                            });
                            progressDialog.dismiss();
                            dialogAddCategory.dismiss();
                        }
                    });
                }
            }
        });
        dialogAddCategory.show();
    }

    // ========== REQUEST PERMISSION GALLERY ============
    private void requestPermissionGallery() {
        if (ContextCompat.checkSelfPermission(
                requireActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED) {
            openGallery();
        } else if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)) {
            new AlertDialog.Builder(getActivity())
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
    private void openGallery() {
        Intent iGallery = new Intent(Intent.ACTION_GET_CONTENT);
        iGallery.setType("image/*");
        intentGallery.launch(iGallery);
    }

    // ========== LOAD LIST CATEGORY ============
    private void loadCategory() {
        referenceDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listItemCategory.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    Category category = data.getValue(Category.class);
                    if (category != null) {
                        String nameCategory = category.getNameCategory();
                        String imgCategory = category.getImgCategory();
                        String keyCategory = data.getKey();
                        listItemCategory.add(new Category(imgCategory, nameCategory, keyCategory));
                        categoryManagerAdapter.setDataCategoryManager(listItemCategory);
                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // ========== INIT VIEW  ============
    private void initView(View view) {
        recycler_CategoryAdmin = view.findViewById(R.id.recycle_CategoryAdmin);
        btnAddCategoryManager = view.findViewById(R.id.btn_AddCategoryManager);
    }


}
