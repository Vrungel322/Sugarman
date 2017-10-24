package com.sugarman.myb.models.tasks;

/**
 * Created by nikita on 24.10.2017.
 */

public class InviteNnewFriendsTask extends Task {
  private int mNumOfInvitedFriends;

  public InviteNnewFriendsTask(int level, int id, int type, String name, String discription) {
    super(level, id, type, name, discription);
  }

  public InviteNnewFriendsTask(int numOfInvitedFriends, int level, int id, int type, String name,
      String discription) {
    super(level, id, type, name, discription);
    this.mNumOfInvitedFriends = numOfInvitedFriends;
  }
}
