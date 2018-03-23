package com.sugarman.myb.data.db;

import com.sugarman.myb.api.models.responses.me.requests.Request;
import com.sugarman.myb.utils.SharedPreferenceHelper;
import java.util.List;

/**
 * Created by nikita on 23.03.2018.
 */

public class DbRepositoryRequests {
  @Deprecated
  public void dropTable() {

  }

  public void saveEntity(List<Request> requests) {
    SharedPreferenceHelper.saveRequests(requests);
  }

  public List<Request> getAllEntities() {
    return SharedPreferenceHelper.getRequests();
  }
}
