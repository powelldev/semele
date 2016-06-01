package com.fireminder.semele;

import android.app.Notification;
import android.support.v4.app.NotificationCompat;

public class ActiveSessionNotificationManager {

  /**
   *
   * @param contentText same as NotificationBuilder.setContentTitle
   * @param contentTitle same as Notification.setContentText
   * @param contentSubtitle will append itself to contentTitle for bigText view
   * @return
   */
  public Notification updateNotification(String contentTitle,
                                         String contentText,
                                         String contentSubtitle) {

    NotificationCompat.Builder builder = new NotificationCompat.Builder(SemeleApp.get())
        .setSmallIcon(R.mipmap.ic_launcher)
        .setContentTitle(contentTitle)
        .setContentText(contentText)
        .setStyle(new NotificationCompat.BigTextStyle()
            .bigText(contentText + "\n" + contentSubtitle));
   // builder.addAction(R.mipmap.ic_launcher, "Dismiss", dismissIntent);
    return builder.build();
  }

  public Notification getBaseNotification() {
    NotificationCompat.Builder builder = new NotificationCompat.Builder(SemeleApp.get())
        .setSmallIcon(R.mipmap.ic_launcher)
        .setContentTitle("base")
        .setContentText("base")
        .setStyle(new NotificationCompat.BigTextStyle()
            .bigText("base"));
    return builder.build();
  }
}
