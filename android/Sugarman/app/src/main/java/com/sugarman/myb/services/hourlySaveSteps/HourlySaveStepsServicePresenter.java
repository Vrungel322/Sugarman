package com.sugarman.myb.services.hourlySaveSteps;

import com.sugarman.myb.App;
import com.sugarman.myb.base.BasicPresenter;
import com.sugarman.myb.constants.Constants;
import com.sugarman.myb.utils.SharedPreferenceHelper;
import java.util.Calendar;
import java.util.List;
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
    //printDebug();

    mPeriodicalSubscription = Observable.interval(1000, 3600000 /*2000*/, TimeUnit.MILLISECONDS)
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(aLong -> {
          steps = SharedPreferenceHelper.getReportStatsLocal(
              SharedPreferenceHelper.getUserId())[0].getStepsCount();
          Calendar calendar = Calendar.getInstance();
          calendar.setTimeInMillis(System.currentTimeMillis());
          int mYear = calendar.get(Calendar.YEAR);
          int mMonth = calendar.get(Calendar.MONTH);
          int mDay = calendar.get(Calendar.DAY_OF_MONTH);

          if (!SharedPreferenceHelper.getDateOfClearingDaysHours()
              .equals(mYear + "-" + mMonth + "-" + mDay)) {
            numOfHours = 0;
            printDebug();
            clearLast24HoursSteps();
          }
          Calendar rightNow = Calendar.getInstance();
          numOfHours = rightNow.get(Calendar.HOUR_OF_DAY);

          Timber.e("startHourlySaveSteps numOfHours " + numOfHours);
          Timber.e("startHourlySaveSteps steps" + steps);
          Timber.e("startHourlySaveSteps countSumOfStats" + countSumOfStats(
              SharedPreferenceHelper.getStepsPerDay()));
          Timber.e("startHourlySaveSteps difference" + (steps - countSumOfStats(
              SharedPreferenceHelper.getStepsPerDay())));

          SharedPreferenceHelper.saveHourlySteps(numOfHours,
              steps - countSumOfStats(SharedPreferenceHelper.getStepsPerDay()));

          //SharedPreferenceHelper.saveHourlySteps(numOfHours,steps );

        }, Throwable::printStackTrace);
  }

  private int countSumOfStats(List<Integer> stats) {
    int sum = 0;
    Timber.e("countSumOfStats stats size" + stats.size());
    for (int i : stats) {
      Timber.e("countSumOfStats IN FOR" + sum);
      sum += i;
    }
    return sum;
  }

  private void printDebug() {
    List<Integer> hashMap = SharedPreferenceHelper.getStepsPerDay();
    for (Integer i : hashMap) {
      Timber.e("printDebug key: " + i);
    }
    Timber.e("printDebug _________________________________________________________________ ");
  }

  private void clearLast24HoursSteps() {
    Timber.e("clearLast24HoursSteps");
    int index = 0;
    //while (BaseSharedPreferenceHelper.getPrefsInstance()
    //    .contains(SharedPreferenceConstants.HOUR_ + index)) {
    for (int i = 0; i < 24; i++) {
      SharedPreferenceHelper.saveHourlySteps(i, Constants.FAKE_STEPS_COUNT);
      SharedPreferenceHelper.saveDateOfClearingDaysHours();
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
