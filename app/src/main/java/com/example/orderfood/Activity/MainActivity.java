package com.example.orderfood.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.orderfood.Fragment.Fragment_Cart;
import com.example.orderfood.Fragment.Fragment_Home;
import com.example.orderfood.Fragment.Fragment_Chat;
import com.example.orderfood.Fragment.Fragment_Profile;
import com.example.orderfood.Model.User;
import com.example.orderfood.MyService.UserService;
import com.example.orderfood.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private final int FRAGMENT_HOME = 1;
    private final int FRAGMENT_CART = 2;
    private final int FRAGMENT_PROFILE = 3;
    private final int FRAGMENT_CHAT = 4;
    private int CurrentFragment = FRAGMENT_HOME;
    public static User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //init View
        initView();

        // Test
        String getStrFromNotification = getIntent().getStringExtra("messageUser");
        if (getStrFromNotification != null)
        {
            replaceFragment(new Fragment_Chat());
            CurrentFragment = FRAGMENT_CHAT;
            bottomNavigationView.setSelectedItemId(R.id.nav_chat);

        }
        else
        {
            //init Bottom Navigation
            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame,new Fragment_Home()).commit();
            bottomNavigationView.setSelectedItemId(R.id.nav_home);
        }

        // get current user
        getCurrentUser();


        // onClick Item Bottom Navigation View
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId())
                {
                    case R.id.nav_home:
                        if (CurrentFragment != FRAGMENT_HOME)
                        {
                            replaceFragment(new Fragment_Home());
                            CurrentFragment = FRAGMENT_HOME;
                        }
                        break;

                    case R.id.nav_cart:
                        if (CurrentFragment != FRAGMENT_CART)
                        {
                            replaceFragment(new Fragment_Cart());
                            CurrentFragment = FRAGMENT_CART;
                        }
                        break;

                    case R.id.nav_profile:
                        if (CurrentFragment != FRAGMENT_PROFILE)
                        {
                            replaceFragment(new Fragment_Profile());
                            CurrentFragment = FRAGMENT_PROFILE;
                        }

                        break;

                    case R.id.nav_chat:
                        if (CurrentFragment != FRAGMENT_CHAT)
                        {
                            replaceFragment(new Fragment_Chat());
                            CurrentFragment = FRAGMENT_CHAT;
                        }
                        break;
                }
                return true;
            }
        });


        }

    @Override
    protected void onResume() {
        super.onResume();
//        // call service
        Intent intent = new Intent(this, UserService.class);
        startService(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(new Intent(this, UserService.class));
    }

    private void getCurrentUser() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference referenceUser = FirebaseDatabase.getInstance().getReference("User");

        assert currentUser != null;
        referenceUser.child(currentUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                user = snapshot.getValue(User.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }



    private  void replaceFragment(Fragment fragment)
    {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content_frame,fragment);
        transaction.commit();
    }

    private void initView()
    {
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_nav_view);
    }
}