package com.sugarman.myb.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.JsonSyntaxException;
import com.sugarman.myb.App;
import com.sugarman.myb.R;
import com.sugarman.myb.api.models.responses.me.notifications.Notification;
import com.sugarman.myb.constants.Constants;
import com.sugarman.myb.constants.NotificationMessageType;
import com.sugarman.myb.eventbus.events.GetInAppNotificationsEvent;
import com.sugarman.myb.eventbus.events.RefreshTrackingsEvent;
import com.sugarman.myb.eventbus.events.ReportStepsEvent;
import com.sugarman.myb.eventbus.events.UpdateInvitesEvent;
import com.sugarman.myb.eventbus.events.UpdateRequestsEvent;
import com.sugarman.myb.ui.activities.MainActivity;
import com.sugarman.myb.ui.activities.SplashActivity;
import com.sugarman.myb.utils.SharedPreferenceHelper;
import com.sugarman.myb.utils.StringHelper;
import java.util.Map;
import java.util.Set;
import org.json.JSONException;
import org.json.JSONObject;
import timber.log.Timber;

public class FcmListenerService extends FirebaseMessagingService {

  private static final String TAG = FcmListenerService.class.getName();

  private String[] flags; // 14 types of notifications

  @Override public void onMessageReceived(RemoteMessage message) {
    if (TextUtils.isEmpty(SharedPreferenceHelper.getUserId())) {
      return;
    }
    if (flags == null) {
      flags = getResources().getStringArray(R.array.notifications_types);
    }

    App.getEventBus().post(new GetInAppNotificationsEvent());

    Map data = message.getData();
    String text = (String) data.get(Constants.FCM_MESSAGE);
    String notification = (String) data.get(Constants.FCM_NOTIFICATION);
    String url = "";
    try {
      JSONObject notificationJSON = new JSONObject(notification);
      url = notificationJSON.getString("url");
    } catch (JSONException e) {
      e.printStackTrace();
    }

    Set keys = data.keySet();
    for (Object key : keys) {
      String keyType = key.toString();
      Log.d(TAG, "key: " + keyType);
      Log.d(TAG, "val: " + data.get(key));

      switch (keyType) {
        case Constants.FCM_REPORT_STEPS_KEY:
          App.getEventBus().post(new ReportStepsEvent());
          break;
        case Constants.FCM_MESSAGE:
          Timber.e("Got in message");
          Timber.e("url " + url);
          if (url != null) {

            processURL(url, text);
          } else {
            processMessage(text, notification);
          }

          break;
        default:
          break;
      }
    }
  }

  private void processURL(String url, String message) {
    Timber.e("Process URL " + url + " " + message);

    Intent intent = new Intent(this, MainActivity.class);

    intent.putExtra(Constants.INTENT_OPEN_ACTIVITY, Constants.OPEN_EXTERNAL_URL);
    intent.putExtra(Constants.INTENT_FCM_URL, url);

    Timber.e(intent.getStringExtra(Constants.INTENT_FCM_URL) + " from fcm extra");

    PendingIntent pendingIntent =
        PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
    sendNotification(message, Constants.FIREBASE_NOTIFICATION_ID, pendingIntent);

    //startActivity(i);
  }

  private void sendNotification(String text, int notificationId, PendingIntent pendingIntent) {
    Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
    NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);

    Bitmap icon =
        BitmapFactory.decodeResource(App.getInstance().getResources(), R.mipmap.ic_launcher);
    Bitmap largeIcon = Bitmap.createScaledBitmap(icon, Constants.NOTIFICATION_LARGE_ICON_SIZE_PX,
        Constants.NOTIFICATION_LARGE_ICON_SIZE_PX, false);

    String title = getString(R.string.app_name);
    notificationBuilder.setSmallIcon(android.R.drawable.stat_notify_chat);
    notificationBuilder.setLargeIcon(largeIcon);
    notificationBuilder.setContentTitle(title);
    notificationBuilder.setContentText(text != null ? text : "");
    notificationBuilder.setAutoCancel(true);
    notificationBuilder.setSound(defaultSoundUri);
    notificationBuilder.setContentIntent(pendingIntent);

