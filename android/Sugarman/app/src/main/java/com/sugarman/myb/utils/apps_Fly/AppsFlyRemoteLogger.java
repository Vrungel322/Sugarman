package com.sugarman.myb.utils.apps_Fly;

import android.content.Context;
import com.appsflyer.AppsFlyerLib;
import com.google.firebase.database.FirebaseDatabase;
import com.sugarman.myb.utils.SharedPreferenceHelper;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import lombok.AllArgsConstructor;
import timber.log.Timber;

/**
 * Created by nikita on 09.02.2018.
 */
@AllArgsConstructor public class AppsFlyRemoteLogger {
  private Context mContext;

  public void report(String eventName, Map<String, String> events) {
    if(SharedPreferenceHelper.isRemoteLoggingEnabled()) {
      //if (SharedPreferenceHelper.isNeedToLogUser) {
      Map<String, String> eventValue = new HashMap<>();
      eventValue.put("af_imei", SharedPreferenceHelper.getIMEI());
      eventValue.put("af_platform", "android");
      eventValue.put("af_user_id", SharedPreferenceHelper.getUserId());
      eventValue.put("af_timezone", TimeZone.getDefault().getID());
      eventValue.put("af_timestamp", System.currentTimeMillis() + "");
      if (events != null) {
        Timber.e("Events!=0");
        eventValue.putAll(events);
        FirebaseDatabase.getInstance()
            .getReference()
            .child("remoteLoggingAndroid")
            .child("serverReport")
            .child(SharedPreferenceHelper.getUserName() + " : " + SharedPreferenceHelper.getUserId())
            .push()
            .setValue(eventValue)
            .addOnCompleteListener(task -> {
              Timber.e("Event log pushed");
            })
            .addOnFailureListener(task -> {
              Timber.e("Error " + task.getLocalizedMessage());
            });
      }
      AppsFlyerLib.getInstance().trackEvent(mContext, eventName, new HashMap<>());
      //}
    }


  }
}
