package com.sugarman.myb.utils.apps_Fly;

import com.appsflyer.AppsFlyerLib;
import com.sugarman.myb.App;
import java.util.HashMap;

/**
 * Created by yegoryeriomin on 2/15/18.
 */

public class AppsFlyerEventSender {
  public static void sendEvent(String eventKey)
  {
    AppsFlyerLib.getInstance()
        .trackEvent(App.getInstance().getApplicationContext(), eventKey, new HashMap<>());

    
  }
}
