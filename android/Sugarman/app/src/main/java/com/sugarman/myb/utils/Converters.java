package com.sugarman.myb.utils;

import android.content.Context;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by nikita on 20.09.17.
 */

public class Converters {
  public static String loadAssetTextAsString(Context context, String name) {
    BufferedReader in = null;
    try {
      StringBuilder buf = new StringBuilder();
      InputStream is = context.getAssets().open(name);
      in = new BufferedReader(new InputStreamReader(is));

      String str;
      boolean isFirst = true;
      while ((str = in.readLine()) != null) {
        if (isFirst) {
          isFirst = false;
        } else {
          buf.append('\n');
        }
        buf.append(str);
      }
      return buf.toString();
    } catch (IOException e) {
    } finally {
      if (in != null) {
        try {
          in.close();
        } catch (IOException e) {
        }
      }
    }

    return null;
  }
}
