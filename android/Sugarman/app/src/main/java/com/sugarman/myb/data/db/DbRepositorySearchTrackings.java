package com.sugarman.myb.data.db;

import com.sugarman.myb.api.models.responses.trackings.TrackingsResponse;
import com.sugarman.myb.utils.SharedPreferenceHelper;

/**
 * Created by nikita on 22.03.2018.
 */

public class DbRepositorySearchTrackings {
  @Deprecated public void dropTable() {

  }

  public void saveEntity(TrackingsResponse trackingsResponse) {
    SharedPreferenceHelper.saveTrackingResponce(trackingsResponse);
  }

  public TrackingsResponse getAllEntities() {
    return SharedPreferenceHelper.getTrackingResponce();
  }
}
