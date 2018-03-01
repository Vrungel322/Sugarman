package com.sugarman.myb.utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import timber.log.Timber;

/**
 * Created by nikita on 01.03.2018.
 */

public class DataUtils {
  public static List<String> getLastXDays(int countOfDays) {
    List<String> allPreviousDates = new ArrayList<>();
    SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd"); // you can specify your format here...
    for (int i = 0; i < countOfDays; i++) {
      Calendar cal = Calendar.getInstance();
      cal.add(Calendar.DATE, -i); // I just want date before 90 days. you can give that you want.
      allPreviousDates.add(s.format(new Date(cal.getTimeInMillis())));
      Timber.e("getLastXDays i = " + i + " data : " + s.format(new Date(cal.getTimeInMillis())));
    }
    Collections.reverse(allPreviousDates);
    return allPreviousDates;
  }
}
