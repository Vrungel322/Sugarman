package com.sugarman.myb.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import com.sugarman.myb.App;
import com.sugarman.myb.constants.Constants;
import com.sugarman.myb.eventbus.events.ChangeConnectionEvent;
import com.sugarman.myb.eventbus.events.ReportStepsEvent;
import com.sugarman.myb.utils.DeviceHelper;

public class NetworkConnectionReceiver extends BroadcastReceiver {

  private static final String TAG = NetworkConnectionReceiver.class.getName();

  @Override public void onReceive(final Context context, final Intent intent) {
    String action = intent.getAction();
    if (!TextUtils.isEmpty(action) && (TextUtils.equals(action,
        Constants.CONNECTIVITY_CHANGE_ACTION) || TextUtils.equals(action,
        Constants.WIFI_CHANGE_ACTION))) {

      int type = DeviceHelper.getConnectivityType();

      if (DeviceHelper.CURRENT_TYPE_CONNECT != type) {
        DeviceHelper.CURRENT_TYPE_CONNECT = type;

        boolean isConnected = (type == DeviceHelper.TYPE_CONNECTED_MOBILE
            || type == DeviceHelper.TYPE_CONNECTED_WIFI);

        if (isConnected) {
          App.getEventBus().post(new ReportStepsEvent());
        }
        App.getEventBus().post(new ChangeConnectionEvent(isConnected));
        Log.d(TAG, getConnectivityStatus(type));
      }
    }
  }

  private static String getConnectivityStatus(int networkConnectionType) {
    String status;
    switch (networkConnectionType) {
      case DeviceHelper.TYPE_CONNECTED_WIFI:
        status = "Connected to Internet by WiFi";
        break;
      case DeviceHelper.TYPE_CONNECTED_MOBILE:
        status = "Connected to Internet by mobile";
        break;
      case DeviceHelper.TYPE_NOT_CONNECTED:
        status = "Not connected to Internet";
        break;
      default:
        status = "Unknown";
        break;
    }

    return status;
  }
}