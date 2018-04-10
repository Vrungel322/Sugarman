package com.sugarman.myb.utils;

import java.util.List;
import timber.log.Timber;

public class StatsUtils {
  public static int HOUR = 3600000;
  public static int DEBUG_DELAY = 2000;

  public static int countSumOfStats(List<Integer> stats) {
    int sum = 0;
    Timber.e("countSumOfStats stats size" + stats.size());
    for (int i : stats) {
      Timber.e("countSumOfStats IN FOR" + sum);
      sum += i;
    }
    return sum;
  }
}
