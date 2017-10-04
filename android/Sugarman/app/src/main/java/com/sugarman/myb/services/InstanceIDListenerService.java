package com.sugarman.myb.services;

import android.util.Log;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.sugarman.myb.App;
import com.sugarman.myb.eventbus.events.RefreshGCMTokenEvent;
import com.sugarman.myb.utils.SharedPreferenceHelper;

public class InstanceIDListenerService extends FirebaseInstanceIdService {

  private static final String TAG = InstanceIDListenerService.class.getName();

  @Override public void onTokenRefresh() {
    String refreshedToken = FirebaseInstanceId.getInstance().getToken();
    Log.d(TAG, "Refreshed token: " + refreshedToken);

    SharedPreferenceHelper.saveGCMToken(refreshedToken);

    App.getEventBus().post(new RefreshGCMTokenEvent(refreshedToken));
  }
}