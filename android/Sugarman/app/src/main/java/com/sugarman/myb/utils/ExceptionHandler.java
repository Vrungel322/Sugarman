package com.sugarman.myb.utils;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.Log;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.sugarman.myb.ui.activities.shop.ShopActivity;
import com.google.firebase.database.DatabaseReference;
import java.io.PrintWriter;
import java.io.StringWriter;
import timber.log.Timber;

/**
 * Created by yegoryeriomin on 2/14/18.
 */

public class ExceptionHandler implements java.lang.Thread.UncaughtExceptionHandler {
  private final Activity myContext;
  private final String LINE_SEPARATOR = "\n";
  private DatabaseReference databaseRefference;
  String key = "error";

  public ExceptionHandler(Activity context) {
    myContext = context;
  }

  public void uncaughtException(Thread thread, Throwable exception) {
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

    Log.e("SHFJKA", "zalupa tsvetochnaya " + errorReport.toString());
    System.out.print("zalupa tsvetochnaya " + errorReport.toString());
    databaseRefference = FirebaseDatabase.getInstance().getReference();
    databaseRefference.child("remoteLoggingAndroid")
        .child(key)
        .child(SharedPreferenceHelper.getUserName() + " : " + SharedPreferenceHelper.getUserId())
        .setValue(errorReport.toString()).addOnCompleteListener(task -> {

        });
    //Intent intent = new Intent(myContext, ShopActivity.class);
    //intent.putExtra("error", errorReport.toString());
    //myContext.startActivity(intent);
    //
    //try {
    //  throw new Exception();
    //} catch (Exception e) {
    //  e.printStackTrace();
    //}
    //
    //android.os.Process.killProcess(android.os.Process.myPid());
    //System.exit(10);
  }
}
