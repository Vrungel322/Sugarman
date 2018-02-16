package com.sugarman.myb.utils.apps_Fly;

import android.content.Context;
import com.appsflyer.AppsFlyerLib;
import com.sugarman.myb.utils.SharedPreferenceHelper;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import lombok.AllArgsConstructor;

/**
 * Created by nikita on 09.02.2018.
 */
@AllArgsConstructor public class AppsFlyRemoteLogger {
  private Context mContext;

  public void report(String eventName, Map<String, Object> events) {
    //if (SharedPreferenceHelper.isNeedToLogUser) {
      Map<String, Object> eventValue = new HashMap<>();
      eventValue.put("af_imei", SharedPreferenceHelper.getIMEI());
      eventValue.put("af_platform", "android");
      eventValue.put("af_user_id", SharedPreferenceHelper.getUserId());
      eventValue.put("af_timezone", TimeZone.getDefault().getID());
      eventValue.put("af_timestamp", System.currentTimeMillis() + "");
    if (events != null) {
      eventValue.putAll(events);
    }
      AppsFlyerLib.getInstance().trackEvent(mContext, eventName, eventValue);
    //}



  }
}
