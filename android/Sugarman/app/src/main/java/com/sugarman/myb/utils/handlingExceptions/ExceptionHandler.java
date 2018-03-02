package com.sugarman.myb.utils.handlingExceptions;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import com.sugarman.myb.ui.activities.exceptionHidenActivity.SendExceptionHiddenActivity;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import timber.log.Timber;

/**
 * Created by nikita on 15.02.2018.
 */

public class ExceptionHandler implements java.lang.Thread.UncaughtExceptionHandler {
  public static final String ERROR_KEY = "error_key";
  public static final String LINE_SEPARATOR = "\n";
  private final Activity myContext;

  public ExceptionHandler(Activity context) {
    myContext = context;
  }

  public void uncaughtException(Thread thread, Throwable exception) {
    Timber.e("uncaughtException");
    StringWriter stackTrace = new StringWriter();
    exception.printStackTrace(new PrintWriter(stackTrace));

    HashMap<String, String> eventValue = new HashMap<>();
    eventValue.put("Stack trace", stackTrace.toString());
    eventValue.put("Brand", Build.BRAND);
    eventValue.put("Device", Build.DEVICE);
    eventValue.put("Model", Build.MODEL);
    eventValue.put("SDK", Build.VERSION.SDK);
    eventValue.put("Release", Build.VERSION.RELEASE);
    eventValue.put("Incremental", Build.VERSION.INCREMENTAL);

    Intent intent = new Intent(myContext, SendExceptionHiddenActivity.class);
    intent.putExtra(ERROR_KEY, eventValue);
    myContext.startActivity(intent);
    android.os.Process.killProcess(android.os.Process.myPid());
    System.exit(10);
  }
}
