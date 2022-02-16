package com.example.orderfood.Activity;

import static com.example.orderfood.Adapter.OrderUserAdapter.mOrder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.orderfood.Adapter.FoodDetailOrderAdapter;
import com.example.orderfood.Model.Order;
import com.example.orderfood.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.HashMap;


public class DetailsOrderActivity extends AppCompatActivity {

    private TextView txt_Id_Order_Details,txt_Date_Order_Details,title_shipping, txt_Cancel_Order;
    private ImageView img_Back_Order_Details;
    private RecyclerView recycle_Order_Details;
    private View oval_shipping,column_shipping;
    private Button btn_Confirm_Pay;
    private Order order;
    @SuppressLint("SimpleDateFormat")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_order);
        
        //init view
        initView();
        order = (Order) getIntent().getExtras().get("order");

        txt_Id_Order_Details.setText(order.getIdOrder());
        txt_Date_Order_Details.setText(new SimpleDateFormat("dd/MM/yyyy hh:mm").format(order.getDateOrder()));

        FoodDetailOrderAdapter orderAdapter = new FoodDetailOrderAdapter(order.getItemCartList());
        recycle_Order_Details.setAdapter(orderAdapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        recycle_Order_Details.setLayoutManager(layoutManager);

        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this,DividerItemDecoration.VERTICAL);
        recycle_Order_Details.addItemDecoration(itemDecoration);

        // change color
        if (order.getStatusOrder() == 1)
        {
            oval_shipping.setBackgroundResource(R.drawable.view_status_current);
            column_shipping.setBackgroundColor(getColor(R.color.yellow_bottom_nav));
            title_shipping.setTextColor(getColor(R.color.black));
            txt_Cancel_Order.setVisibility(View.GONE);
            btn_Confirm_Pay.setVisibility(View.VISIBLE);
        }
        else if (order.getStatusOrder() == 2)
        {
            oval_shipping.setBackgroundResource(R.drawable.view_status_current);
            column_shipping.setBackgroundColor(getColor(R.color.yellow_bottom_nav));
            title_shipping.setTextColor(getColor(R.color.black));
            btn_Confirm_Pay.setVisibility(View.GONE);
            txt_Cancel_Order.setVisibility(View.GONE);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        img_Back_Order_Details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Cancel order
        txt_Cancel_Order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogCancelOrder(order);
            }
        });

        // Confirm
        btn_Confirm_Pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmOrder(order);
            }
        });
    }

    private void confirmOrder(Order order) {
        DatabaseReference referenceOrder = FirebaseDatabase.getInstance().getReference("Order");
        HashMap<String, Object> confirm = new HashMap<>();
        confirm.put("seen", false);
        confirm.put("statusOrder",2);
        referenceOrder.child(order.getIdOrder()).updateChildren(confirm);
        finish();
    }

    private void showDialogCancelOrder(Order order) {

        Dialog dialogCancelOrder = new Dialog(DetailsOrderActivity.this);
        dialogCancelOrder.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogCancelOrder.setContentView(R.layout.dialog_cancel_order);

        Window windowCancelOrder = dialogCancelOrder.getWindow();
        windowCancelOrder.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        windowCancelOrder.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Button btn_Confirm      = dialogCancelOrder.findViewById(R.id.btn_Confirm_Cancel_Order);
        TextView txt_Dismiss    = dialogCancelOrder.findViewById(R.id.txt_Dismiss);

        // Dismiss
        txt_Dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogCancelOrder.dismiss();
            }
        });

        // Confirm
        btn_Confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference referenceOrder = FirebaseDatabase.getInstance().getReference("Order");
                referenceOrder.child(order.getIdOrder()).removeValue();
                dialogCancelOrder.dismiss();
                finish();
            }
        });

        dialogCancelOrder.show();

    }


    private void initView() {
        txt_Id_Order_Details    = findViewById(R.id.txt_Id_Order_Details);
        txt_Date_Order_Details  = findViewById(R.id.txt_Date_Order_Details);
        txt_Cancel_Order        = findViewById(R.id.txt_Cancel_Order);
        img_Back_Order_Details  = findViewById(R.id.img_Back_Order_Details);
        recycle_Order_Details   = findViewById(R.id.recycle_Order_Details);
        oval_shipping           = findViewById(R.id.oval_shipping);
        column_shipping         = findViewById(R.id.column_shipping);
        title_shipping          = findViewById(R.id.title_shipping);
        btn_Confirm_Pay         = findViewById(R.id.btn_Confirm_Pay);

    }
}