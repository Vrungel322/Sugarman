package com.sugarman.myb.data.db;

import com.sugarman.myb.api.models.responses.me.invites.Invite;
import com.sugarman.myb.utils.SharedPreferenceHelper;
import java.util.List;

/**
 * Created by nikita on 23.03.2018.
 */

public class DbRepositoryInvites {
  @Deprecated
  public void dropTable() {

  }

  public void saveEntity(List<Invite> invites) {
    SharedPreferenceHelper.saveInvites(invites);
  }

  public List<Invite> getAllEntities() {
    return SharedPreferenceHelper.getInvites();
  }
}
