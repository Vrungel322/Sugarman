package com.sugarman.myb.utils;

import android.content.Context;
import com.sugarman.myb.R;
import com.sugarman.myb.constants.Constants;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.text.NumberFormat;

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

  public static String timeFromMilliseconds(Context context,long timeMs) {
    //if (!date.equals("")) {
    //  String dateResult;
    //  SimpleDateFormat formatter =
    //      //new SimpleDateFormat("HH:mm:ss", new Locale(Locale.getDefault().getLanguage()));
    //      new SimpleDateFormat("HH:mm:ss", new Locale("en"));
    //  Calendar calendar = Calendar.getInstance(new Locale("en"));
    //  long d = Long.valueOf(date);
    //  calendar.setTimeInMillis(d);
    //  dateResult = formatter.format(calendar.getTime());
    //
    //  return dateResult;
    //}
    //return "";
    final NumberFormat timeFormatter = new DecimalFormat("00");

    int days = (int) (timeMs / Constants.MS_IN_DAY);
    int hours = (int) ((timeMs % Constants.MS_IN_DAY) / Constants.MS_IN_HOUR);
    int minutes = (int) ((timeMs % Constants.MS_IN_HOUR) / Constants.MS_IN_MIN);
    int sec = (int) ((timeMs % Constants.MS_IN_MIN) / Constants.MS_IN_SEC);

    String timeFormatted =
        String.format(context.getString(R.string.timer_template), timeFormatter.format(days), timeFormatter.format(hours),
            timeFormatter.format(minutes), timeFormatter.format(sec));
    return timeFormatted;
  }
}
