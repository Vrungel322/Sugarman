package com.sugarman.myb.data.db;

import com.sugarman.myb.api.models.responses.me.stats.Stats;
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
      dropStatsTable();
      Timber.e("saveStats " + (mDbHelper != null));
      for (Stats s : statsList) {
        mDbHelper.save(s);
      }
    }
  }
}
