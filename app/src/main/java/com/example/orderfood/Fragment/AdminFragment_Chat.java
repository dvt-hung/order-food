package com.example.orderfood.Fragment;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.orderfood.Adapter.UserChatAdapter;
import com.example.orderfood.Activity.ChatActivity;
import com.example.orderfood.Model.User;
import com.example.orderfood.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AdminFragment_Chat extends Fragment {

    private RecyclerView recycler_ListUser;
    private UserChatAdapter userChatAdapter;
    private List<User> userList;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       View view = inflater.inflate(R.layout.admin_fragment_food,container,false);

        recycler_ListUser = view.findViewById(R.id.recycler_ListUser);

        // adapter recycler
        userChatAdapter = new UserChatAdapter(new UserChatAdapter.IUserChatListener() {
            @Override
            public void onClickUserChat(User user) {
                Intent intent = new Intent(requireActivity(), ChatActivity.class);
                intent.putExtra("idUser",user.getId());
                startActivity(intent);
            }
        });
        recycler_ListUser.setAdapter(userChatAdapter);

        // layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
        recycler_ListUser.setLayoutManager(layoutManager);

        // item decoration
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(requireActivity(),DividerItemDecoration.VERTICAL);
        recycler_ListUser.addItemDecoration(itemDecoration);

        // load list user
        loadListUser();
        return view;
    }

    private void loadListUser() {
        userList = new ArrayList<>();
        DatabaseReference referenceUser = FirebaseDatabase.getInstance().getReference("User");

        referenceUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for (DataSnapshot data : snapshot.getChildren())
                {
                    User user = data.getValue(User.class);

                    if (user != null) {
                        if (!user.isAdmin())
                        {
                            userList.add(user);
                            userChatAdapter.setListUser(userList);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
