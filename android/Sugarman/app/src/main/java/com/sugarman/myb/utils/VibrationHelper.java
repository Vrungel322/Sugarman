package com.sugarman.myb.utils;

import android.content.Context;
import android.os.Vibrator;

/**
 * Created by nikita on 12.12.2017.
 */

public class VibrationHelper {
  public static void simpleVibration(Context context, long duration) {
    Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
    if (vibrator != null) {
      vibrator.vibrate(duration);
    }
  }
}
