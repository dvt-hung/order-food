package com.example.orderfood.DataLocal;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import com.example.orderfood.R;

public class FoodApplication extends Application {

    public static final String CHANNEL_ADMIN = "CHANNEL_ADMIN";
    public static final String CHANNEL_USER = "CHANNEL_USER";

    @Override
    public void onCreate() {
        super.onCreate();
        DataLocalPreferences.init(getApplicationContext());
        createNotification();
    }

    private void createNotification() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            //Channel Admin
            CharSequence name = getString(R.string.channel_name_admin);
            String description = getString(R.string.channel_des_admin);
            int important = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ADMIN,name,important);
            notificationChannel.setDescription(description);


            //Channel User
            CharSequence name_user = getString(R.string.channel_name_user);
            String description_user = getString(R.string.channel_des_user);
            int important_user = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel notificationChannelUser = new NotificationChannel(CHANNEL_USER,name_user,important_user);
            notificationChannelUser.setDescription(description_user);

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(notificationChannel);
            manager.createNotificationChannel(notificationChannelUser);
        }
    }

}
