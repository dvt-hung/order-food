package com.example.orderfood.Adapter;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.orderfood.Model.Order;
import com.example.orderfood.R;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class OrderUserAdapter extends RecyclerView.Adapter<OrderUserAdapter.OrderUserViewHolder>{

    private List<Order> orderList;
    private final OrderUserListener listener;
    public static Order mOrder;

    public interface OrderUserListener
    {
        void onClickOrderUser(Order order);
    }

    public OrderUserAdapter(OrderUserListener listener) {
        this.listener = listener;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setDataOrderUser(List<Order> order)
    {
        this.orderList = order;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public OrderUserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_user,parent,false);
        return new OrderUserViewHolder(view);
    }

    @SuppressLint({"SetTextI18n", "ResourceAsColor"})
    @Override
    public void onBindViewHolder(@NonNull OrderUserViewHolder holder, int position) {
        Order order = orderList.get(position);

        if (order != null)
        {
            // Id order
            holder.txt_Id_Order.setText(order.getIdOrder());

            // Date order
            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm");
            holder.txt_Date_Order.setText(dateFormat.format(order.getDateOrder()));

            //Address order
            holder.txt_Address_Order.setText(order.getAddressOder());

            // Total order
            Locale locale = new Locale("vn");
            NumberFormat numberFormat = NumberFormat.getInstance(locale);
            holder.txt_Total_Order.setText(numberFormat.format(order.getTotalOrder()));

            // Set color
            if (order.getStatusOrder() == 2)
            {
                holder.txt_Id_Order.setTextColor(R.color.black);
                holder.txt_Date_Order.setTextColor(R.color.black);
            }

            holder.item_Order_User.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onClickOrderUser(order);
                }
            });


        }
    }

    @Override
    public int getItemCount() {
        if (orderList != null)
        {
            return orderList.size();
        }
        return 0;
    }

    public class OrderUserViewHolder extends RecyclerView.ViewHolder {
        private CardView item_Order_User;
        private TextView txt_Id_Order, txt_Date_Order,txt_Address_Order,txt_Total_Order;
        public OrderUserViewHolder(@NonNull View itemView) {
            super(itemView);
            item_Order_User     = itemView.findViewById(R.id.item_order_user);
            txt_Id_Order        = itemView.findViewById(R.id.txt_Id_Order_User);
            txt_Date_Order      = itemView.findViewById(R.id.txt_Date_Order_User);
            txt_Address_Order   = itemView.findViewById(R.id.txt_Address_Order_User);
            txt_Total_Order     = itemView.findViewById(R.id.txt_Total_Order_User);
        }
    }
}
