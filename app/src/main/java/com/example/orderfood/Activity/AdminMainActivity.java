package com.example.orderfood.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.orderfood.Fragment.AdminFragment_Account;
import com.example.orderfood.Fragment.AdminFragment_Category;
import com.example.orderfood.Fragment.AdminFragment_Chat;
import com.example.orderfood.Fragment.AdminFragment_Order;
import com.example.orderfood.MyService.AdminService;
import com.example.orderfood.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class AdminMainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavAdmin;
    private final int ADMIN_FRAGMENT_CATEGORY = 1;
    private final int ADMIN_FRAGMENT_CHAT = 2;
    private final int ADMIN_FRAGMENT_ORDER = 3;
    private final int ADMIN_FRAGMENT_ACCOUNT = 4;
    private  int CURRENT_FRAGMENT = ADMIN_FRAGMENT_CATEGORY;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);
        // initView
        initView();

        String getStrNewOrder = getIntent().getStringExtra("newOrder");
        if (getStrNewOrder != null)
        {
            replaceAdminFragment(new AdminFragment_Order());
            CURRENT_FRAGMENT = ADMIN_FRAGMENT_ORDER;
            bottomNavAdmin.setSelectedItemId(R.id.navAdmin_Orders);
        }
        else
        {
            // Bottom navigation admin
            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame_admin,new AdminFragment_Category()).commit();
            bottomNavAdmin.setSelectedItemId(R.id.navAdmin_Category);
        }


        bottomNavAdmin.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId())
                {
                    case R.id.navAdmin_Category:
                        if(CURRENT_FRAGMENT != ADMIN_FRAGMENT_CATEGORY)
                        {
                            replaceAdminFragment(new AdminFragment_Category());
                            CURRENT_FRAGMENT = ADMIN_FRAGMENT_CATEGORY;
                        }
                        break;

                    case R.id.navAdmin_Chat:
                        if(CURRENT_FRAGMENT != ADMIN_FRAGMENT_CHAT)
                        {
                            replaceAdminFragment(new AdminFragment_Chat());
                            CURRENT_FRAGMENT = ADMIN_FRAGMENT_CHAT;

                        }

                        break;

                    case R.id.navAdmin_Orders:
                        if(CURRENT_FRAGMENT != ADMIN_FRAGMENT_ORDER)
                        {
                            replaceAdminFragment(new AdminFragment_Order());
                            CURRENT_FRAGMENT = ADMIN_FRAGMENT_ORDER;

                        }

                        break;

                    case R.id.navAdmin_Account:
                        if(CURRENT_FRAGMENT != ADMIN_FRAGMENT_ACCOUNT)
                        {
                            replaceAdminFragment(new AdminFragment_Account());
                            CURRENT_FRAGMENT = ADMIN_FRAGMENT_ACCOUNT;

                        }
                        break;

                }
                return true;
            }
        });


        Intent intentService = new Intent(this, AdminService.class);
        startService(intentService);

    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intentService = new Intent(this, AdminService.class);
        startService(intentService);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(new Intent(this, AdminService.class));
    }

    private void replaceAdminFragment(Fragment fragment)
    {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content_frame_admin,fragment);
        transaction.commit();
    }
    private void initView() {
        bottomNavAdmin = findViewById(R.id.bottom_nav_admin);
    }

}