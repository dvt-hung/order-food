package com.example.orderfood.Adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.orderfood.Model.User;
import com.example.orderfood.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserChatAdapter extends RecyclerView.Adapter<UserChatAdapter.UserChatViewHolder>{

    private List<User> users;
    private IUserChatListener listener;
    public interface IUserChatListener
    {
        void onClickUserChat(User user);
    }

    public UserChatAdapter(IUserChatListener listener) {
        this.listener = listener;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setListUser(List<User> user)
    {
        this.users = user;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public UserChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_user_chat,parent,false);
        return new UserChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserChatViewHolder holder, int position) {
        User user = users.get(position);

        if (user != null)
        {
            holder.txt_NameUser_Chat.setText(user.getName());
            holder.item_User_Chat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onClickUserChat(user);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if (users != null)
        {
            return users.size();
        }
        return 0;
    }

    public class UserChatViewHolder extends RecyclerView.ViewHolder {
        TextView txt_NameUser_Chat;
        RelativeLayout item_User_Chat;
        public UserChatViewHolder(@NonNull View itemView) {
            super(itemView);

            txt_NameUser_Chat   = itemView.findViewById(R.id.txt_NameUser_Chat);
            item_User_Chat      = itemView.findViewById(R.id.item_User_Chat);
        }
    }
}
