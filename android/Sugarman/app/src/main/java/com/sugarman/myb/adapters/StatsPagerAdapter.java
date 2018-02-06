package com.sugarman.myb.adapters;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import com.sugarman.myb.api.models.responses.me.stats.Stats;
import com.sugarman.myb.constants.Config;
import com.sugarman.myb.ui.fragments.StatsWeekFragment;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;

public class StatsPagerAdapter extends FragmentStatePagerAdapter {

  private final Hashtable<Integer, SoftReference<StatsWeekFragment>> cashedFragments =
      new Hashtable<>();

  private Stats[] stats = new Stats[0];
  private boolean isMentors;

  public StatsPagerAdapter(FragmentManager fm) {
    super(fm);
  }

  @Override public StatsWeekFragment getItem(int position) {
    StatsWeekFragment fragment = getFragment(position);
    if (fragment == null) {
      List<Stats> statsList = new ArrayList<>(Config.DAYS_ON_STATS_SCREEN);
      int from = position * Config.DAYS_ON_STATS_SCREEN;
      int tmp = from + Config.DAYS_ON_STATS_SCREEN;
      int to = tmp > stats.length ? stats.length : tmp;
      statsList.addAll(Arrays.asList(stats).subList(from, to));

      fragment =
          StatsWeekFragment.newInstance(statsList.toArray(new Stats[statsList.size()]), position,isMentors);
      cashedFragments.put(position, new SoftReference<>(fragment));
    }
    return fragment;
  }

  @Override public int getCount() {
    return stats.length / Config.DAYS_ON_STATS_SCREEN + (
        stats.length % Config.DAYS_ON_STATS_SCREEN == 0 ? 0 : 1);
  }

  public void setStats(Stats[] stats, boolean isMentors) {
    this.stats = stats;
    this.isMentors = isMentors;
    notifyDataSetChanged();
  }

  public void setStats(Stats[] stats) {
    this.stats = stats;
    this.isMentors = isMentors;
    notifyDataSetChanged();
  }

  public int getTodayIndex() {
    for (int i = 0; i < cashedFragments.size(); i++) {
      SoftReference<StatsWeekFragment> reference = cashedFragments.get(i);
      if (reference.get() != null) {
        if (reference.get().isToday()) {
          return i;
        }
      }
    }
    return 0;
  }

  private StatsWeekFragment getFragment(int position) {
    SoftReference<StatsWeekFragment> reference = cashedFragments.get(position);
    return reference == null ? null : reference.get();
  }
}
