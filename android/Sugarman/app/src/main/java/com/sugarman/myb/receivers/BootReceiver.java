package com.sugarman.myb.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import com.sugarman.myb.constants.Constants;
import com.sugarman.myb.services.MasterStepDetectorService;
import com.sugarman.myb.utils.SharedPreferenceHelper;

public class BootReceiver extends BroadcastReceiver {

  @Override public void onReceive(Context context, Intent intent) {
    String action = intent.getAction();
    if (!TextUtils.isEmpty(action) && (TextUtils.equals(action, Intent.ACTION_BOOT_COMPLETED)
        || TextUtils.equals(action, Constants.QUICK_BOOT_ACTION))) {

      if (SharedPreferenceHelper.isNeedStartStepDetectorService()) {
        startStepDetectorService(context);
      }
    }
  }

  private void startStepDetectorService(Context context) {
    Intent startIntent = new Intent(context, MasterStepDetectorService.class);
    startIntent.setAction(Constants.COMMAND_START_STEP_DETECTOR_SERVICE);
    context.startService(startIntent);
  }
}