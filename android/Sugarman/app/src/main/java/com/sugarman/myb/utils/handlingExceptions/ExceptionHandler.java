package com.sugarman.myb.utils.handlingExceptions;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import com.sugarman.myb.ui.activities.exceptionHidenActivity.SendExceptionHiddenActivity;
import java.io.PrintWriter;
import java.io.StringWriter;
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
    StringBuilder errorReport = new StringBuilder();
    errorReport.append("************ CAUSE OF ERROR ************\n\n");
    errorReport.append(stackTrace.toString());

    errorReport.append("\n************ DEVICE INFORMATION ***********\n");
    errorReport.append("Brand: ");
    errorReport.append(Build.BRAND);
    errorReport.append(LINE_SEPARATOR);
    errorReport.append("Device: ");
    errorReport.append(Build.DEVICE);
    errorReport.append(LINE_SEPARATOR);
    errorReport.append("Model: ");
    errorReport.append(Build.MODEL);
    errorReport.append(LINE_SEPARATOR);
    errorReport.append("Id: ");
    errorReport.append(Build.ID);
    errorReport.append(LINE_SEPARATOR);
    errorReport.append("Product: ");
    errorReport.append(Build.PRODUCT);
    errorReport.append(LINE_SEPARATOR);
    errorReport.append("\n************ FIRMWARE ************\n");
    errorReport.append("SDK: ");
    errorReport.append(Build.VERSION.SDK);
    errorReport.append(LINE_SEPARATOR);
    errorReport.append("Release: ");
    errorReport.append(Build.VERSION.RELEASE);
    errorReport.append(LINE_SEPARATOR);
    errorReport.append("Incremental: ");
    errorReport.append(Build.VERSION.INCREMENTAL);
    errorReport.append(LINE_SEPARATOR);

    Timber.e("uncaughtException "+ errorReport.toString());

    Intent intent = new Intent(myContext, SendExceptionHiddenActivity.class);
    intent.putExtra(ERROR_KEY, errorReport.toString());
    myContext.startActivity(intent);
    android.os.Process.killProcess(android.os.Process.myPid());
    System.exit(10);



  }
}
