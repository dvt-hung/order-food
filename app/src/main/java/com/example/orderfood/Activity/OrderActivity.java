package com.example.orderfood.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.orderfood.Adapter.OrderUserAdapter;
import com.example.orderfood.Model.Order;
import com.example.orderfood.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class OrderActivity extends AppCompatActivity {

    private RecyclerView recycler_OrderUser;
    private ImageView img_Back;
    private OrderUserAdapter orderUserAdapter;
    private FirebaseUser currentUser;
    private DatabaseReference referenceOrderUser;
    private List<Order> orderList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        // init view
        initView();

        // set adapter
        orderUserAdapter        = new OrderUserAdapter(new OrderUserAdapter.OrderUserListener() {
            @Override
            public void onClickOrderUser(Order order) {
                Intent intent = new Intent(OrderActivity.this,DetailsOrderActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("order",order);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        recycler_OrderUser.setAdapter(orderUserAdapter);

        // set layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(this,RecyclerView.VERTICAL,false);
        recycler_OrderUser.setLayoutManager(layoutManager);

        // load list order
        loadListOrder();

        // img back
        img_Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    // ========= LOAD LIST ORDER OF USER =========
    private void loadListOrder() {
        orderList = new ArrayList<>();

        referenceOrderUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                orderList.clear();
                for (DataSnapshot data : snapshot.getChildren())
                {
                    Order order = data.getValue(Order.class);
                    if (order != null)
                    {
                        order.setIdOrder(data.getKey());
                        if (order.getUser().getId().equals(currentUser.getUid()))
                        {
                            orderList.add(order);
                            orderUserAdapter.setDataOrderUser(orderList);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // ========= INIT VIEW =========
    private void initView() {
        recycler_OrderUser      = findViewById(R.id.recycle_OrderUser);
        img_Back                = findViewById(R.id.img_Back);
        currentUser             = FirebaseAuth.getInstance().getCurrentUser();
        referenceOrderUser      = FirebaseDatabase.getInstance().getReference("Order");
    }
}