package com.sugarman.myb.services.fetching_animation;

import android.app.IntentService;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import java.io.File;
import timber.log.Timber;

/**
 * Created by nikita on 02.01.2018.
 */

public class FetchingAnimationService extends
    //Service
    IntentService implements IFetchingAnimationServiceView {
  private FetchingAnimationServicePresenter mPresenter;

  public FetchingAnimationService() {
    super("FetchingAnimationService");
    mPresenter = new FetchingAnimationServicePresenter();
    mPresenter.bind(this);
  }

  @Override public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
    mPresenter.getAnimations(new File(getFilesDir() + "/animations/"));
    return START_REDELIVER_INTENT;
  }

  @Override protected void onHandleIntent(@Nullable Intent intent) {
    Timber.e("onHandleIntent");
    //mPresenter.getAnimations(new File(getFilesDir() + "/animations/"));
  }

  @Override public void onDestroy() {
    super.onDestroy();
    mPresenter.unbind();
  }

  @Nullable @Override public IBinder onBind(Intent intent) {
    return null;
  }
}
