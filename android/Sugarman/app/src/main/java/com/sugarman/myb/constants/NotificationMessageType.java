package com.sugarman.myb.constants;

public abstract class NotificationMessageType {

  public static final int UNKNOWN = -1;
  public static final int GROUP_NAME_IN_HOUR = 0;
  public static final int GROUP_NAME_GOOD_LUCK = 1;
  public static final int USER_NAME_HAS_POKED_YOU = 2;
  public static final int USER_NAME_INVITED = 3;
  public static final int CONGRATS = 4;
  public static final int DAILY_SUGARMAN = 5;
  public static final int USER_NAME_JOINED = 6;
  public static final int GROUP_NAME_HAS_FAILED = 7;
  public static final int ONE_MORE_DAY_TO_ADD_FRIENDS = 8;
  public static final int INVITATION_NO_AVAILABLE = 9;
  public static final int CREATOR_NAME_APPROVED = 10;
  public static final int USER_NAME_REQUESTED = 11;
  public static final int IS_UNABLE_TO_START = 12;
  public static final int PINGED_YOU_TO_MYB = 13;

  // order is important!
  public static final int[] IDS = new int[] {
      GROUP_NAME_IN_HOUR, GROUP_NAME_GOOD_LUCK, USER_NAME_HAS_POKED_YOU, USER_NAME_INVITED,
      CONGRATS, DAILY_SUGARMAN, USER_NAME_JOINED, GROUP_NAME_HAS_FAILED,
      ONE_MORE_DAY_TO_ADD_FRIENDS, INVITATION_NO_AVAILABLE, CREATOR_NAME_APPROVED,
      USER_NAME_REQUESTED, IS_UNABLE_TO_START, PINGED_YOU_TO_MYB
  };

  private NotificationMessageType() {
    // only static methods and fields
  }
}
