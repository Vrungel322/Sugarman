package com.sugarman.myb.tasks;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import com.sugarman.myb.constants.Config;
import com.sugarman.myb.listeners.AsyncSaveBitmapToFileListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.UUID;

public class SaveBitmapToFileAsyncTask extends AsyncTask<Bitmap, Void, String> {

  private static final String TAG = SaveBitmapToFileAsyncTask.class.getName();

  private final WeakReference<AsyncSaveBitmapToFileListener> endListener;

  private boolean isSuccess = true;

  public SaveBitmapToFileAsyncTask(AsyncSaveBitmapToFileListener listener) {
    endListener = new WeakReference<>(listener);
  }

  @Override protected String doInBackground(Bitmap... params) {
    Bitmap bmp = params[0];

    String fileName = UUID.randomUUID().toString() + ".jpg";
    String filePath = Config.APP_FOLDER + "/" + fileName;

    File file = new File(filePath);
    File dir = new File(Config.APP_FOLDER);
    if (!dir.exists()) {
      dir.mkdirs();
    }

    if (!file.exists()) {
      try {
        file.createNewFile();
      } catch (IOException e) {
        Log.e(TAG, "failure create file: " + filePath, e);
      }
    }

    FileOutputStream out = null;
    try {
      out = new FileOutputStream(filePath);
      bmp.compress(Bitmap.CompressFormat.PNG, 100, out);
    } catch (Exception e) {
      isSuccess = false;
      Log.e(TAG, "failed save bitmap to file", e);
    } finally {
      try {
        if (out != null) {
          out.close();
        }
      } catch (IOException e) {
        Log.e(TAG, "failed close output stream", e);
      }
    }

    return filePath;
  }

  @Override protected void onPostExecute(String result) {
    super.onPostExecute(result);

    if (endListener.get() != null) {
      if (isSuccess) {
        endListener.get().onAsyncBitmapSaveSuccess(result);
      } else {
        endListener.get().onAsyncBitmapSaveFailed();
      }
    }
  }
}
