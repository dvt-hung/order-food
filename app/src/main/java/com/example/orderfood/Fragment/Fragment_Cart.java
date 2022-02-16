package com.example.orderfood.Fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.orderfood.Adapter.CartAdapter;
import com.example.orderfood.DataLocal.DataLocalPreferences;
import com.example.orderfood.Activity.MainActivity;
import com.example.orderfood.Model.Food;
import com.example.orderfood.Model.ItemCart;
import com.example.orderfood.Model.Order;
import com.example.orderfood.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class Fragment_Cart extends Fragment {
    public static long total = DataLocalPreferences.getTotal();
    private RecyclerView recyclerItemCart;
    @SuppressLint("StaticFieldLeak")
    public  static TextView txtTotalPriceCart;
    private Button btnPayCart;
    private CartAdapter cartAdapter;
    public static List<ItemCart> itemCartList = new ArrayList<>();
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart,container,false);

        // Init Recycler View
        initView(view);

        // Load item cart
        itemCartList = DataLocalPreferences.getListItemCart();
        //Adapter  - Cart
        cartAdapter = new CartAdapter(view.getContext());
        recyclerItemCart.setAdapter(cartAdapter);
        // Layout manager Recycler - Cart
        LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext(),RecyclerView.VERTICAL,false);
        recyclerItemCart.setLayoutManager(layoutManager);
        // Decoration Recycler - Cart
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(view.getContext(),DividerItemDecoration.VERTICAL);
        recyclerItemCart.addItemDecoration(itemDecoration);
        cartAdapter.setDataItemCart(itemCartList);
        // Load list item cart - Cart
        loadTotalPrice();

        // Button Pay Cart
        btnPayCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogOrder();

            }
        });


        return view;
    }

    private void showDialogOrder() {

        Dialog dialogOrder = new Dialog(getActivity());
        dialogOrder.setContentView(R.layout.dialog_order);
        dialogOrder.setCanceledOnTouchOutside(false);

        Window windowOrder = dialogOrder.getWindow();
        if (windowOrder != null)
        {
            windowOrder.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT);
            windowOrder.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        // init view
        TextInputLayout layout_PhoneOrder   = dialogOrder.findViewById(R.id.edtLayout_PhoneOrder);
        TextInputLayout layout_AddressOrder = dialogOrder.findViewById(R.id.edtLayout_AddressOrder);
        TextInputLayout layout_NoteOrder    = dialogOrder.findViewById(R.id.edtLayout_NoteOrder);
        TextInputEditText edt_PhoneOrder    = dialogOrder.findViewById(R.id.edt_PhoneOrder);
        TextInputEditText edt_AddressOrder  = dialogOrder.findViewById(R.id.edt_AddressOrder);
        TextInputEditText edt_NoteOrder     = dialogOrder.findViewById(R.id.edt_NoteOrder);
        Button btnCancelOrder               = dialogOrder.findViewById(R.id.btn_CancelOrder);
        Button btnConfirmOrder              = dialogOrder.findViewById(R.id.btn_ConfirmOrder);

        checkValueAddFood(layout_PhoneOrder,edt_PhoneOrder);
        checkValueAddFood(layout_AddressOrder,edt_AddressOrder);

        // Button Cancel
        btnCancelOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogOrder.dismiss();
            }
        });

        // Button Confirm
        btnConfirmOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone    = Objects.requireNonNull(edt_PhoneOrder.getText()).toString().trim();
                String address  = Objects.requireNonNull(edt_AddressOrder.getText()).toString().trim();
                String note     = Objects.requireNonNull(edt_NoteOrder.getText()).toString().trim();
                if (phone.isEmpty() || address.isEmpty())
                {
                    Toast.makeText(getActivity(), "Vui lòng không bỏ trống thông tin", Toast.LENGTH_SHORT).show();
                }else {
                    addOderToDatabase(dialogOrder,phone,address,note);
                }
            }
        });

        dialogOrder.show();
    }

    // ============ Check Value Order ============
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

    // ==============  ADD ORDER TO DATABASE ==============
    private void addOderToDatabase(Dialog dialogOrder, String phone, String address, String note) {
        // Progress dialog
        ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Đang tiến hành đặt hàng");
        progressDialog.show();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference referenceDB_Order = database.getReference("Order");
        String idOrder =  "O" + Calendar.getInstance().getTimeInMillis();
        Date dateOder = new Date();

        Order order = new Order(MainActivity.user,itemCartList,0,total,address,phone,note,dateOder.getTime(),false);
        referenceDB_Order.child(idOrder).setValue(order).addOnCompleteListener(new OnCompleteListener<Void>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful())
                {
                    dialogOrder.dismiss();
                    progressDialog.dismiss();
                    Toast.makeText(getActivity(), "Đã đặt hàng thành công", Toast.LENGTH_SHORT).show();

                    // Reset value
                    itemCartList.clear();
                    total = 0;
                    Locale locale = new Locale("vn");
                    NumberFormat numberFormat = NumberFormat.getInstance(locale);
                    txtTotalPriceCart.setText(String.format("%s đ", numberFormat.format(total)));
                    cartAdapter.setDataItemCart(itemCartList);
                    DataLocalPreferences.saveListItemCart(itemCartList);
                    DataLocalPreferences.saveTotal(total);
                }
                else {
                    progressDialog.dismiss();
                    Toast.makeText(getActivity(), "Đã đặt hàng thất bại", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // ==============  INIT VIEW ==============
    private void initView(View view) {
        recyclerItemCart    = view.findViewById(R.id.recycle_cart);
        txtTotalPriceCart   = view.findViewById(R.id.txtTotalPriceCart);
        btnPayCart          = view.findViewById(R.id.btnPayCart);
    }

    // ==============  LOAD TOTAL PRICE CART ==============
    public static void  loadTotalPrice() {
        Locale locale = new Locale("vn");
        NumberFormat numberFormat = NumberFormat.getInstance(locale);

        if (itemCartList.isEmpty())
        {
            total = 0;
        }
        txtTotalPriceCart.setText(String.format("%s đ", numberFormat.format(total)));
        DataLocalPreferences.saveListItemCart(itemCartList);
        DataLocalPreferences.saveTotal(total);
    }


}
