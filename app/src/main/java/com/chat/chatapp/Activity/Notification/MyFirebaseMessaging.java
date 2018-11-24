package com.chat.chatapp.Activity.Notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.chat.chatapp.Activity.Activity.MessageActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessaging extends FirebaseMessagingService {

    // vars
    FirebaseUser firebaseUser;
    String sented = "", user = "", icon = "", title = "", body = "", current_user;
    RemoteMessage.Notification notification;
    int j;
    Bundle bundle;
    PendingIntent pendingIntent;
    Uri defaultSound;
    NotificationCompat.Builder builder;
    NotificationManager notificationManager;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        sented = remoteMessage.getData().get("sented");
        current_user = remoteMessage.getData().get("user");

        SharedPreferences preferences = getSharedPreferences("PREFS", MODE_PRIVATE);
        String currentUser = preferences.getString("currentUser", "none");

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null && sented.equals(firebaseUser.getUid())) ;
        {
            if (!currentUser.equals(current_user)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    sendOreoNotification(remoteMessage);
                } else {
                    sendNotification(remoteMessage);

                }
            }

        }
    }

    private void sendOreoNotification(RemoteMessage remoteMessage) {
        user = remoteMessage.getData().get("user");
        icon = remoteMessage.getData().get("icon");
        title = remoteMessage.getData().get("title");
        body = remoteMessage.getData().get("body");

        notification = remoteMessage.getNotification();
        j = Integer.parseInt(user.replaceAll("[\\D]", ""));
        Intent message = new Intent(this, MessageActivity.class);
        bundle = new Bundle();
        bundle.putString("userid", user);
        message.putExtras(bundle);
        message.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        pendingIntent = PendingIntent.getActivity(this, j, message, PendingIntent.FLAG_ONE_SHOT);
        defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        OreoNotification oreoNotification = new OreoNotification(this);
        Notification.Builder builder = oreoNotification.getOreoNotification(title, body, pendingIntent, defaultSound, icon);

        int i = 0;
        if (j > 0) {
            i = j;
        }
        oreoNotification.getManager().notify(i, builder.build());

    } // sending notification to oreo version

    private void sendNotification(RemoteMessage remoteMessage) {

        user = remoteMessage.getData().get("user");
        icon = remoteMessage.getData().get("icon");
        title = remoteMessage.getData().get("title");
        body = remoteMessage.getData().get("body");

        notification = remoteMessage.getNotification();
        j = Integer.parseInt(user.replaceAll("[\\D]", ""));
        Intent message = new Intent(this, MessageActivity.class);
        bundle = new Bundle();
        bundle.putString("userid", user);
        message.putExtras(bundle);
        message.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        pendingIntent = PendingIntent.getActivity(this, j, message, PendingIntent.FLAG_ONE_SHOT);
        defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        builder = new NotificationCompat.Builder(this)
                .setSmallIcon(Integer.parseInt(icon))
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setSound(defaultSound)
                .setContentIntent(pendingIntent);

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        int i = 0;
        if (j > 0) {
            i = j;
        }
        notificationManager.notify(i, builder.build());

    } // sending notification method
}
