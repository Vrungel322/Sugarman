package com.sugarman.myb.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

/**
 * Created by yegoryeriomin on 9/19/17.
 */

public class SaveFileHelper {
  public static String saveFileTemp(Bitmap bitmap, Context context) {

    File f3 = new File(context.getFilesDir() + "/tempImages/");
    if (!f3.exists()) f3.mkdirs();
    OutputStream outStream = null;
    File file = new File(context.getFilesDir() + "/tempImages/" + "avatar" + ".png");
    try {
      outStream = new FileOutputStream(file);
      bitmap.compress(Bitmap.CompressFormat.PNG, 30, outStream);
      outStream.close();
      Log.e("FILE", "Saved " + file.getAbsolutePath());
    } catch (Exception e) {
      e.printStackTrace();
    }
    return file.getAbsolutePath();
  }
}
