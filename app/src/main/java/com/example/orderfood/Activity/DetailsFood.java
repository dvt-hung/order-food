package com.example.orderfood.Activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.orderfood.DataLocal.DataLocalPreferences;
import com.example.orderfood.Fragment.Fragment_Cart;
import com.example.orderfood.Model.Food;
import com.example.orderfood.Model.ItemCart;
import com.example.orderfood.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.text.NumberFormat;
import java.util.Locale;

public class DetailsFood extends BottomSheetDialogFragment {

    private Food mFood;
    private ImageView imgDetailsFood;
    private TextView txtNameDetailsFood,txtPriceDetailsFood,txtDescriptionDetailsFood;
    private Button btnAddToCart;

    public static DetailsFood getInstance(Food food)
    {
        DetailsFood detailsFood = new DetailsFood();
        Bundle bundle = new Bundle();
        bundle.putSerializable("details_food",food);
        detailsFood.setArguments(bundle);
        return detailsFood;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle data = getArguments();
        if (data != null)
        {
            mFood = (Food) data.getSerializable("details_food");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);

        View view = LayoutInflater.from(getContext()).inflate(R.layout.details_food_bottomsheet,null);
        dialog.setContentView(view);
        initView(view);
        setDataView();


            btnAddToCart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ItemCart item = new ItemCart(mFood, 1);
                    if(Fragment_Cart.itemCartList.isEmpty())
                    {
                        Fragment_Cart.itemCartList.add(item);
                        Fragment_Cart.total = (item.getFood().getPriceFood());
                        Toast.makeText(getActivity(), "Đã thêm giỏ hàng thành công"  , Toast.LENGTH_SHORT).show();
                    }
                    else {
                        boolean isSuss = false;
                        for (int i = 0; i < Fragment_Cart.itemCartList.size(); i++)
                        {
                            ItemCart x = Fragment_Cart.itemCartList.get(i);
                            if (x.equals(item))
                            {
                                isSuss = true;
                                break;
                            }
                        }
                        if (!isSuss)
                        {
                            Fragment_Cart.itemCartList.add(item);
                            Fragment_Cart.total += (item.getFood().getPriceFood());
                            Toast.makeText(getActivity(), "Đã thêm giỏ hàng thành công"  , Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(getActivity(), "Đã có trong giỏ hàng"  , Toast.LENGTH_SHORT).show();
                        }
                    }
                    dialog.dismiss();
                    DataLocalPreferences.saveListItemCart(Fragment_Cart.itemCartList);
                }
            });

        return dialog;
    }

    private void initView(View view) {
        imgDetailsFood = view.findViewById(R.id.imgDetailsFood);
        txtNameDetailsFood = view.findViewById(R.id.txtNameDetailsFood);
        txtPriceDetailsFood = view.findViewById(R.id.txtPriceDetailsFood);
        txtDescriptionDetailsFood = view.findViewById(R.id.txtDescriptionDetailsFood);
        btnAddToCart = view.findViewById(R.id.btnAddToCart);
    }

    @SuppressLint("SetTextI18n")
    private void setDataView()
    {
        Locale locale = new Locale("vn");
        NumberFormat numberFormat = NumberFormat.getInstance(locale);
        if (mFood != null)
        {
            Glide.with(requireContext()).load(mFood.getImgFood()).into(imgDetailsFood);
            txtNameDetailsFood.setText(mFood.getNameFood());
            txtPriceDetailsFood.setText("Giá: " + numberFormat.format(mFood.getPriceFood()) + " đ");
            txtDescriptionDetailsFood.setText("Topping: " + mFood.getDescriptionFood());
        }
    }
}
