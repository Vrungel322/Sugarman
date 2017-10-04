package com.sugarman.myb.api.clients;

import android.os.AsyncTask;
import android.util.Log;
import com.sugarman.myb.App;
import com.sugarman.myb.api.models.requests.StatsRequest;
import com.sugarman.myb.listeners.OnReportSendListener;
import com.sugarman.myb.tasks.SendReportAsyncTask;

public class ReportStatsLightClient {

  private static final String TAG = ReportStatsLightClient.class.getName();

  public void sendStats(OnReportSendListener listener, StatsRequest request, int addedSteps,
      int todaySteps, int currentDay) {
    Log.d("!!!", "report today: " + request.getData()[0].getStepsCount());
    String bodyJson = App.getGsonInstance().toJson(request, StatsRequest.class);
    Log.d("!!!", "report json: " + bodyJson);
    Log.d("%%%", "report json: " + bodyJson);
    new SendReportAsyncTask(listener, bodyJson, addedSteps, todaySteps,
        currentDay).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
  }
}
