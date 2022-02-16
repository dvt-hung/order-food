package com.example.orderfood.MyService;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.orderfood.Activity.MainActivity;
import com.example.orderfood.Activity.SignInActivity;
import com.example.orderfood.DataLocal.FoodApplication;
import com.example.orderfood.Activity.DetailsOrderActivity;
import com.example.orderfood.Model.Message;
import com.example.orderfood.Model.Order;
import com.example.orderfood.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class UserService extends Service {

    private DatabaseReference referenceOrder;
    private DatabaseReference referenceChat;


    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        referenceOrder = database.getReference("Order");
        referenceChat = FirebaseDatabase.getInstance().getReference("Chat");

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        getOrderUser();
        getMessageUser();
        return START_NOT_STICKY;
    }

    // ============= GET MESSAGE ================
    private void getMessageUser() {
        referenceChat.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Message message = snapshot.getValue(Message.class);
                assert message != null;
                if (message.getReceiverID().equals(SignInActivity.currentUser.getUid()) && !message.isSeen()) {
                    sendMessageUser(message);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void sendMessageUser(Message message) {
        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.logo);
        Intent intentChatUser = new Intent(this, MainActivity.class);
        intentChatUser.putExtra("messageUser", "ChatUser");
        @SuppressLint("UnspecifiedImmutableFlag")
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intentChatUser, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder notificationChatAdmin = new NotificationCompat.Builder(this, FoodApplication.CHANNEL_USER)
                .setContentTitle("Bạn có 1 tin nhắn mới")
                .setContentText(message.getMessage())
                .setAutoCancel(true)
                .setLargeIcon(bm)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.logo);

        NotificationManager managerChat = (NotificationManager) getBaseContext().getSystemService(Context.NOTIFICATION_SERVICE);
        managerChat.notify((int) message.getTime(), notificationChatAdmin.build());
    }


    // ============= GET ORDER ================
    private void getOrderUser() {
        referenceOrder.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Order order = snapshot.getValue(Order.class);
                if (order != null) {
                    order.setIdOrder(snapshot.getKey());
                    if (order.getStatusOrder() == 1 && order.getUser().getId().equals(SignInActivity.currentUser.getUid()) && !order.isSeen())
                    {
                            sendForUser(order);
                            HashMap<String, Object> seen = new HashMap<>();
                            seen.put("seen", true);
                            snapshot.getRef().updateChildren(seen);
                    }
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void sendForUser(Order order) {
        Intent intent = new Intent(this, DetailsOrderActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("order", order);
        intent.putExtras(bundle);
        @SuppressLint("UnspecifiedImmutableFlag")
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notificationOrder = new NotificationCompat.Builder(this, FoodApplication.CHANNEL_USER)
                .setContentTitle("Đơn hàng: " + order.getIdOrder())
                .setContentText("Đơn hàng của bạn đang được giao")
                .setSmallIcon(R.drawable.logo)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManager managerOrder = (NotificationManager) getBaseContext().getSystemService(Context.NOTIFICATION_SERVICE);
        managerOrder.notify((int) order.getDateOrder(), notificationOrder.build());
    }

  
}
