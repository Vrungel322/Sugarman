package com.sugarman.myb.data.db;

import com.sugarman.myb.api.models.responses.me.stats.Stats;
import com.sugarman.myb.utils.SharedPreferenceHelper;
import java.util.List;
import timber.log.Timber;

/**
 * Created by nikita on 28.02.2018.
 */

public class DbRepositoryStats implements IDbRepository<Stats> {

  public DbRepositoryStats() {
  }

  @Override public void dropTable() {
    Timber.e("dropTable");
  }

  @Override public void saveEntity(Stats statsList) {
    if (statsList != null) {
      Timber.e("saveEntityList entityList list size = " + statsList.getDate());
      dropTable();
      SharedPreferenceHelper.saveStats(statsList);
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
  @Override public List<Stats> getAllEntities(int previousCountDays) {
    return SharedPreferenceHelper.getStats(previousCountDays);
  }
}
