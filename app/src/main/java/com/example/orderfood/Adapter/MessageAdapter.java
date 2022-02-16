package com.example.orderfood.Adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.orderfood.Model.Message;
import com.example.orderfood.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter{

    private final int MESSAGE_RIGHT = 1;
    private final int MESSAGE_LEFT = 2;

    private List<Message> messageList;


    @SuppressLint("NotifyDataSetChanged")
    public void setDataMessage(List<Message> message)
    {
        this.messageList = message;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == MESSAGE_RIGHT)
        {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_right,parent,false);
            return new MessageRightViewHolder(view);
        }
        else
        {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_left,parent,false);
            return new MessageLeftViewHolder(view);
        }
    }

    @SuppressLint("SimpleDateFormat")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = messageList.get(position);

        if (message != null)
        {
            if (holder.getClass() == MessageRightViewHolder.class)
            {
                MessageRightViewHolder rightViewHolder = (MessageRightViewHolder) holder;
                rightViewHolder.txt_Message_Right.setText(message.getMessage());
                rightViewHolder.txt_Time_Right.setText(new SimpleDateFormat("hh:mm").format(message.getTime()));
            }
            else
            {
                MessageLeftViewHolder leftViewHolder = (MessageLeftViewHolder) holder;
                leftViewHolder.txt_Message_Left.setText(message.getMessage());
                leftViewHolder.txt_Time_Left.setText(new SimpleDateFormat("hh:mm").format(message.getTime()));
            }
        }

    }

    @Override
    public int getItemCount() {
        if (messageList != null)
        {
            return messageList.size();
        }
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messageList.get(position);
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        assert currentUser != null;
        if (message.getSenderID().equals(currentUser.getUid()))
        {
            return MESSAGE_RIGHT;
        }
        else
        {
            return MESSAGE_LEFT;
        }
    }

    public static class MessageRightViewHolder extends RecyclerView.ViewHolder {
        TextView txt_Message_Right, txt_Time_Right;
        public MessageRightViewHolder(@NonNull View itemView) {
            super(itemView);

            txt_Message_Right   = itemView.findViewById(R.id.txt_Message_Right);
            txt_Time_Right      = itemView.findViewById(R.id.txt_TimeMess_Right);
        }
    }

    public static class MessageLeftViewHolder extends RecyclerView.ViewHolder {
        TextView txt_Message_Left, txt_Time_Left;
        public MessageLeftViewHolder(@NonNull View itemView) {
            super(itemView);

            txt_Message_Left   = itemView.findViewById(R.id.txt_Message_Left);
            txt_Time_Left      = itemView.findViewById(R.id.txt_TimeMess_Left);
        }
    }
}
