package com.sugarman.myb.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Parcelable;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import com.sugarman.myb.App;
import com.sugarman.myb.api.models.requests.ReportStats;
import com.sugarman.myb.constants.Config;
import com.sugarman.myb.constants.Constants;
import com.sugarman.myb.services.StepDetectorService;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;

public abstract class DeviceHelper {

  private static final String TAG = DeviceHelper.class.getName();

  public static int CURRENT_TYPE_CONNECT = -1;
  public static final int TYPE_CONNECTED = 1;
  public static final int TYPE_CONNECTED_WIFI = 2;
  public static final int TYPE_CONNECTED_MOBILE = 3;
  public static final int TYPE_NOT_CONNECTED = 0;

  private static final int MILLISECONDS_IN_DAYS = 86400000;

  private DeviceHelper() {
    // only static methods and fields
  }

  public static void hideKeyboard(final View view) {
    if (view != null) {
      InputMethodManager imm =
          (InputMethodManager) App.getInstance().getSystemService(Context.INPUT_METHOD_SERVICE);
      imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
  }

  public static void hideKeyboard(Activity activity) {
    View view = activity.getCurrentFocus();
    if (view != null) {
      InputMethodManager imm =
          (InputMethodManager) App.getInstance().getSystemService(Context.INPUT_METHOD_SERVICE);
      imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
  }

  public static long howManyMillisecondsToMidnight() {
    Calendar c = Calendar.getInstance();
    c.add(Calendar.DAY_OF_MONTH, 1);
    c.set(Calendar.HOUR_OF_DAY, 0);
    c.set(Calendar.MINUTE, 0);
    c.set(Calendar.SECOND, 0);
    c.set(Calendar.MILLISECOND, 0);
    long howMany = (c.getTimeInMillis() - System.currentTimeMillis());

    return howMany;
  }

  public static boolean isNetworkConnected() {
    return getConnectivityStatus() == TYPE_CONNECTED;
  }

  private static int getConnectivityStatus() {
    return getConnectivityType() == TYPE_NOT_CONNECTED ? TYPE_NOT_CONNECTED : TYPE_CONNECTED;
  }

  public static int getConnectivityType() {
    Context context = App.getInstance();
    ConnectivityManager cm =
        (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

    NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
    if (null != activeNetwork) {
      int activeNetworkType = activeNetwork.getType();
      switch (activeNetworkType) {
        case ConnectivityManager.TYPE_WIFI:
          return TYPE_CONNECTED_WIFI;
        case ConnectivityManager.TYPE_MOBILE:
          return TYPE_CONNECTED_MOBILE;
        default:
          Log.d(TAG, "unknown network type: " + activeNetworkType);
          return TYPE_NOT_CONNECTED;
      }
    }
    return TYPE_NOT_CONNECTED;
  }

  public static void vibrate() {
    Vibrator v = (Vibrator) App.getInstance().getSystemService(Context.VIBRATOR_SERVICE);
    v.vibrate(Config.VIBRATION_TIME);
  }

  public static String getAppVersionName() {
    Context context = App.getInstance();
    PackageInfo pInfo = null;
    try {
      pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
    } catch (PackageManager.NameNotFoundException e) {
      Log.e(TAG, "failed get app version name", e);
    }
    if (pInfo != null) {
      return pInfo.versionName;
    } else {
      return null;
    }
  }

  public static void revokeWritePermission(Uri uri) {
    App.getInstance()
        .revokeUriPermission(uri,
            Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
  }

  public static Intent getGalleryIntent() {
    Intent galleryIntent =
        new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
      galleryIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
    }

    return galleryIntent;
  }

  public static Pair<String[], Parcelable[]> getCameraData() {
    Context context = App.getInstance();
    Pair<String, Uri> outputFileUri = getPictureUri();
    String uri = outputFileUri.second == null ? "" : outputFileUri.second.toString();

    Parcelable[] intents = null;
    if (outputFileUri.second != null) {
      List<Intent> cameraIntents = new ArrayList<>();
      Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
      PackageManager packageManager = context.getPackageManager();
      List<ResolveInfo> camList = packageManager.queryIntentActivities(captureIntent, 0);
      for (ResolveInfo res : camList) {
        final String packageName = res.activityInfo.packageName;
        final Intent intent = new Intent(captureIntent);
        intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
        intent.setPackage(packageName);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri.second);
        grantWritePermission(intent, outputFileUri.second);
        cameraIntents.add(intent);
      }

      intents = cameraIntents.toArray(new Parcelable[cameraIntents.size()]);
    }

    return new Pair<>(new String[] { outputFileUri.first, uri }, intents);
  }

  private static void grantWritePermission(Intent intent, Uri uri) {
    Context context = App.getInstance();
    List<ResolveInfo> resInfoList = context.getPackageManager()
        .queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
    for (ResolveInfo resolveInfo : resInfoList) {
      String packageName = resolveInfo.activityInfo.packageName;
      context.grantUriPermission(packageName, uri,
          Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
    }
  }

  private static Pair<String, Uri> getPictureUri() {
    File dir = new File(App.getInstance().getCacheDir(), Config.APP_FOLDER_NAME);
    if (!dir.exists()) {
      dir.mkdirs();
    }

    String filePath = "";
    Uri uri = null;
    try {
      File imagePath = File.createTempFile(UUID.randomUUID().toString(), ".jpg", dir);

      filePath = imagePath.getPath();
      String packageName = App.getInstance().getPackageName();
      String authority = packageName + ".myb.fileprovider";

      uri = FileProvider.getUriForFile(App.getInstance(), authority, imagePath);
    } catch (IOException ex) {
      Log.e(TAG, "failure create temp file: " + dir.toString(), ex);
    }

    return new Pair<>(filePath, uri);
  }

  public static boolean isStepSensorSupported() {
    int currentApiVersion = android.os.Build.VERSION.SDK_INT;
    // Check that the device supports the step counter and detector sensors
    PackageManager packageManager = App.getInstance().getPackageManager();
    return currentApiVersion >= android.os.Build.VERSION_CODES.KITKAT
        && packageManager.hasSystemFeature(PackageManager.FEATURE_SENSOR_STEP_COUNTER)
        && packageManager.hasSystemFeature(PackageManager.FEATURE_SENSOR_STEP_DETECTOR);
  }

  public static Class<?> getServiceName() {
    if (isStepSensorSupported()) {
      return StepDetectorService.class;
      // return StepDetectorServiceSensor.class;
    } else {
      return StepDetectorService.class;
      //return StepDetectorServiceAccelerometer.class;
    }
  }

  public static boolean canISend(long lastReportTime, long currentTime, int lastReportSteps,
      int currentSteps) {
    if (App.getInstance().isAppForeground() && (currentTime - lastReportTime
        >= Config.TIMEOUT_BETWEEN_STEP_REPORT_FOREGROUND || currentSteps - lastReportSteps >= 60)) {
      return true;
    } else if (currentTime - lastReportTime >= Config.TIMEOUT_BETWEEN_STEP_REPORT
        && !App.getInstance().isAppForeground()) {
      return true;
    } else {
      return false;
    }
  }

  public static File pickedExistingPicture(Uri uri) {
    Context context = App.getInstance();
    File file = null;
    try {
      InputStream pictureInputStream = context.getContentResolver().openInputStream(uri);
      File directory = new File(Config.APP_FOLDER);
      if (!directory.exists()) {
        directory.mkdirs();
      }

      file = new File(directory, UUID.randomUUID().toString() + "." + getMimeType(uri));
      file.createNewFile();
      writeToFile(pictureInputStream, file);
    } catch (IOException ex) {
      Log.e(TAG, "failure picked picture by uri: " + uri, ex);
    }
    return file;
  }

  private static String getMimeType(Uri uri) {
    Context context = App.getInstance();
    String extension;

    if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
      //If scheme is a content
      final MimeTypeMap mime = MimeTypeMap.getSingleton();
      extension = mime.getExtensionFromMimeType(context.getContentResolver().getType(uri));
    } else {
      //If scheme is a File
      //This will replace white spaces with %20 and also other special characters. This will avoid returning null values on file name with spaces and special characters.
      extension =
          MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(new File(uri.getPath())).toString());
    }

    return extension;
  }

  private static void writeToFile(InputStream in, File file) {
    try {
      OutputStream out = new FileOutputStream(file);
      byte[] buf = new byte[1024];
      int len;
      while ((len = in.read(buf)) > 0) {
        out.write(buf, 0, len);
      }
      out.close();
      in.close();
    } catch (IOException e) {
      Log.e(TAG, "failure write to file: ", e);
    }
  }

  public static String getRealPathFromUri(Uri contentUri) {
    Context context = App.getInstance();
    Cursor cursor = null;
    try {
      String[] proj = { MediaStore.Images.Media.DATA };
      cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
      if (cursor != null) {
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
      }
      return "";
    } finally {
      if (cursor != null) {
        cursor.close();
      }
    }
  }

  public static Bitmap takeScreenshot(Activity activity) {
    Bitmap bitmap = null;
    try {
      // create bitmap screen capture
      View rootView = activity.getWindow().getDecorView().getRootView();
      rootView.setDrawingCacheEnabled(true);
      bitmap = Bitmap.createBitmap(rootView.getDrawingCache());
      rootView.setDrawingCacheEnabled(false);
    } catch (Throwable e) {
      Log.e(TAG, "failure take screenshot", e);
    }

    return bitmap;
  }

  public static int getScreenHeight(Activity activity) {
    DisplayMetrics metrics = new DisplayMetrics();
    activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);

    return metrics.heightPixels;
  }

