package com.sugarman.myb.services.hourlySaveSteps;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import com.sugarman.myb.utils.SharedPreferenceHelper;
import timber.log.Timber;

/**
 * Created by nikita on 26.03.2018.
 */

public class HourlySaveStepsService extends Service {
  private HourlySaveStepsServicePresenter mServicePresenter;

  public HourlySaveStepsService() {
    Timber.e("HourlySaveStepsService steps " + SharedPreferenceHelper.getReportStatsLocal(SharedPreferenceHelper.getUserId())[0]
        .getStepsCount());
    mServicePresenter = new HourlySaveStepsServicePresenter();
  }

  @Override public int onStartCommand(Intent intent, int flags, int startId) {
    Timber.e("onStartCommand");
    mServicePresenter.startHourlySaveSteps();
    return START_REDELIVER_INTENT;
  }

  @Override public void onDestroy() {
    super.onDestroy();
    Timber.e("onDestroy");
  }

  @Nullable @Override public IBinder onBind(Intent intent) {
    return null;
  }
}
