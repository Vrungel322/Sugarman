package com.sugarman.myb.services.hourlySaveSteps;

import com.sugarman.myb.App;
import com.sugarman.myb.base.BasicPresenter;
import com.sugarman.myb.constants.Constants;
import com.sugarman.myb.utils.SharedPreferenceHelper;
import java.util.Map;
import java.util.SortedMap;
import java.util.concurrent.TimeUnit;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

/**
 * Created by nikita on 26.03.2018.
 */

public class HourlySaveStepsServicePresenter extends BasicPresenter<IHourlySaveStepsServiceView> {
  private IHourlySaveStepsServiceView mView;
  private Subscription mPeriodicalSubscription;
  private int steps;
  private int numOfHours = 0;

  @Override protected void inject() {
    App.getAppComponent().inject(this);
  }

  public void startHourlySaveSteps() {
    mPeriodicalSubscription = Observable.interval(1000, 10000, TimeUnit.MILLISECONDS)
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(aLong -> {
          steps = SharedPreferenceHelper.getReportStatsLocal(
              SharedPreferenceHelper.getUserId())[0].getStepsCount();
          if (numOfHours == 24) {
            numOfHours = 0;
            printDebug();
            clearLast24HoursSteps();
          }
          Timber.e("startHourlySaveSteps " + numOfHours);
          SharedPreferenceHelper.saveHourlySteps(numOfHours, steps);
          numOfHours++;
        }, Throwable::printStackTrace);
  }

  private void printDebug() {
    SortedMap<String, Integer> hashMap = SharedPreferenceHelper.getStepsPerDay();
    for (Map.Entry<String, Integer> entry : hashMap.entrySet()) {
      Timber.e("printDebug key: " + entry.getKey() + " value: " + entry.getValue());
    }
    Timber.e("printDebug _________________________________________________________________ ");
  }

  private void clearLast24HoursSteps() {
    Timber.e("clearLast24HoursSteps");
    for (int i = 0; i < 24; i++) {
      SharedPreferenceHelper.saveHourlySteps(i, Constants.FAKE_STEPS_COUNT);
    }
  }

  public void bind(IHourlySaveStepsServiceView iHourlySaveStepsServiceView) {
    mView = iHourlySaveStepsServiceView;
  }

  public void unBind() {
    if (mView != null) mView = null;
    if (mPeriodicalSubscription.isUnsubscribed()) mPeriodicalSubscription.unsubscribe();
  }
}
