package com.example.orderfood.Adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.orderfood.Model.Order;
import com.example.orderfood.R;

import java.text.SimpleDateFormat;
import java.util.List;

public class OrderManagerAdapter extends RecyclerView.Adapter<OrderManagerAdapter.OrderViewHolder> {

    private List<Order> orderList;
    private final IOrderManagerListener orderManagerListener;

    public interface IOrderManagerListener{
        void itemOrderManagerClick(Order order);
    }

    public OrderManagerAdapter(IOrderManagerListener orderManagerListener) {
        this.orderManagerListener = orderManagerListener;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setListOrder(List<Order> list){
        this.orderList = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_admin,parent,false);
        return new OrderViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orderList.get(position);
        if (order != null)
        {
            holder.txt_Id_Order.setText(order.getIdOrder());
            holder.txt_Name_Order.setText(order.getUser().getName());
            holder.txt_Phone_Order.setText(order.getPhoneOrder());
            holder.txt_Address_Order.setText(order.getAddressOder());
            if (order.getStatusOrder() == 0)
            {
                holder.txt_Status_Order.setText("Đang chờ duyệt");
            }
            else
            {
                holder.txt_Status_Order.setText("Đang giao hàng");
            }

            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm");
            holder.txt_Date_Order.setText(dateFormat.format(order.getDateOrder()));

            holder.item_Order.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    orderManagerListener.itemOrderManagerClick(order);
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

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        CardView item_Order;
        TextView txt_Id_Order, txt_Date_Order, txt_Status_Order, txt_Name_Order, txt_Phone_Order, txt_Address_Order;
        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);

            txt_Id_Order = itemView.findViewById(R.id.txt_Id_Order);
            txt_Date_Order = itemView.findViewById(R.id.txt_Date_Order);
            txt_Status_Order = itemView.findViewById(R.id.txt_Status_Order);
            txt_Name_Order = itemView.findViewById(R.id.txt_Name_Order);
            txt_Phone_Order = itemView.findViewById(R.id.txt_Phone_Order);
            txt_Address_Order = itemView.findViewById(R.id.txt_Address_Order);
            item_Order = itemView.findViewById(R.id.item_Order);
        }
    }
}
