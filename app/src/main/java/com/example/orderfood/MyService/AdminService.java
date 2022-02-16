package com.example.orderfood.MyService;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.orderfood.Activity.AdminMainActivity;
import com.example.orderfood.Activity.ChatActivity;
import com.example.orderfood.Activity.MainActivity;
import com.example.orderfood.Activity.SaleActivity;
import com.example.orderfood.Activity.SignInActivity;
import com.example.orderfood.DataLocal.FoodApplication;
import com.example.orderfood.Fragment.AdminFragment_Order;
import com.example.orderfood.Model.Message;
import com.example.orderfood.Model.Order;
import com.example.orderfood.Model.User;
import com.example.orderfood.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;
import java.util.HashMap;
import java.util.Random;

public class AdminService extends Service {

    private DatabaseReference referenceOrder;
    private DatabaseReference referenceChat;

    private final String idAdmin = "WnafxLx5uwPFczStY2woEhQD5Rt1";


    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        referenceChat = FirebaseDatabase.getInstance().getReference("Chat");

        referenceOrder = database.getReference("Order");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        getOrder();
        getMessage();
        return START_NOT_STICKY;
    }

    // ============= GET NEW MESSAGE ================
    private void getMessage() {
        referenceChat.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Message message = snapshot.getValue(Message.class);
                assert message != null;
                if (SignInActivity.currentUser.getUid().equals(idAdmin) && message.getReceiverID().equals(idAdmin) && !message.isSeen()) {
                    sendMessageAdmin(message);
                    // Update isSeen
                    HashMap<String,Object> seen = new HashMap<>();
                    seen.put("seen",true);
                    snapshot.getRef().updateChildren(seen);
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

    private void sendMessageAdmin(Message message) {
        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.logo);
        Intent intentChatAdmin = new Intent(this, ChatActivity.class);
        intentChatAdmin.putExtra("idUser", message.getSenderID());
        @SuppressLint("UnspecifiedImmutableFlag")
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intentChatAdmin, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder notificationChatAdmin = new NotificationCompat.Builder(this, FoodApplication.CHANNEL_ADMIN)
                .setContentTitle("Thông báo tin nhắn mới")
                .setContentText("Bạn có 1 tin nhắn mới: " + message.getMessage())
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setLargeIcon(bm)
                .setSmallIcon(R.drawable.logo);

        NotificationManager managerChat = (NotificationManager) getBaseContext().getSystemService(Context.NOTIFICATION_SERVICE);
        managerChat.notify((int) message.getTime(), notificationChatAdmin.build());
    }

    // ============= GET ORDER ================
    private void getOrder() {

        referenceOrder.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                Order order = snapshot.getValue(Order.class);
                if (order != null) {
                    order.setIdOrder(snapshot.getKey());
                    if (order.getStatusOrder() == 0 && !order.isSeen() && SignInActivity.currentUser.getUid().equals(idAdmin)) {
                        sendOrderAdd(order);
                        HashMap<String, Object> seen = new HashMap<>();
                        seen.put("seen", true);
                        snapshot.getRef().updateChildren(seen);
                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Order order = snapshot.getValue(Order.class);
                if (order != null) {
                    if (order.getStatusOrder() == 2 && !order.isSeen() && SignInActivity.currentUser.getUid().equals(idAdmin)) {
                        sendOrderConfirm(order);
                        HashMap<String, Object> seen = new HashMap<>();
                        seen.put("seen", true);
                        snapshot.getRef().updateChildren(seen);
                    }
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                Order order = snapshot.getValue(Order.class);
                assert order != null;
                if (SignInActivity.currentUser.getUid().equals(idAdmin)) {
                    order.setIdOrder(snapshot.getKey());
                    sendOrderDelete(order);
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void sendOrderAdd(Order order) {
        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.logo);
        Intent intentNewOrder = new Intent(this, AdminMainActivity.class);
        intentNewOrder.putExtra("newOrder", "StrNewOrder");
        @SuppressLint("UnspecifiedImmutableFlag")
        PendingIntent pendingIntentNewOrder = PendingIntent.getActivity(this, 0, intentNewOrder, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder notificationOrderAdmin = new NotificationCompat.Builder(this, FoodApplication.CHANNEL_ADMIN)
                .setContentTitle("Thông báo đơn hàng mới")
                .setContentText("Đơn hàng mới: " + order.getIdOrder())
                .setContentIntent(pendingIntentNewOrder)
                .setLargeIcon(bm)
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.logo);

        NotificationManager managerOrder = (NotificationManager) getBaseContext().getSystemService(Context.NOTIFICATION_SERVICE);
        managerOrder.notify((int) order.getDateOrder(), notificationOrderAdmin.build());
    }

    private void sendOrderDelete(Order order) {
        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.logo);

        @SuppressLint("UnspecifiedImmutableFlag")
        PendingIntent pendingIntentDeleteOrder = PendingIntent.getActivity(this, 0, new Intent(), PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notificationOrderAdmin = new NotificationCompat.Builder(this, FoodApplication.CHANNEL_ADMIN)
                .setContentTitle("Đơn hàng bị hủy")
                .setContentText(order.getIdOrder() + " đã bị hủy")
                .setLargeIcon(bm)
                .setAutoCancel(true)
                .setContentIntent(pendingIntentDeleteOrder)
                .setSmallIcon(R.drawable.logo);

        NotificationManager managerOrder = (NotificationManager) getBaseContext().getSystemService(Context.NOTIFICATION_SERVICE);
        managerOrder.notify((int) order.getDateOrder(), notificationOrderAdmin.build());
    }

    private void sendOrderConfirm(Order order) {
        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.logo);

        Intent intentConfirmOrder = new Intent(this, SaleActivity.class);
        @SuppressLint("UnspecifiedImmutableFlag")
        PendingIntent pendingIntentConfirmOrder = PendingIntent.getActivity(this, 0, intentConfirmOrder, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notificationOrderConfirm = new NotificationCompat.Builder(this, FoodApplication.CHANNEL_ADMIN)
                .setContentTitle("Đơn hàng hoàn thành")
                .setContentText(order.getIdOrder() + " đã hoàn thành")
                .setSmallIcon(R.drawable.logo)
                .setLargeIcon(bm)
                .setAutoCancel(true)
                .setContentIntent(pendingIntentConfirmOrder);

        NotificationManager managerOrder = (NotificationManager) getBaseContext().getSystemService(Context.NOTIFICATION_SERVICE);
        managerOrder.notify((int) order.getDateOrder(), notificationOrderConfirm.build());

    }

}
