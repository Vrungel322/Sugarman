package com.sugarman.myb.services;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;
import com.sugarman.myb.App;
import com.sugarman.myb.R;
import com.sugarman.myb.api.clients.ReportStatsLightClient;
import com.sugarman.myb.api.models.requests.ReportStats;
import com.sugarman.myb.api.models.requests.StatsRequest;
import com.sugarman.myb.constants.Config;
import com.sugarman.myb.constants.Constants;
import com.sugarman.myb.eventbus.events.DebugCacheStepsEvent;
import com.sugarman.myb.eventbus.events.DebugRefreshStepsEvent;
import com.sugarman.myb.eventbus.events.DebugRequestStepsEvent;
import com.sugarman.myb.eventbus.events.ReportStepsEvent;
import com.sugarman.myb.eventbus.events.SwitchToNextDayEvent;
import com.sugarman.myb.eventbus.events.SwitchUIToNextDayEvent;
import com.sugarman.myb.listeners.OnReportSendListener;
import com.sugarman.myb.receivers.SwitchToNextDayReceiver;
import com.sugarman.myb.ui.activities.SplashActivity;
import com.sugarman.myb.utils.SharedPreferenceHelper;
import com.sugarman.myb.utils.StringHelper;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import org.greenrobot.eventbus.Subscribe;
import timber.log.Timber;

public class BaseStepDetectorService extends Service implements OnReportSendListener {

  private final String TAG = BaseStepDetectorService.class.getName();

  protected PowerManager.WakeLock wlSwitchToNextDay;

  protected int todayReportedSteps;
  protected String userId;
  protected ReportStats[] stats;
  protected StatsRequest request;

  protected ReportStatsLightClient statsClient;

  protected long lastReportTimestamp = 0;
  protected int addedSteps = 0;
  protected int debugCalculated = 0;
  protected int debugStartValue = 0;

  protected int counterSteps = 0;

  protected Handler handler;

  protected boolean isScreenOff = false;
  protected boolean isSwitchToNextDay = false;

  protected final Runnable sendCacheSteps = new Runnable() {
    @Override public void run() {
      if (addedSteps > 0) {
        int totalSteps = todayReportedSteps + addedSteps;
        Log.d("???", "sent report (cache): " + totalSteps);

        stats[0].setStepsCount(totalSteps);
        if (request == null) {
          request = new StatsRequest();
        }
        request.setData(stats);

        //  statsClient.sendStats(BaseStepDetectorService.this, request, addedSteps, totalSteps);
        lastReportTimestamp = System.currentTimeMillis();
        addedSteps = 0;
        Log.d("%%%", "sendCacheSteps: addedSteps: 0");

        App.getEventBus().post(new DebugCacheStepsEvent(addedSteps));
      }
    }
  };

  @Nullable @Override public IBinder onBind(Intent intent) {
    return null;
  }

  @Override public void onApiReportSendSuccess(int todaySteps, int steps, int sendReportDay) {
    Log.d("!!!", "added steps: " + addedSteps);
    Log.d("!!!", "saved today: " + todaySteps);
    SharedPreferenceHelper.saveStatsTodaySteps(getUserId(), todaySteps);
    this.todayReportedSteps = todaySteps;
    Log.d("????", "reported today: " + todayReportedSteps);
    App.appendLog(Constants.TAG_TEST_GO_TO_NEXT_DAY, "service succed send report");
    if (isSwitchToNextDay) {
      refreshNextDays();
    }
  }

  @Override public void onApiReportSendFailure(int addedSteps) {
    Log.d("%%%", "onApiReportSendFailure: failure steps: "
        + addedSteps
        + " current addedSteps: "
        + this.addedSteps);
    this.addedSteps += addedSteps;
    App.getEventBus().post(new DebugCacheStepsEvent(this.addedSteps));
    lastReportTimestamp -= Config.TIMEOUT_BETWEEN_STEP_REPORT;
    App.appendLog(Constants.TAG_TEST_GO_TO_NEXT_DAY, "service failed send report");
    if (isSwitchToNextDay) {
      refreshNextDays();
    }
  }

