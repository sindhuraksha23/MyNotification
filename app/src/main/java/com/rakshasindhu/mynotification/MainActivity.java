package com.rakshasindhu.mynotification;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.RemoteInput;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button b1;
    public static int notificationId = 1;
    public static final String KEY_NOTIFICATION_REPLY = "KEY_NOTIFICATION_REPLY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        b1 = (Button) findViewById(R.id.button);

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // NotificationCompat Builder takes care of backwards compatibility and
                // provides clean API to create rich notifications

                Intent detailsIntent = new Intent(MainActivity.this, DetailsActivity.class);
                detailsIntent.putExtra("EXTRA_DETAILS_ID", 42);
                PendingIntent detailsPendingIntent = PendingIntent.getActivity(MainActivity.this, 0, detailsIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                PendingIntent replyPendingIntent = null;
                // Call Activity on platforms that don't support DirectReply natively
                if (Build.VERSION.SDK_INT < 24) {
                    replyPendingIntent = detailsPendingIntent;
                } else { // Call BroadcastReceiver on platforms supporting DirectReply
                    replyPendingIntent = PendingIntent.getBroadcast(
                            MainActivity.this,
                            0,
                            new Intent(MainActivity.this, ReplyReceiver.class),
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );
                }

                // Create RemoteInput and attach it to Notification Action
                RemoteInput remoteInput = new RemoteInput.Builder(KEY_NOTIFICATION_REPLY)
                        .setLabel("Reply")
                        .build();
                NotificationCompat.Action replyAction = new NotificationCompat.Action.Builder(
                        android.R.drawable.ic_menu_save, "Provide ID", replyPendingIntent)
                        .addRemoteInput(remoteInput)
                        .build();


                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(MainActivity.this)
                        .setSmallIcon(android.R.drawable.ic_dialog_info)
                        .setContentTitle("Notification title")
                        .setContentText("Content text")
                        .setContentIntent(detailsPendingIntent)
                        .setAutoCancel(true)
                        .addAction(replyAction)
                        .setContentIntent(detailsPendingIntent)
                        .addAction(android.R.drawable.ic_menu_compass, "Details", detailsPendingIntent)
                        .addAction(android.R.drawable.ic_menu_directions, "Show Map", detailsPendingIntent);

                // Obtain NotificationManager system service in order to show the notification
                NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                notificationManager.notify(notificationId, mBuilder.build());


            }
        });
    }

}
