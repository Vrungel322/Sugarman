package com.sugarman.myb.tasks;

import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import java.io.File;

public class DeleteFileAsyncTask extends AsyncTask<String, Void, Void> {

  private static final String TAG = DeleteFileAsyncTask.class.getName();

  @Override protected Void doInBackground(String... params) {
    String path = params[0];
    if (!TextUtils.isEmpty(path)) {
      File file = new File(path);
      deleteRecursive(file);
    } else {
      Log.e(TAG, "file path is empty");
    }
    return null;
  }

  private void deleteRecursive(File fileOrDirectory) {
    if (fileOrDirectory != null) {
      if (fileOrDirectory.isDirectory() && fileOrDirectory.listFiles() != null) {
        for (File child : fileOrDirectory.listFiles()) {
          deleteRecursive(child);
        }
      }

      fileOrDirectory.delete();
    }
  }
}