  @Subscribe public void onEvent(SwitchToNextDayEvent event) {
    App.appendLog(Constants.TAG_TEST_GO_TO_NEXT_DAY, "switch to next day event service  ");
    wlSwitchToNextDay = wakeLockAcquire(true);
    isSwitchToNextDay = true;

    App.getHandlerInstance().postDelayed(new Runnable() {
      @Override public void run() {
        if (!isScreenOff) {
          sendReport(0, true);
        }
      }
    }, 3000);
  }

  @Subscribe public void onEvent(ReportStepsEvent event) {
    PowerManager.WakeLock wl = wakeLockAcquire(false);
    sendReport(0, true);
    wakeLockRelease(wl);
  }

  @Subscribe public void onEvent(DebugRequestStepsEvent event) {
    //debugCalculated = 0;

    App.appendLog(Constants.TAG_TEST_GO_TO_NEXT_DAY,
        "Service DebugRequestStepsEvent before startValue " + debugStartValue);
    /*    if (debugStartValue == 0) {
            debugStartValue = event.getStartValue();
        }*/
    App.appendLog(Constants.TAG_TEST_GO_TO_NEXT_DAY,
        "Service DebugRequestStepsEvent after startValue " + debugStartValue);
    App.getEventBus()
        .post(new DebugRefreshStepsEvent(debugCalculated, addedSteps, debugStartValue));
  }

  protected void startForegroundService() {
    Notification notification = getForegroundNotification();
    startForeground(Constants.STEP_DETECTOR_FOREGROUND_NOTIFICATION_ID, notification);
  }

