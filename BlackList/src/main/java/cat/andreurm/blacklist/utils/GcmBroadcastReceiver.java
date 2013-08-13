package cat.andreurm.blacklist.utils;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.json.JSONArray;
import org.json.JSONObject;

import cat.andreurm.blacklist.R;
import cat.andreurm.blacklist.activities.LoginActivity;
import cat.andreurm.blacklist.activities.PromoterCodeActivity;

/**
 * Handling of GCM messages.
 */
public class GcmBroadcastReceiver extends BroadcastReceiver {
    static final String TAG = "AND-PUSH-BROADCAST";
    public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;
    Context ctx;
    @Override
    public void onReceive(Context context, Intent intent) {

        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(context);
        ctx = context;
        /*String messageType = gcm.getMessageType(intent);

        if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
            sendNotification("Send error: " + intent.getExtras().toString());
        } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
            sendNotification("Deleted messages on server: " +
                    intent.getExtras().toString());
        } else {
            sendNotification("Received: " + intent.getExtras().toString());
        }*/
        Bundle extras = intent.getExtras();
        String msg= msg=extras.getString("message");

        sendNotification(msg);
        setResultCode(Activity.RESULT_OK);

    }

    // Put the GCM message into a notification and post it.
    private void sendNotification(String msg) {


       mNotificationManager = (NotificationManager)
                ctx.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(ctx, 0,
                new Intent(ctx, LoginActivity.class), 0);

        long[] pattern = new long[2];
        pattern[0]=100;
        pattern[1]=500;

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(ctx)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle(ctx.getString(R.string.blacklist))
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(msg))
                        .setContentText(msg)
                        .setVibrate(pattern)
                        .setAutoCancel(true)
                        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }
}