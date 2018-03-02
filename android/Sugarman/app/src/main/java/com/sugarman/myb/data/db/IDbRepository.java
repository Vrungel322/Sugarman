package com.sugarman.myb.data.db;

import java.util.List;

/**
 * Created by nikita on 01.03.2018.
 */

public interface IDbRepository<T> {
  void dropTable();

  void saveEntity(T entityList);

  //void appendEntities(List<T> entityList);

  List<T> getAllEntities(int previousCountDays);
}
