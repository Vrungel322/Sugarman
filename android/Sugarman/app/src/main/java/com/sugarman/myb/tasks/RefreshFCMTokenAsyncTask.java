package com.sugarman.myb.tasks;

import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import com.google.firebase.iid.FirebaseInstanceId;
import com.sugarman.myb.utils.SharedPreferenceHelper;

public class RefreshFCMTokenAsyncTask extends AsyncTask<Void, Void, Void> {

  @Override protected Void doInBackground(Void... params) {
    String gcmToken = SharedPreferenceHelper.getGCMToken();
    if (TextUtils.isEmpty(gcmToken)) {
      Log.d("fcm", "request fcm token");
      FirebaseInstanceId.getInstance().getToken();
    } else {
      Log.d("fcm", "fcm token is exist:" + gcmToken);
    }
    return null;
  }
}