  protected void sendReport(int addedSteps, boolean isForce) {
    if (statsClient == null) {
      statsClient = new ReportStatsLightClient();
    }

    if (stats == null || stats.length != Constants.REPORTED_DAYS) {
      stats = SharedPreferenceHelper.getReportStats(getUserId());
    }

    if (request == null) {
      request = new StatsRequest();
    }

    Log.d("%%%", "sendReport: current: " + this.addedSteps + " addedSteps: " + addedSteps);
    this.addedSteps += addedSteps;

    if (stats.length == 0) {
      stats = new ReportStats[1];
      ReportStats reportStats = new ReportStats();
      reportStats.setDate(StringHelper.getReportStepsDate(new Date()));
      stats[0] = reportStats;
    }

    handler = getHandler();
    if (lastReportTimestamp + Config.TIMEOUT_BETWEEN_STEP_REPORT <= System.currentTimeMillis()
        || isForce) {
      handler.removeCallbacks(sendCacheSteps);

      int totalSteps = todayReportedSteps + this.addedSteps;
      Log.d("???", "sent report: " + totalSteps);

      stats[0].setStepsCount(totalSteps);
      request.setData(stats);
      App.appendLog(Constants.TAG_TEST_GO_TO_NEXT_DAY, "service sendReport() show stats:");
      for (int i = 0; i < stats.length; i++) {
        App.appendLog(Constants.TAG_TEST_GO_TO_NEXT_DAY,
            "stat  " + i + "   " + stats[i].toString());
      }
      App.appendLog(Constants.TAG_TEST_GO_TO_NEXT_DAY, "service sendReport() shot stats finish");

      //try {
      //    TrueTime.build().initialize();
      //} catch (IOException e) {
      //    e.printStackTrace();
      //}
      //Calendar trueTime = Calendar.getInstance();
      //trueTime.setTime(TrueTime.now());

      //statsClient.sendStats(this, request, this.addedSteps, totalSteps, trueTime.get(Calendar.DAY_OF_MONTH));
      statsClient.sendStats(this, request, this.addedSteps, totalSteps,
          Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
      lastReportTimestamp = System.currentTimeMillis();
      this.addedSteps = 0;
      Log.d("%%%", "sendReport: added steps: 0");
    }

    if (this.addedSteps > 0) {
      handler.removeCallbacks(sendCacheSteps);
      handler.postDelayed(sendCacheSteps, Config.TIMEOUT_BETWEEN_STEP_REPORT);
    }

    Log.d("???", "not sent steps: " + this.addedSteps);
    App.getEventBus().post(new DebugCacheStepsEvent(this.addedSteps));
  }

  protected void refreshNextDays() {
    App.appendLog(Constants.TAG_TEST_GO_TO_NEXT_DAY, "refreshNextDays() start");
    if (isNeedGoToNextDay()) {
      Log.d("%%%", "refreshNextDays addedSteps: 0");
      todayReportedSteps = 0;
      addedSteps = 0;
      debugCalculated = 0;
      debugStartValue = 0;
      // TO DO shift date if shift more than one day
      for (int i = 0; i < offsetDays(); i++) {
        App.appendLog(Constants.TAG_TEST_GO_TO_NEXT_DAY, "refreshNextDays() move array");
        SharedPreferenceHelper.shiftStatsOnOneDay(getUserId());
      }
      stats = SharedPreferenceHelper.getReportStats(getUserId());
      request.setData(stats);
      App.appendLog(Constants.TAG_TEST_GO_TO_NEXT_DAY, "refreshNextDays() get stats:");
      for (int i = 0; i < stats.length; i++) {
        App.appendLog(Constants.TAG_TEST_GO_TO_NEXT_DAY,
            "refreshNextDays()  " + i + "   " + stats[i].toString());
      }
      App.appendLog(Constants.TAG_TEST_GO_TO_NEXT_DAY, "refreshNextDays() get stats finish");
      SharedPreferenceHelper.saveUserTodaySteps(todayReportedSteps);
      Timber.e("Save Showed Steps " + todayReportedSteps);
      SharedPreferenceHelper.saveShowedSteps(todayReportedSteps);

      isSwitchToNextDay = false;

      App.getEventBus()
          .post(new DebugRefreshStepsEvent(debugCalculated, addedSteps, debugStartValue));
      App.getEventBus().post(new SwitchUIToNextDayEvent());

      if (wlSwitchToNextDay != null) {
        wakeLockRelease(wlSwitchToNextDay);
        wlSwitchToNextDay = null;
      }
    }
    App.appendLog(Constants.TAG_TEST_GO_TO_NEXT_DAY, "refreshNextDays() finish ");
  }

  private boolean isNeedGoToNextDay() {
    Calendar calendar = Calendar.getInstance();
    int currentDay = calendar.get(Calendar.DAY_OF_MONTH);
    long currentTimeMillisec = calendar.getTimeInMillis();
    App.appendLog(Constants.TAG_TEST_GO_TO_NEXT_DAY,
        "isNeedGoToNextDay() currentDate:" + calendar.get(Calendar.YEAR) + "/" + calendar.get(
            Calendar.MONTH) + "/" + calendar.get(Calendar.DAY_OF_MONTH));
    calendar.setTime(StringHelper.convertReportStatDate(stats[0].getDate()));
    App.appendLog(Constants.TAG_TEST_GO_TO_NEXT_DAY,
        "isNeedGoToNextDay() lastStatsDate:" + calendar.get(Calendar.YEAR) + "/" + calendar.get(
            Calendar.MONTH) + "/" + calendar.get(Calendar.DAY_OF_MONTH));
    int lastStatsDay = calendar.get(Calendar.DAY_OF_MONTH);
    long statsTimeInMillis = calendar.getTimeInMillis();

    return currentDay > lastStatsDay || difMoreThanOneDay(statsTimeInMillis, currentTimeMillisec);
  }

  private boolean difMoreThanOneDay(long statsTime, long currentTime) {
    return currentTime - statsTime > 24 * 60 * 60 * 1000;
  }

  protected long offsetDays() {
    Calendar calendar = Calendar.getInstance();
    long currentTimeMillisec = calendar.getTimeInMillis();
    calendar.setTime(StringHelper.convertReportStatDate(stats[0].getDate()));
    long statsTimeInMillis = calendar.getTimeInMillis();
    return (currentTimeMillisec - statsTimeInMillis) / (24 * 60 * 60 * 1000);
  }

  protected String getUserId() {
    if (TextUtils.isEmpty(userId)) {
      userId = SharedPreferenceHelper.getUserId();
    }

    return userId;
  }

  protected Handler getHandler() {
    if (handler == null) {
      handler = App.getHandlerInstance();
    }

    return handler;
  }

  protected void stopForegroundService(int startId) {
    SharedPreferenceHelper.saveIsNeedStartStepDetectorService(false);

    stopForeground(true);
    stopSelf(startId);
  }

  protected Notification getForegroundNotification() {
    Bitmap appIcon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
    Bitmap largeIcon = Bitmap.createScaledBitmap(appIcon, Constants.NOTIFICATION_LARGE_ICON_SIZE_PX,
        Constants.NOTIFICATION_LARGE_ICON_SIZE_PX, false);

    String text = getString(R.string.move_your_butt);

    Intent intent = new Intent(this, SplashActivity.class);
    intent.putExtra(Constants.INTENT_OPEN_ACTIVITY, Constants.OPEN_MAIN_ACTIVITY);
    PendingIntent pendingIntent =
        PendingIntent.getActivity(this, Constants.STEP_DETECTOR_CODE, intent,
            PendingIntent.FLAG_UPDATE_CURRENT);

    Notification notification =
        new NotificationCompat.Builder(this).setContentTitle(getString(R.string.app_name))
            .setContentText(text)
            .setSmallIcon(R.drawable.ic_run_white)
            .setLargeIcon(largeIcon)
            .setContentIntent(pendingIntent)
            .setAutoCancel(false)
            .setOngoing(true)
            .build();
    notification.flags |= Notification.FLAG_FOREGROUND_SERVICE;

    return notification;
  }

  protected PowerManager.WakeLock wakeLockAcquire(boolean isFullWake) {
    PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
    PowerManager.WakeLock wl = pm.newWakeLock(isFullWake ? PowerManager.SCREEN_BRIGHT_WAKE_LOCK
        | PowerManager.FULL_WAKE_LOCK
        | PowerManager.ACQUIRE_CAUSES_WAKEUP : PowerManager.PARTIAL_WAKE_LOCK, TAG);
    wl.acquire();

    return wl;
  }

  protected void wakeLockRelease(PowerManager.WakeLock wakeLock) {
    if (wakeLock != null && wakeLock.isHeld()) {
      wakeLock.release();
    }
  }

  protected void setSwitchToNextDayAlarm(Context context) {
    cancelSwitchToNextDayAlarm(context);

    AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    Intent intent = new Intent(context, SwitchToNextDayReceiver.class);
    PendingIntent pi =
        PendingIntent.getBroadcast(context, Constants.SWITCH_TO_NEXT_DAY_REQUEST_CODE, intent, 0);

    Calendar calendar = GregorianCalendar.getInstance();
    calendar.set(Calendar.HOUR_OF_DAY, 23);
    calendar.set(Calendar.MINUTE, 59);
    calendar.set(Calendar.SECOND, 59);
    calendar.set(Calendar.MILLISECOND, 999);

    long startUpTime = calendar.getTimeInMillis();
    if (System.currentTimeMillis() > startUpTime) {
      startUpTime = startUpTime + Constants.MS_IN_DAY;
    }

    am.setInexactRepeating(AlarmManager.RTC_WAKEUP, startUpTime, AlarmManager.INTERVAL_DAY, pi);
  }

  protected void unregisterBusEvent() {
    try {
      App.getEventBus().unregister(this);
    } catch (IllegalStateException e) {
      Log.e(TAG, "failure unregister event bus", e);
    }
  }

  protected void cancelSwitchToNextDayAlarm(Context context) {
    Intent intent = new Intent(context, SwitchToNextDayReceiver.class);
    PendingIntent sender =
        PendingIntent.getBroadcast(context, Constants.SWITCH_TO_NEXT_DAY_REQUEST_CODE, intent, 0);
    AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    alarmManager.cancel(sender);
  }
}
