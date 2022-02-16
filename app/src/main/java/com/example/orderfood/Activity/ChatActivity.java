package com.example.orderfood.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ChatActivity extends AppCompatActivity {

    private ImageView img_Back, btn_Send;
    private TextView txt_Name_Chat;
    private RecyclerView recycler_Chat;
    private EditText edt_SendMessage_Chat;
    private MessageAdapter chatAdapter;
    private List<Message> messageList;
    private FirebaseUser admin;
    private User userChat;
    private String idUserChat;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // init view
        initView();

        // Get user chat
        idUserChat = getIntent().getStringExtra("idUser");
        getUser(idUserChat);

        // Adapter recycler
        chatAdapter = new MessageAdapter();
        recycler_Chat.setAdapter(chatAdapter);

        // Layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(this,RecyclerView.VERTICAL,false);
        recycler_Chat.setLayoutManager(layoutManager);
        layoutManager.setStackFromEnd(true);


        // load message
        loadMessage();
    }

    private void getUser(String idUser)
    {
        DatabaseReference referenceUser = FirebaseDatabase.getInstance().getReference("User");
        referenceUser.child(idUser).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User user = snapshot.getValue(User.class);
                    assert user != null;
                    userChat = user;
                    txt_Name_Chat.setText(userChat.getName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        btn_Send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mess = edt_SendMessage_Chat.getText().toString().trim();
                if (mess.isEmpty())
                {
                    Toast.makeText(getApplicationContext(), "Vui lòng nhập tin nhắn", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    edt_SendMessage_Chat.setText("");
                    sendMessage(mess);
                    closeKeyboard();
                }
            }
        });

        // btn Back
        img_Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void sendMessage(String mess) {
        Date date = new Date();
        Message message = new Message(admin.getUid(),idUserChat,mess,date.getTime(),false);

        DatabaseReference referenceMessage = FirebaseDatabase.getInstance().getReference("Chat");
        referenceMessage.push().setValue(message);

    }

    // ======= CLOSE KEYBOARD =======
    private void closeKeyboard()
    {
        View view = getCurrentFocus();
        if (view != null)
        {
            InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            manager.hideSoftInputFromWindow(view.getWindowToken(),0);
        }
    }

    private void loadMessage() {
        messageList = new ArrayList<>();
        DatabaseReference referenceMessage = FirebaseDatabase.getInstance().getReference("Chat");
        referenceMessage.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messageList.clear();
                for (DataSnapshot data : snapshot.getChildren())
                {
                    Message message = data.getValue(Message.class);
                    assert message != null;
                    if (message.getReceiverID().equals(admin.getUid()) && message.getSenderID().equals(idUserChat) ||
                            message.getReceiverID().equals(idUserChat) && message.getSenderID().equals(admin.getUid()))
                    {
                        messageList.add(message);
                        chatAdapter.setDataMessage(messageList);
                        recycler_Chat.smoothScrollToPosition(messageList.size());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void initView() {
        img_Back                = findViewById(R.id.img_Back_Chat);
        btn_Send                = findViewById(R.id.btn_SendMessage_Chat);
        txt_Name_Chat           = findViewById(R.id.txt_Name_Chat);
        recycler_Chat           = findViewById(R.id.recycler_Chat);
        edt_SendMessage_Chat    = findViewById(R.id.edt_SendMessage_Chat);
        admin                   = FirebaseAuth.getInstance().getCurrentUser();
    }
}