  public static boolean isMyServiceRunning(Class<?> serviceClass) {
    ActivityManager manager =
        (ActivityManager) App.getInstance().getSystemService(Context.ACTIVITY_SERVICE);
    for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(
        Integer.MAX_VALUE)) {
      if (serviceClass.getName().equals(service.service.getClassName())) {
        return true;
      }
    }
    return false;
  }

  public static List<String> getLast21Days() {
    Calendar c = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
    c.add(Calendar.MILLISECOND, Calendar.getInstance().getTimeZone().getRawOffset());
    c.add(Calendar.MILLISECOND, c.getInstance().getTimeZone().getDSTSavings());
    List<String> last21Days = new ArrayList<>();
    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    df.setTimeZone(c.getTimeZone());

    for (int i = 0; i < Constants.REPORTED_DAYS; i++) {
      last21Days.add(df.format(c.getTime()));
      c.add(Calendar.DATE, -1);
    }
    return last21Days;
  }

  public static boolean isNeedGoToNextDay(ReportStats lastReportStat) {
    Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
    calendar.add(Calendar.MILLISECOND, Calendar.getInstance().getTimeZone().getRawOffset());
    calendar.add(Calendar.MILLISECOND, calendar.getInstance().getTimeZone().getDSTSavings());

    App.appendLog(Constants.TAG_TEST_GO_TO_NEXT_DAY,
        "isNeedGoToNextDay() currentDate:" + " " + calendar.get(Calendar.YEAR) + "/" + calendar.get(
            Calendar.MONTH) + "/" + calendar.get(Calendar.DAY_OF_MONTH) + " " + calendar.get(
            Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE));

    long currentNumbersDay = calendar.getTimeInMillis() / MILLISECONDS_IN_DAYS;

    calendar.setTime(StringHelper.convertReportStatDateWithoutTimeZone(lastReportStat.getDate()));
    App.appendLog(Constants.TAG_TEST_GO_TO_NEXT_DAY, "isNeedGoToNextDay() lastStatsDate:"
        + " "
        + calendar.get(Calendar.YEAR)
        + "/"
        + calendar.get(Calendar.MONTH)
        + "/"
        + calendar.get(Calendar.DAY_OF_MONTH)
        + " "
        + calendar.get(Calendar.HOUR_OF_DAY)
        + ":"
        + calendar.get(Calendar.MINUTE));

    long statsNumbersDay = (calendar.getTimeInMillis()) / MILLISECONDS_IN_DAYS;

    return currentNumbersDay > statsNumbersDay;
  }

  public static int numbersOffsetDays(ReportStats lastReportStat) {
    Calendar calendar = Calendar.getInstance();
    calendar.add(Calendar.MILLISECOND, Calendar.getInstance().getTimeZone().getRawOffset());
    calendar.add(Calendar.MILLISECOND, calendar.getInstance().getTimeZone().getDSTSavings());

    int currentNumbersDay = (int) (calendar.getTimeInMillis() / MILLISECONDS_IN_DAYS);

    calendar.setTime(StringHelper.convertReportStatDateWithoutTimeZone(lastReportStat.getDate()));

    int statsNumbersDay = (int) (calendar.getTimeInMillis() / MILLISECONDS_IN_DAYS);
    return currentNumbersDay - statsNumbersDay;
  }

  public static boolean isPackageInstalled(String packageName) {
    boolean isInstalled = false;
    List<PackageInfo> installed = getInstalledApps();
    for (PackageInfo packageInfo : installed) {
      if (TextUtils.equals(packageName, packageInfo.packageName)) {
        isInstalled = true;
        break;
      }
    }

    return isInstalled;
  }

  private static List<PackageInfo> getInstalledApps() {
    final PackageManager pm = App.getInstance().getPackageManager();
    return pm.getInstalledPackages(0);
  }

  public static String getDeviceName() {
    String manufacturer = Build.MANUFACTURER;
    String model = Build.MODEL;
    if (model.toLowerCase().startsWith(manufacturer.toLowerCase())) {
      return capitalize(model);
    } else {
      return capitalize(manufacturer) + " " + model;
    }
  }

  private static String capitalize(String s) {
    if (s == null || s.length() == 0) {
      return "";
    }
    char first = s.charAt(0);
    if (Character.isUpperCase(first)) {
      return s;
    } else {
      return Character.toUpperCase(first) + s.substring(1);
    }
  }
}
