package com.sugarman.myb.tasks;

import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import com.sugarman.myb.constants.Config;
import com.sugarman.myb.constants.Constants;
import com.sugarman.myb.listeners.OnReportSendListener;
import com.sugarman.myb.utils.SharedPreferenceHelper;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

public class SendReportAsyncTask extends AsyncTask<Void, Void, Void> {

  private static final String TAG = "!!!";//SendReportAsyncTask.class.getName();

  private final String body;
  private final int addedSteps;
  private final int todaySteps;
  private final int currentDay;
  private final HashMap<String, String> headers = new HashMap<>(2);
  private int responseCode;
  private final WeakReference<OnReportSendListener> endListener;

  public SendReportAsyncTask(OnReportSendListener listener, String body, int addedSteps,
      int todaySteps, int currentDay) {
    this.body = body;
    this.addedSteps = addedSteps;
    this.todaySteps = todaySteps;
    this.currentDay = currentDay;
    endListener = new WeakReference<>(listener);
    String accessToken = SharedPreferenceHelper.getAccessToken();

    headers.put(Constants.AUTHORIZATION, Constants.BEARER + accessToken);
    headers.put(Constants.CONTENT_TYPE, Constants.APP_JSON);
    headers.put(Constants.TIMEZONE, TimeZone.getDefault().getID());
    headers.put(Constants.TIMESTAMP, System.currentTimeMillis() + "");
  }

  @Override protected Void doInBackground(Void... params) {
    if (!TextUtils.isEmpty(body)) {
      URL url = null;
      try {
        url = new URL(Config.SERVER_URL + Constants.REPORT_ENDPOINT);
        url = new URL(SharedPreferenceHelper.getBaseUrl() + Constants.REPORT_ENDPOINT);
      } catch (MalformedURLException e) {
        Log.e(TAG, "failure parse url", e);
      }

      HttpURLConnection connection = null;
      if (url != null) {
        try {
          connection = (HttpURLConnection) url.openConnection();
        } catch (IOException e) {
          Log.e(TAG, "failure open connection", e);
        }
      }

      if (connection != null) {
        try {
          connection.setRequestMethod(Constants.POST);
          connection.setDoInput(true);
          connection.setDoOutput(true);

          Set<Map.Entry<String, String>> entrySet = headers.entrySet();
          for (Map.Entry<String, String> entry : entrySet) {
            String key = entry.getKey();
            String value = entry.getValue();

            connection.addRequestProperty(key, value);
          }
        } catch (ProtocolException e) {
          Log.e(TAG, "failure prepare connection", e);
        }
      }

      OutputStream os = null;
      if (connection != null) {
        try {
          os = connection.getOutputStream();
          byte[] outputInBytes = body.getBytes(Constants.UTF_8);
          os.write(outputInBytes);
          os.close();

          responseCode = connection.getResponseCode();
          Log.d(TAG, "response code: " + responseCode);
        } catch (IOException e) {
          Log.e(TAG, "failure prepare connection", e);
        } finally {
          if (os != null) {
            try {
              os.close();
            } catch (IOException e) {
              Log.e(TAG, "failure close output stream", e);
            }
          }
        }
      }
    }

    return null;
  }

  @Override protected void onPostExecute(Void result) {
    super.onPostExecute(result);

    if (endListener.get() != null) {
      if (responseCode == Constants.SUCCESS_RESPONSE_CODE) {
        endListener.get().onApiReportSendSuccess(todaySteps, addedSteps, currentDay);
      } else {
        endListener.get().onApiReportSendFailure(addedSteps);
      }
    }
  }
}
