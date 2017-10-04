package com.sugarman.myb.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import com.sugarman.myb.App;
import com.sugarman.myb.eventbus.events.SwitchToNextDayEvent;

public class SwitchToNextDayReceiver extends BroadcastReceiver {

  private static final String TAG = SwitchToNextDayReceiver.class.getName();

  @Override public void onReceive(Context context, Intent intent) {
    PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
    PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
    wl.acquire();

    App.getEventBus().post(new SwitchToNextDayEvent());
    wl.release();
  }
}
