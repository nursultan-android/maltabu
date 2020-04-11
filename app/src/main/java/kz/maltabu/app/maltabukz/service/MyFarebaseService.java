package kz.maltabu.app.maltabukz.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import io.paperdb.Paper;
import kz.maltabu.app.maltabukz.ui.activity.MainActivity;

import static kz.maltabu.app.maltabukz.App.CHANNEL_ID;

public class MyFarebaseService extends FirebaseMessagingService {
    private NotificationManagerCompat notificationManager;
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        notificationManager = NotificationManagerCompat.from(this);
        showNotification(remoteMessage.getData());
        Log.d("FirebaseTag", "From: " + remoteMessage.getFrom());

        if (remoteMessage.getData().size() > 0) {
            Log.d("FirebaseTag", "Message data payload: " + remoteMessage.getData());
        }

        if (remoteMessage.getNotification() != null) {
            Log.d("FirebaseTag", "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }
    }

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        Log.d("FirebaseTag", "Refreshed token: " + token);
        Paper.book().write("firebaseToken", token);
    }

    public void showNotification(Map<String, String> notificationData){
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSound(getAlarmSound())
                .setContentIntent(createPendingIntent())
                .setContentTitle(notificationData.get("title"))
                .setContentText(notificationData.get("body"))
                .setAutoCancel(true)
                .build();

        notificationManager.notify(1,notification);
    }

    private Uri getAlarmSound(){
        return RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
    }

    private PendingIntent createPendingIntent(){
        Intent resultIntent = new Intent(this, MainActivity.class);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0, resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        return resultPendingIntent;
    }
}
