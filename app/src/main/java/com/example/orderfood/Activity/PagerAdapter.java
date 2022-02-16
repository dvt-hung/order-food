package com.example.orderfood.Activity;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.orderfood.Fragment.Fragment_Cart;
import com.example.orderfood.Fragment.Fragment_Home;
import com.example.orderfood.Fragment.Fragment_Chat;
import com.example.orderfood.Fragment.Fragment_Profile;

public class PagerAdapter extends FragmentStateAdapter {

    public PagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position)
        {
            case 0:
                return new Fragment_Home();
            case 1:
                return new Fragment_Cart();
            case 2:
                return new Fragment_Profile();
            case 3:
                return new Fragment_Chat();

        }
        return null;
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}
