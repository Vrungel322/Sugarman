package com.sugarman.myb.services;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;
import com.instacart.library.truetime.TrueTime;
import com.sugarman.myb.App;
import com.sugarman.myb.R;
import com.sugarman.myb.api.clients.ReportStatsLightClient;
import com.sugarman.myb.api.models.requests.ReportStats;
import com.sugarman.myb.api.models.requests.StatsRequest;
import com.sugarman.myb.constants.Config;
import com.sugarman.myb.constants.Constants;
import com.sugarman.myb.eventbus.events.DebugRealStepAddedEvent;
import com.sugarman.myb.eventbus.events.DebugRefreshStepsEvent;
import com.sugarman.myb.eventbus.events.DebugRequestStepsEvent;
import com.sugarman.myb.eventbus.events.ReportStepsEvent;
import com.sugarman.myb.eventbus.events.StepServiceStartedEvent;
import com.sugarman.myb.eventbus.events.SwitchToNextDayEvent;
import com.sugarman.myb.eventbus.events.SwitchUIToNextDayEvent;
import com.sugarman.myb.listeners.OnReportSendListener;
import com.sugarman.myb.listeners.StepListener;
import com.sugarman.myb.receivers.SwitchToNextDayReceiver;
import com.sugarman.myb.ui.activities.splash.SplashActivity;
import com.sugarman.myb.utils.DeviceHelper;
import com.sugarman.myb.utils.SharedPreferenceHelper;
import com.sugarman.myb.utils.StringHelper;
import com.sugarman.myb.utils.stepcounter.CustomStepDetector;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import org.greenrobot.eventbus.Subscribe;

public class MasterStepDetectorService extends Service implements OnReportSendListener {

  private static final String TAG = MasterStepDetectorService.class.getName();

  private SensorManager mSensorManager;
  private Sensor mStepDetectorSensor;
  private Sensor mStepCounterSensor;
  private Sensor mAccelerometerSensor;
  private CustomStepDetector customStepDetector;

  private PowerManager.WakeLock wlSwitchToNextDay;

  private String userId;
  private ReportStats[] stats;
  private StatsRequest request;

  private ReportStatsLightClient statsClient;

  private long lastReportTimestamp = 0;
  private int lastSendSteps = 0;

  private int counterSteps = 0; ///steps stepsCounter
  private int initStepCounterSteps = 0;
  private int numStepsCustomDetector;

  private boolean isScreenOff = false;

  private Handler handler;

  private boolean isSwitchToNextDay = false;

  private final SensorEventListener mStepDetectorListener = new SensorEventListener() {

    @Override public void onSensorChanged(SensorEvent event) {
      int steps = event.values.length;
      if (steps > 0) {
        //                refreshNextDays();
        //Log.e("Step", "HUYHUYHUY");

        Log.d("###", "step detector: steps: " + steps);
        addAndSaveSteps(steps);
        sendReport(false);
        App.getEventBus().post(new DebugRealStepAddedEvent(stats[0].getStepsCount()));
      }
    }

    @Override public void onAccuracyChanged(Sensor sensor, int accuracy) {
      // nothing
    }
  };

