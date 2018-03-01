package com.sugarman.myb.data.db;

import com.sugarman.myb.api.models.responses.me.stats.StatsResponse;
import com.sugarman.myb.utils.SharedPreferenceHelper;
import timber.log.Timber;

/**
 * Created by nikita on 28.02.2018.
 */

public class DbRepositoryStats implements IDbRepository<StatsResponse> {

  public DbRepositoryStats() {
  }

  @Override public void dropTable() {
    Timber.e("dropTable");
    SharedPreferenceHelper.clearStats();
  }

  @Override public void saveEntityList(StatsResponse entityList) {
    if (entityList != null) {
      Timber.e("saveEntityList entityList list size = " + entityList.getResult().length);
      dropTable();
      SharedPreferenceHelper.saveStats(entityList);
    }
  }

  //@Override public void appendEntities(List<Stats> entityList) {
    //if (entityList != null) {
    //  Timber.e("appendEntities entityList list size = " + entityList.size());
    //  List<Stats> appendedList = new ArrayList<>();
    //  appendedList.addAll(mDbHelper.getAll(Stats.class));
    //  Timber.e("appendEntities previous list size = " + (mDbHelper.getAll(Stats.class).size()));
    //  appendedList.addAll(entityList);
    //  Timber.e("appendEntities appendedList list size = " + appendedList.size());
    //  saveEntityList(new StatsResponse(appendedList));
    //}
  //}

  @Override public StatsResponse getAllEntities() {
    return SharedPreferenceHelper.getStats();
  }
}
