package com.sugarman.myb.data.db;

import com.sugarman.myb.api.models.responses.me.stats.StatsResponse;

/**
 * Created by nikita on 01.03.2018.
 */

public interface IDbRepository<T> {
  void dropTable();

  void saveEntityList(StatsResponse entityList);

  //void appendEntities(List<T> entityList);

  T getAllEntities();

}