  private final SensorEventListener mStepCounterListener = new SensorEventListener() {

    @Override public void onSensorChanged(SensorEvent event) {

      if (event.values != null && event.values.length > 0) {
        int steps = (int) event.values[0];

        //Log.d("MASTERSTEPDETECTOR",
        //    "Is first launch " + SharedPreferenceHelper.isFirstLaunchOfTheDay(userId));
        if (SharedPreferenceHelper.getOnLaunch() == true) {
          if (SharedPreferenceHelper.isFirstLaunch()) {
            SharedPreferenceHelper.setFirstLaunch(false);
          } else {
            if (!SharedPreferenceHelper.isFirstLaunchOfTheDay(userId)) {

              // Log.d("MASTERSTEPDETECTOR" , "Is first launch " + SharedPreferenceHelper.isFirstLaunchOfTheDay(userId));
              int delta = (int) event.values[0] - SharedPreferenceHelper.getBaseline();
              //Log.e("baseline", "called not first launch");
              //Log.d("baseline", "values " + event.values[0]);
              //Log.d("baseline", "baseline " + SharedPreferenceHelper.getBaseline());
              //Log.d("baseline", "delta " + delta);
              if (delta > 0) {
                addAndSaveSteps(delta);
                //Log.d("BASELINE ", "BASELINE ADDED " + delta + " steps");
                SharedPreferenceHelper.setOnLaunch(false);
              }

              SharedPreferenceHelper.setFirstLaunchOfTheDay(false);
            } else {
              //Log.e("baseline", "called first launch");
              SharedPreferenceHelper.saveBaseline(steps);
              SharedPreferenceHelper.setFirstLaunchOfTheDay(false);
            }
          }
        }

        if (initStepCounterSteps == 0 || !isScreenOff) {
          initStepCounterSteps = steps;
        } else {
          counterSteps += (steps - initStepCounterSteps);
          if (counterSteps < 0) {
            counterSteps = 0;
          } else {
            counterSteps = counterSteps;
          }
          initStepCounterSteps = steps;
        }

        DateFormat dfDate = new SimpleDateFormat("yyyy-MM-dd");
        String date = dfDate.format(Calendar.getInstance().getTime());
        SharedPreferenceHelper.setTodayDate(date);
        //Log.d("PEDOMETER IN SETTINGS", "" + SharedPreferenceHelper.getBaseline());
        //Log.e("USER ID", "LENGTH " + SharedPreferenceHelper.getUserId().length());
        //Log.d("baseline", "called save baseline step");
        SharedPreferenceHelper.saveBaseline(
            steps); // Baseline is set each step to provide info on app relaunching.
        // It will help covering the steps that were made during the time that the service wasn't working
        //Log.d("UPD PEDO IN SETTINGS", "" + SharedPreferenceHelper.getBaseline());

        //Log.d("PEDOMETER FROM SENSOR", "" + steps);

        //Log.d("###", "step counter: steps: " + steps + " counterSteps: " + counterSteps);
      }
    }

    @Override public void onAccuracyChanged(Sensor sensor, int accuracy) {
      // nothing
    }
  };

  private final SensorEventListener mCustomStepDetectorListener = new SensorEventListener() {
    @Override public void onSensorChanged(SensorEvent event) {
      if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
        customStepDetector.updateAccel(event.timestamp, event.values[0], event.values[1],
            event.values[2]);
      }
    }

