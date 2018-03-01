package com.sugarman.myb.utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
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
    if (countOfDays == 0){
      Calendar cal = Calendar.getInstance();
      cal.add(Calendar.DATE, -0); // I just want date before 90 days. you can give that you want.
      allPreviousDates.add(s.format(new Date(cal.getTimeInMillis())));
      Timber.e("getLastXDays i = " + 0 + " data : " + s.format(new Date(cal.getTimeInMillis())));
    }
    Collections.reverse(allPreviousDates);
    return allPreviousDates;
  }

  public static Long getDateDiff(Date date1, Date date2, TimeUnit timeUnit) {
    Long diffInMillies = date2.getTime() - date1.getTime();
    return timeUnit.convert(diffInMillies,TimeUnit.MILLISECONDS);
  }

  public static Date subtractDays(Date date,int days)
  {
   long newDate = date.getTime() - days * 86400000l;
   Date result = new Date(newDate);
   return result;
  }
}
