package com.sugarman.myb.tasks;

import android.os.AsyncTask;
import android.util.Log;
import com.google.firebase.iid.FirebaseInstanceId;
import com.sugarman.myb.utils.SharedPreferenceHelper;
import java.io.IOException;

public class DeleteFCMTokenAsyncTask extends AsyncTask<Void, Void, Void> {

  private static final String TAG = DeleteFCMTokenAsyncTask.class.getName();

  @Override protected Void doInBackground(Void... params) {
    try {
      FirebaseInstanceId.getInstance().deleteInstanceId();
      SharedPreferenceHelper.saveGCMToken("");
      Log.d("fcm", "delete fcm token");
    } catch (IOException e) {
      Log.e(TAG, "Couldn't delete fcm token");
    }
    return null;
  }
}