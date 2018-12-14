package com.me.priya.mychatapp.notifications;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.net.Uri;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.support.annotation.RequiresApi;

/**
 * Created by Priya Jain on 14,December,2018
 */
public class OreoNotification extends ContextWrapper {

  private NotificationManager notificationManager;
  private static final String CHANEL_ID = "com.priya.mychatapp";
  private static final String CHANEL_NAME = "mychatapp";
  public OreoNotification(Context base) {
    super(base);
    if(VERSION.SDK_INT >= Build.VERSION_CODES.O){
      createChannel();
    }
  }

  @RequiresApi(api = VERSION_CODES.O)
  private void createChannel() {
    NotificationChannel channel = new NotificationChannel(CHANEL_ID,
        CHANEL_NAME,
        NotificationManager.IMPORTANCE_DEFAULT);
    channel.enableLights(false);
    channel.enableVibration(true);
    channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

    getManager().createNotificationChannel(channel);
  }
  public NotificationManager getManager(){
    if(notificationManager == null){
      notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
    }
    return notificationManager;
  }
  @RequiresApi(api = VERSION_CODES.O)
  public Notification.Builder getOreoNotification(String title, String body,
      PendingIntent pendingIntent, Uri soundUri, String icon){
    return new Notification.Builder(getApplicationContext(), CHANEL_ID)
        .setContentIntent(pendingIntent)
        .setContentTitle(title)
        .setContentText(body)
        .setSmallIcon(Integer.parseInt(icon))
        .setSound(soundUri)
        .setAutoCancel(true);
  }
}