    @Override public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
  };

  private final StepListener mCustomStepListener = new StepListener() {
    @Override public void step(long timeNs) {
      refreshNextDays();
      numStepsCustomDetector++;
      //Log.d(TAG, "custom detector steps " + numStepsCustomDetector);
      addAndSaveSteps(1);
      sendReport(false);
      App.getEventBus().post(new DebugRealStepAddedEvent(stats[0].getStepsCount()));
    }
  };

  private final BroadcastReceiver mScreenOffReceiver = new BroadcastReceiver() {
    @Override public void onReceive(Context context, Intent intent) {
      if (TextUtils.equals(intent.getAction(), Intent.ACTION_SCREEN_OFF)) {
        Log.d("@@@", "SCREEN_OFF");

        Handler handler = getHandler();
        handler.removeCallbacks(screenOn);
        handler.removeCallbacks(screenOff);

        counterSteps = 0;
        handler.postDelayed(screenOff, Config.SCREEN_OFF_RECEIVER_DELAY);
      }
    }
  };

  private final BroadcastReceiver mScreenOnReceiver = new BroadcastReceiver() {
    @Override public void onReceive(Context context, Intent intent) {
      if (TextUtils.equals(intent.getAction(), Intent.ACTION_SCREEN_ON)) {
        //Log.d("@@@", "SCREEN_ON");
        App.appendLog(Constants.TAG_TEST_GO_TO_NEXT_DAY, "screenOn receiver");
        addAndSaveSteps(counterSteps);
        sendReport(false);
        counterSteps = 0;

        App.getEventBus().post(new DebugRealStepAddedEvent(stats[0].getStepsCount()));
        Handler handler = getHandler();
        handler.removeCallbacks(screenOn);
        handler.removeCallbacks(screenOff);

        handler.postDelayed(screenOn, Config.SCREEN_OFF_RECEIVER_DELAY);
      }
    }
  };

  private final Runnable screenOn = new Runnable() {

    @Override public void run() {
      unregisterEventStepDetector();
      registerEventStepDetector();
      unregisterEventStepCounter();
      registerEventStepCounter();

      isScreenOff = false;
    }
  };

  private final Runnable screenOff = new Runnable() {

    @Override public void run() {
      unregisterEventStepDetector();
      unregisterEventStepCounter();
      registerEventStepCounter();

      isScreenOff = true;
    }
  };

  void initializeTrueTime() {
    new Thread(new Runnable() {
      @Override public void run() {
        try {
          TrueTime.build().initialize();
        } catch (IOException e) {
          initializeTrueTime();
          e.printStackTrace();
        }
      }
    }).start();
  }

  @Override public void onCreate() {
    super.onCreate();
    //initializeTrueTime();
    //      todayReportedSteps = SharedPreferenceHelper.getStatsTodaySteps(getUserId());
    //      debugStartValue = SharedPreferenceHelper.getStatsTodaySteps(getUserId());
    //     App.appendLog(Constants.TAG_TEST_GO_TO_NEXT_DAY, "service onCreate()  startValue " + debugStartValue + "  todayReportedSteps  " + todayReportedSteps);
    getReportStats();
    Log.d(TAG, "onCreate");

    getSensorManager();
    getStepDetectorSensor();
    getStepCounterSensor();
    getAccelerometerSensor();

    registerReceiver(mScreenOffReceiver, new IntentFilter(Intent.ACTION_SCREEN_OFF));
    registerReceiver(mScreenOnReceiver, new IntentFilter(Intent.ACTION_SCREEN_ON));

    SharedPreferenceHelper.saveIsNeedStartStepDetectorService(true);
  }

  @Override public int onStartCommand(Intent intent, int flags, int startId) {
    PowerManager.WakeLock wl = wakeLockAcquire(false);
    //Log.d(TAG, "onStartCommand intent " + intent + " flag " + flags);
    //   App.appendLog(Constants.TAG_TEST_GO_TO_NEXT_DAY, "service onStartCommand()  startValue " + debugStartValue);
    Context context = getApplicationContext();

    unregisterEventStepCounter();
    registerEventStepCounter();
    unregisterEventStepDetector();
    registerEventStepDetector();

    if (!DeviceHelper.isStepSensorSupported()) {
      registerCustomStepDetector();
    }

    registerReceiver(mScreenOffReceiver, new IntentFilter(Intent.ACTION_SCREEN_OFF));
    registerReceiver(mScreenOnReceiver, new IntentFilter(Intent.ACTION_SCREEN_ON));

    if (!App.getEventBus().isRegistered(this)) {
      App.getEventBus().register(this);
      App.getEventBus().post(new StepServiceStartedEvent());
    }

    if (intent != null) {
      String action = intent.getAction();
      if (!TextUtils.isEmpty(action)) {
        switch (action) {
          case Constants.COMMAND_START_STEP_DETECTOR_SERVICE:
            //no todayReportedSteps = SharedPreferenceHelper.getStatsTodaySteps(getUserId());
            startForegroundService();
            setSwitchToNextDayAlarm(context);
            break;
          case Constants.COMMAND_STOP_STEP_DETECTOR_SERVICE:
            SharedPreferenceHelper.saveReportStatsLocal(stats);
            stopForegroundService(startId);
            cancelSwitchToNextDayAlarm(context);
            unregisterEventStepDetector();
            unregisterEventStepCounter();
            unregisterCustomStepDetector();
            unregisterBusEvent();
            getHandler().removeCallbacks(screenOn);
            break;
          default:
            Log.e(TAG, "command not processed: " + action);
            break;
        }
      }
    } else {
      if (!App.getEventBus().isRegistered(this)) {
        App.getEventBus().register(this);
        App.getEventBus().post(new StepServiceStartedEvent());
      }
    }

    wakeLockRelease(wl);
    return START_STICKY;
  }

  @Override public void onDestroy() {
    super.onDestroy();
    App.appendLog(Constants.TAG_TEST_GO_TO_NEXT_DAY, "service onDestroy()  ");
    //Log.d(TAG, "destroy start ");
    PowerManager.WakeLock wl = wakeLockAcquire(false);

    final Context context = getApplicationContext();
    SharedPreferenceHelper.saveReportStatsLocal(stats);
    cancelSwitchToNextDayAlarm(context);
    unregisterEventStepDetector();
    unregisterEventStepCounter();
    unregisterCustomStepDetector();
    unregisterBusEvent();
    unregisterReceiver(mScreenOffReceiver);
    unregisterReceiver(mScreenOnReceiver);
    getHandler().removeCallbacks(screenOn);

    if (SharedPreferenceHelper.isNeedStartStepDetectorService()) {
      App.getHandlerInstance().postDelayed(new Runnable() {
        @Override public void run() {
          // not expected stop service
          Intent startIntent = new Intent(context, MasterStepDetectorService.class);
          startIntent.setAction(Constants.COMMAND_START_STEP_DETECTOR_SERVICE);
          startService(startIntent);
        }
      }, 3000);
    }
    Log.d(TAG, "destroy end ");
    wakeLockRelease(wl);
  }

  @Nullable @Override public IBinder onBind(Intent intent) {
    return null;
  }

  @Override public void onApiReportSendSuccess(int todaySteps, int addedSteps, int sendReportDay) {
    //Log.d("!!!", "added steps: " + addedSteps);
    //Log.d("!!!", "saved today: " + todaySteps);
    SharedPreferenceHelper.saveReportStatsLocal(stats);

    App.appendLog(Constants.TAG_TEST_GO_TO_NEXT_DAY, "service succed send report");
    if (isSwitchToNextDay) {
      refreshNextDays();
      App.getEventBus().post(new DebugRealStepAddedEvent(stats[0].getStepsCount()));
    }
  }

  @Override public void onApiReportSendFailure(int addedSteps) {
    App.appendLog(Constants.TAG_TEST_GO_TO_NEXT_DAY, "service failed send report");
    if (isSwitchToNextDay) {
      refreshNextDays();
      App.getEventBus().post(new DebugRealStepAddedEvent(stats[0].getStepsCount()));
    }
  }

  @Subscribe public void onEvent(SwitchToNextDayEvent event) {
    App.appendLog(Constants.TAG_TEST_GO_TO_NEXT_DAY, "switch to next day event service  ");
    wlSwitchToNextDay = wakeLockAcquire(true);
    isSwitchToNextDay = true;
    App.getHandlerInstance().postDelayed(new Runnable() {
      @Override public void run() {
        if (!isScreenOff) {
          sendReport(true);
          App.getEventBus().post(new DebugRealStepAddedEvent(stats[0].getStepsCount()));
        }
      }
    }, 3000);
  }

  @Subscribe public void onEvent(ReportStepsEvent event) {
    PowerManager.WakeLock wl = wakeLockAcquire(false);
    sendReport(true);
    wakeLockRelease(wl);
  }

  @Subscribe public void onEvent(DebugRequestStepsEvent event) {
    App.getEventBus().post(new DebugRefreshStepsEvent(stats[0].getStepsCount(), 0, 0));
  }

  private void setSwitchToNextDayAlarm(Context context) {
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

  private void cancelSwitchToNextDayAlarm(Context context) {
    Intent intent = new Intent(context, SwitchToNextDayReceiver.class);
    PendingIntent sender =
        PendingIntent.getBroadcast(context, Constants.SWITCH_TO_NEXT_DAY_REQUEST_CODE, intent, 0);
    AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    alarmManager.cancel(sender);
  }

  private void sendReport(boolean isForce) {
    //initializeTrueTime();
    if (statsClient == null) {
      statsClient = new ReportStatsLightClient();
    }

    if (stats == null || stats.length != Constants.REPORTED_DAYS) {
      stats = SharedPreferenceHelper.getReportStats(getUserId());
    }

    if (request == null) {
      request = new StatsRequest();
    }

    if (stats.length == 0) {
      stats = new ReportStats[1];
      ReportStats reportStats = new ReportStats();
      reportStats.setDate(StringHelper.getReportStepsDate(new Date()));
      stats[0] = reportStats;
    }

    handler = getHandler();
    if (DeviceHelper.canISend(lastReportTimestamp, System.currentTimeMillis(), lastSendSteps,
        stats[0].getStepsCount()) || isForce) {

      ReportStats[] lastTwoDatedStats = { stats[0], stats[1] };

      Log.d("???!!!!", "sent report today steps: " + stats[0].getStepsCount());
      request.setData(lastTwoDatedStats);
      App.appendLog(Constants.TAG_TEST_GO_TO_NEXT_DAY, "service sendReport() show stats:");
      for (int i = 0; i < stats.length; i++) {
        App.appendLog(Constants.TAG_TEST_GO_TO_NEXT_DAY,
            "stat  " + i + "   " + stats[i].toString());
      }
      App.appendLog(Constants.TAG_TEST_GO_TO_NEXT_DAY, "service sendReport() shot stats finish");

      //Calendar trueTime = Calendar.getInstance();
      //trueTime.setTime(TrueTime.now());
      //statsClient.sendStats(this, request, 0,0, trueTime.get(Calendar.DAY_OF_MONTH));

      statsClient.sendStats(this, request, 0, 0, Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
      //Log.e("true time", ""+trueTime.get(Calendar.DAY_OF_MONTH));

      lastSendSteps = stats[0].getStepsCount();
      lastReportTimestamp = System.currentTimeMillis();
      Log.d("%%%", "sendReport: added steps: 0");
    }
    // App.getEventBus().post(new DebugCacheStepsEvent(this.addedSteps));
  }

  private PowerManager.WakeLock wakeLockAcquire(boolean isFullWake) {
    PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
    PowerManager.WakeLock wl = pm.newWakeLock(isFullWake ? PowerManager.SCREEN_BRIGHT_WAKE_LOCK
        | PowerManager.FULL_WAKE_LOCK
        | PowerManager.ACQUIRE_CAUSES_WAKEUP : PowerManager.PARTIAL_WAKE_LOCK, TAG);
    wl.acquire();

    return wl;
  }

  private void startForegroundService() {
    Notification notification = getForegroundNotification();
    startForeground(Constants.STEP_DETECTOR_FOREGROUND_NOTIFICATION_ID, notification);
  }

  private void stopForegroundService(int startId) {
    SharedPreferenceHelper.saveIsNeedStartStepDetectorService(false);

    stopForeground(true);
    stopSelf(startId);
  }

  private Notification getForegroundNotification() {
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

  private void registerEventStepCounter() {
    getSensorManager().registerListener(mStepCounterListener, getStepCounterSensor(),
        SensorManager.SENSOR_DELAY_NORMAL, 500);
  }

  private void unregisterEventStepCounter() {
    getSensorManager().unregisterListener(mStepCounterListener, getStepCounterSensor());
  }

  private void registerEventStepDetector() {
    getSensorManager().registerListener(mStepDetectorListener, getStepDetectorSensor(),
        SensorManager.SENSOR_DELAY_NORMAL, 500);
  }

  private void unregisterEventStepDetector() {
    getSensorManager().unregisterListener(mStepDetectorListener, getStepDetectorSensor());
  }

  private void registerCustomStepDetector() {
    setCustomStepListener();
    getSensorManager().registerListener(mCustomStepDetectorListener, getAccelerometerSensor(),
        SensorManager.SENSOR_DELAY_FASTEST);
  }

  private void unregisterCustomStepDetector() {
    removeCustomStepListener();
    getSensorManager().unregisterListener(mCustomStepDetectorListener, getAccelerometerSensor());
  }

  private void setCustomStepListener() {
    getCustomStepDetector().registerListener(mCustomStepListener);
  }

  private void removeCustomStepListener() {
    getCustomStepDetector().unregisterListener();
  }

  private CustomStepDetector getCustomStepDetector() {
    if (customStepDetector == null) {
      customStepDetector = new CustomStepDetector();
    }
    return customStepDetector;
  }

  private void unregisterBusEvent() {
    try {
      App.getEventBus().unregister(this);
    } catch (IllegalStateException e) {
      Log.e(TAG, "failure unregister event bus", e);
    }
  }

  private void wakeLockRelease(PowerManager.WakeLock wakeLock) {
    if (wakeLock != null && wakeLock.isHeld()) {
      wakeLock.release();
    }
  }

  private SensorManager getSensorManager() {
    if (mSensorManager == null) {
      mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
    }

    return mSensorManager;
  }

  private Sensor getStepDetectorSensor() {
    if (mStepDetectorSensor == null) {
      mStepDetectorSensor = getSensorManager().getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
    }

    return mStepDetectorSensor;
  }

  private Sensor getStepCounterSensor() {
    if (mStepCounterSensor == null) {
      mStepCounterSensor = getSensorManager().getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
    }

    return mStepCounterSensor;
  }

  private Sensor getAccelerometerSensor() {
    if (mAccelerometerSensor == null) {
      mAccelerometerSensor = getSensorManager().getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }
    return mAccelerometerSensor;
  }

  private Handler getHandler() {
    if (handler == null) {
      handler = App.getHandlerInstance();
    }

    return handler;
  }

  private void addAndSaveSteps(int steps) {
    stats[0].setStepsCount(stats[0].getStepsCount() + steps);
    SharedPreferenceHelper.saveReportStatsLocal(stats);
  }

  private void getReportStats() {
    stats = SharedPreferenceHelper.getReportStatsLocal(getUserId());
    Log.e("stats length masterstep", "" + stats + " " + stats.length);
    Log.e("stats ", "" + SharedPreferenceHelper.getReportStats(getUserId())[0]);
    if (stats == null || stats.length == 0) {
      SharedPreferenceHelper.saveReportStatsLocal(
          SharedPreferenceHelper.getReportStats(getUserId()));
      stats = SharedPreferenceHelper.getReportStatsLocal(getUserId());
    }
    Log.e("stats ", "" + SharedPreferenceHelper.getReportStats(getUserId())[0]);
    if (stats != null) {
      //            if (DeviceHelper.isNeedGoToNextDay(stats[0])) {
      //                for (int i = 0; i < DeviceHelper.numbersOffsetDays(stats[0]); i++) {
      //                    App.appendLog(Constants.TAG_TEST_GO_TO_NEXT_DAY, "refreshNextDays() move array");
      //                    SharedPreferenceHelper.shiftStatsLocalOnOneDay(getUserId());
      //                }

      //            }
      stats = SharedPreferenceHelper.getReportStatsLocal(getUserId());
      App.getEventBus().post(new DebugRealStepAddedEvent(stats[0].getStepsCount()));
    }
  }

  private void refreshNextDays() {
    App.appendLog(Constants.TAG_TEST_GO_TO_NEXT_DAY, "refreshNextDays() start");
    if (DeviceHelper.isNeedGoToNextDay(stats[0])) {
      Log.d("%%%", "refreshNextDays addedSteps: 0");
      for (int i = 0; i < DeviceHelper.numbersOffsetDays(stats[0]); i++) {
        App.appendLog(Constants.TAG_TEST_GO_TO_NEXT_DAY, "refreshNextDays() move array");
        SharedPreferenceHelper.shiftStatsLocalOnOneDay(getUserId());
      }
      stats = SharedPreferenceHelper.getReportStatsLocal(getUserId());
      request.setData(stats);
      App.appendLog(Constants.TAG_TEST_GO_TO_NEXT_DAY, "refreshNextDays() get stats:");
      for (int i = 0; i < stats.length; i++) {
        App.appendLog(Constants.TAG_TEST_GO_TO_NEXT_DAY,
            "refreshNextDays()  " + i + "   " + stats[i].toString());
      }
      App.appendLog(Constants.TAG_TEST_GO_TO_NEXT_DAY, "refreshNextDays() get stats finish");
      SharedPreferenceHelper.saveReportStatsLocal(stats);
      //SharedPreferenceHelper.saveShowedSteps(todayReportedSteps);

      unregisterEventStepDetector();
      registerEventStepDetector();

      isSwitchToNextDay = false;

      App.getEventBus().post(new DebugRefreshStepsEvent(stats[0].getStepsCount(), 0, 0));
      App.getEventBus().post(new SwitchUIToNextDayEvent());

      if (wlSwitchToNextDay != null) {
        wakeLockRelease(wlSwitchToNextDay);
        wlSwitchToNextDay = null;
      }
    }
    App.appendLog(Constants.TAG_TEST_GO_TO_NEXT_DAY, "refreshNextDays() finish ");
  }

  private String getUserId() {
    if (TextUtils.isEmpty(userId)) {
      userId = SharedPreferenceHelper.getUserId();
    }

    return userId;
  }
}