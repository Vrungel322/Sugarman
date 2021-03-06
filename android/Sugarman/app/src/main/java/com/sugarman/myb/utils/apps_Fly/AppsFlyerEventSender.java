package com.sugarman.myb.utils.apps_Fly;

import com.appsflyer.AppsFlyerLib;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sugarman.myb.App;
import com.sugarman.myb.utils.SharedPreferenceHelper;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class AppsFlyerEventSender {

  public static void sendEvent(String eventKey) {
    Map<String, Object> eventValue = new HashMap<>();
    eventValue.put("event", eventKey);
    eventValue.put("imei", SharedPreferenceHelper.getIMEI());
    eventValue.put("platform", "android");
    eventValue.put("user_id", SharedPreferenceHelper.getUserId());
    eventValue.put("timezone", TimeZone.getDefault().getID());
    eventValue.put("timestamp", System.currentTimeMillis() + "");

    if (SharedPreferenceHelper.isRemoteLoggingEnabled()) {
      AppsFlyerLib.getInstance()
          .trackEvent(App.getInstance().getApplicationContext(), eventKey, eventValue);

      DatabaseReference databaseRefference;
      databaseRefference = FirebaseDatabase.getInstance().getReference();
      databaseRefference.child("remoteLoggingAndroid")
          .child("event")
          .child(SharedPreferenceHelper.getUserName() + " : " + SharedPreferenceHelper.getUserId())
          .push()
          .setValue(eventValue)
          .addOnCompleteListener(task -> {

          });
    }
  }
}