    NotificationManager notificationManager =
        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    notificationManager.notify(notificationId, notificationBuilder.build());
  }

  private void processMessage(String text, String notification) {
    int type = StringHelper.getNotificationMessageType(flags, text);
    Intent intent = new Intent(this, SplashActivity.class);
    String trackingId = getTrackingId(notification);
    Timber.e("Message" + type);

    switch (type) {
      case NotificationMessageType.GROUP_NAME_GOOD_LUCK:
      case NotificationMessageType.ONE_MORE_DAY_TO_ADD_FRIENDS:
      case NotificationMessageType.INVITATION_NO_AVAILABLE:
      case NotificationMessageType.IS_UNABLE_TO_START:
        // nothing
        break;
      case NotificationMessageType.PINGED_YOU_TO_MYB:
      case NotificationMessageType.USER_NAME_HAS_POKED_YOU:
      case NotificationMessageType.GROUP_NAME_IN_HOUR:
        intent.putExtra(Constants.INTENT_OPEN_ACTIVITY, Constants.OPEN_MAIN_ACTIVITY);
        intent.putExtra(Constants.INTENT_FCM_TRACKING_ID, trackingId);
        break;
      case NotificationMessageType.USER_NAME_JOINED:
      case NotificationMessageType.CREATOR_NAME_APPROVED:
        App.getEventBus().post(new RefreshTrackingsEvent(trackingId));
        intent.putExtra(Constants.INTENT_OPEN_ACTIVITY, Constants.OPEN_MAIN_ACTIVITY);
        intent.putExtra(Constants.INTENT_FCM_TRACKING_ID, trackingId);
        break;
      case NotificationMessageType.USER_NAME_INVITED:
        App.getEventBus().post(new UpdateInvitesEvent());
        intent.putExtra(Constants.INTENT_OPEN_ACTIVITY, Constants.OPEN_INVITES_ACTIVITY);
        break;
      case NotificationMessageType.CONGRATS:
        intent.putExtra(Constants.INTENT_OPEN_ACTIVITY, Constants.OPEN_CONGRATULATION_ACTIVITY);
        intent.putExtra(Constants.INTENT_FCM_TRACKING_ID, trackingId);
        break;
      case NotificationMessageType.DAILY_SUGARMAN:
        intent.putExtra(Constants.INTENT_OPEN_ACTIVITY, Constants.OPEN_DAILY_ACTIVITY);
        intent.putExtra(Constants.INTENT_FCM_TRACKING_ID, trackingId);
        break;
      case NotificationMessageType.GROUP_NAME_HAS_FAILED:
        intent.putExtra(Constants.INTENT_OPEN_ACTIVITY, Constants.OPEN_FAILED_ACTIVITY);
        intent.putExtra(Constants.INTENT_FCM_TRACKING_ID, trackingId);
        break;
      case NotificationMessageType.USER_NAME_REQUESTED:
        App.getEventBus().post(new UpdateRequestsEvent());
        intent.putExtra(Constants.INTENT_OPEN_ACTIVITY, Constants.OPEN_REQUESTS_ACTIVITY);
        break;
      default:
        break;
    }

    PendingIntent pendingIntent =
        PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
    sendNotification(text, Constants.FIREBASE_NOTIFICATION_ID, pendingIntent);
  }

  private void removeNotification(int notificationId) {
    NotificationManager notificationManager =
        (NotificationManager) getApplicationContext().getSystemService(
            Context.NOTIFICATION_SERVICE);
    notificationManager.cancel(notificationId);
  }

  private String getTrackingId(String jsonNotification) {
    Notification notification = null;
    if (!TextUtils.isEmpty(jsonNotification)) {
      try {
        notification = App.getGsonInstance().fromJson(jsonNotification, Notification.class);
      } catch (JsonSyntaxException e) {
        Log.e(TAG, "failure parse notification", e);
      }
    }

    return notification == null ? "" : notification.getTrackingId();
  }
}

