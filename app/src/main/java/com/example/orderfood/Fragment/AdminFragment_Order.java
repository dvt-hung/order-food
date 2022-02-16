package com.example.orderfood.Fragment;


import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.orderfood.Adapter.FoodDetailOrderAdapter;
import com.example.orderfood.Adapter.OrderManagerAdapter;
import com.example.orderfood.Model.Order;
import com.example.orderfood.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AdminFragment_Order extends Fragment {

    private RecyclerView recycler_Order;
    private OrderManagerAdapter adapter_Order;
    private DatabaseReference referenceOrder;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       View view = inflater.inflate(R.layout.admin_fragment_order,container,false);

       // init View
        initView(view);

        // init Database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        referenceOrder = database.getReference("Order");

        // Layout manager recycler order
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(),RecyclerView.VERTICAL,false);
        recycler_Order.setLayoutManager(layoutManager);

        // Adapter recycler order
        adapter_Order = new OrderManagerAdapter(new OrderManagerAdapter.IOrderManagerListener() {
            @Override
            public void itemOrderManagerClick(Order order) {
                showDialogDetailOrder(order);
            }
        });
        // Load order list
        loadOrderList();
        recycler_Order.setAdapter(adapter_Order);


        return view;
    }

    // =========== SHOW DIALOG DETAILS ORDER ===========
    @SuppressLint("SetTextI18n")
    private void showDialogDetailOrder(Order order) {
        Dialog dialogDetailOrder = new Dialog(getActivity());
        dialogDetailOrder.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogDetailOrder.setContentView(R.layout.dialog_details_order);
        dialogDetailOrder.setCanceledOnTouchOutside(false);

        Window windowDetailsOrder = dialogDetailOrder.getWindow();
        windowDetailsOrder.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        windowDetailsOrder.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        // init view in dialog
        TextView txt_Name_Detail_Order      = dialogDetailOrder.findViewById(R.id.txt_Name_Detail_Order);
        TextView txt_Phone_Detail_Order     = dialogDetailOrder.findViewById(R.id.txt_Phone_Detail_Order);
        TextView txt_Address_Detail_Order   = dialogDetailOrder.findViewById(R.id.txt_Address_Detail_Order);
        TextView txt_Note_Detail_Order   = dialogDetailOrder.findViewById(R.id.txt_Note_Detail_Order);
        TextView txt_Total_Order            = dialogDetailOrder.findViewById(R.id.txt_Total_Order);
        Spinner spinner_status_Order        = dialogDetailOrder.findViewById(R.id.spinner_status_Order);
        RecyclerView recycle_Detail_Order   = dialogDetailOrder.findViewById(R.id.recycle_Detail_Order);
        Button btn_Cancel_Detail_Order      = dialogDetailOrder.findViewById(R.id.btn_Cancel_Detail_Order);
        Button btn_Confirm_Detail_Order     = dialogDetailOrder.findViewById(R.id.btn_Confirm_Detail_Order);

        // origin value order
        Locale locale = new Locale("vn");
        NumberFormat numberFormat = NumberFormat.getInstance(locale);

        txt_Name_Detail_Order.setText(order.getUser().getName());
        txt_Phone_Detail_Order.setText(order.getPhoneOrder());
        txt_Address_Detail_Order.setText(order.getAddressOder());
        txt_Total_Order.setText(numberFormat.format(order.getTotalOrder()) + " đ");
        txt_Note_Detail_Order.setText(order.getNoteOrder());
        // value spinner
        ArrayAdapter<String> adapterSpinner = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item,
                new String[]{"Đang chờ duyệt","Đang giao hàng"});
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_status_Order.setAdapter(adapterSpinner);

        spinner_status_Order.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                order.setStatusOrder(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // Recycler detail order
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false);
        recycle_Detail_Order.setLayoutManager(layoutManager);

        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(requireActivity(),DividerItemDecoration.VERTICAL);
        recycle_Detail_Order.addItemDecoration(itemDecoration);

        FoodDetailOrderAdapter adapterFoodDetailOrder = new FoodDetailOrderAdapter(order.getItemCartList());
        recycle_Detail_Order.setAdapter(adapterFoodDetailOrder);


        // Button Cancel
        btn_Cancel_Detail_Order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogDetailOrder.dismiss();
            }
        });


        // Button Confirm
        btn_Confirm_Detail_Order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                order.setSeen(false);
                referenceOrder.child(order.getIdOrder()).setValue(order);
                dialogDetailOrder.dismiss();
            }
        });
        dialogDetailOrder.show();
    }

    // =========== LOAD ORDER LIST ===========
    private void loadOrderList() {
        List<Order> orderList = new ArrayList<>();
        referenceOrder.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                orderList.clear();
                for (DataSnapshot data : snapshot.getChildren())
                {
                    Order order = data.getValue(Order.class);

                    assert order != null;
                    order.setIdOrder(data.getKey());
                        if (order.getStatusOrder() == 0)
                        {
                            orderList.add(order);
                        }
                    adapter_Order.setListOrder(orderList);

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    // =========== INIT VIEW ===========
    private void initView(View view) {
        recycler_Order = view.findViewById(R.id.recycle_OrderAdmin);
    }
}
