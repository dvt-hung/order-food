package com.example.orderfood.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.orderfood.Adapter.MessageAdapter;
import com.example.orderfood.Model.Message;
import com.example.orderfood.Model.User;
import com.example.orderfood.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class Fragment_Chat extends Fragment {
    private RecyclerView recycler_ChatUser;
    private EditText edt_MessageUser;
    private ImageView btn_SendMessageUser;
    private FirebaseUser currentUser;
    private List<Message> messageList;
    private MessageAdapter messageAdapter;
    private final String idAdmin = "WnafxLx5uwPFczStY2woEhQD5Rt1";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat,container,false);
        
        // init view
        initView(view);


        // adapter recycler
        messageAdapter = new MessageAdapter();
        recycler_ChatUser.setAdapter(messageAdapter);

        // layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireActivity(),LinearLayoutManager.VERTICAL,false);
        layoutManager.setStackFromEnd(true);
        recycler_ChatUser.setLayoutManager(layoutManager);


        // load message
        loadMessage();

        return view;
    }




    @Override
    public void onResume() {
        super.onResume();
        btn_SendMessageUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mess = edt_MessageUser.getText().toString().trim();
                if (mess.isEmpty())
                {
                    Toast.makeText(getActivity(), "Vui lòng nhập tin nhắn", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    edt_MessageUser.setText("");
                    sendMessage(mess);
                    closeKeyboard();
                }
                
            }
        });
    }

    private void loadMessage() {
        messageList = new ArrayList<>();
        DatabaseReference referenceChat = FirebaseDatabase.getInstance().getReference("Chat");

        referenceChat.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                    messageList.clear();
                    for (DataSnapshot data : snapshot.getChildren())
                    {
                        Message message = data.getValue(Message.class);

                        assert message != null;
                        if (message.getSenderID().equals(currentUser.getUid()) && message.getReceiverID().equals(idAdmin) ||
                        message.getSenderID().equals(idAdmin) && message.getReceiverID().equals(currentUser.getUid()))
                        {
                            messageList.add(message);
                            HashMap<String,Object> seen = new HashMap<>();
                            seen.put("seen",true);
                            data.getRef().updateChildren(seen);
                        }
                    }
                    messageAdapter.setDataMessage(messageList);
                    recycler_ChatUser.smoothScrollToPosition(messageList.size());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void sendMessage(String mess) {
        Date date = new Date();
        Message message = new Message(currentUser.getUid(),idAdmin,mess,date.getTime(),false);

        DatabaseReference referenceChat = FirebaseDatabase.getInstance().getReference("Chat");
        referenceChat.push().setValue(message);
    }

    // ======= CLOSE KEYBOARD =======
    private void closeKeyboard()
    {
        View view = requireActivity().getCurrentFocus();
        if (view != null)
        {
            InputMethodManager manager = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            manager.hideSoftInputFromWindow(view.getWindowToken(),0);
        }
    }
    private void initView(View view) {
        recycler_ChatUser = view.findViewById(R.id.recycler_Chat_User);
        edt_MessageUser = view.findViewById(R.id.edt_SendMessage_User);
        btn_SendMessageUser = view.findViewById(R.id.btn_SendMessage_User);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
    }
}
