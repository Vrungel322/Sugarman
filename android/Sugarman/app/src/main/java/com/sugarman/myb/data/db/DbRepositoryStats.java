package com.sugarman.myb.data.db;

import com.sugarman.myb.api.models.responses.me.stats.Stats;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import timber.log.Timber;

/**
 * Created by nikita on 28.02.2018.
 */

public class DbRepositoryStats {
  private DbHelper mDbHelper;

  @Inject public DbRepositoryStats(DbHelper dbHelper) {
    mDbHelper = dbHelper;
  }

  private void dropStatsTable() {
    mDbHelper.dropRealmTable(Stats.class);
  }

  public void saveStats(List<Stats> statsList) {
    if (statsList != null) {
      Timber.e("saveStats statsList list size = " + statsList.size());

      dropStatsTable();
      Timber.e("saveStats " + (mDbHelper != null));
      for (Stats s : statsList) {
        mDbHelper.save(s);
      }
    }
  }

  public void appendStats(List<Stats> statsList) {
    if (statsList != null) {
      Timber.e("appendStats statsList list size = " + statsList.size());
      List<Stats> appendedList = new ArrayList<>();
      appendedList.addAll(mDbHelper.getAll(Stats.class));
      Timber.e("appendStats previous list size = " + (mDbHelper.getAll(Stats.class).size()));
      appendedList.addAll(statsList);
      Timber.e("appendStats appendedList list size = " + appendedList.size());
      saveStats(appendedList);
    }
  }

}
