package com.sugarman.myb.constants;

import android.os.Environment;

public abstract class Config {

  private static final String EXTERNAL_DIR =
      Environment.getExternalStorageDirectory().getAbsolutePath();

  public static final String APP_FOLDER_NAME = ".myb";

  public static final String APP_FOLDER = EXTERNAL_DIR + "/" + APP_FOLDER_NAME;

  public static final String TERMS_OF_USE_URL = "http://www.sugarman.eu/terms-and-conditions/";

  public static final String PRIVACY_POLICY = "http://www.sugarman.eu/privacy-policy/";

  public static final String SERVER_URL = "http://sugarman-server.herokuapp.com/";
  //public static final String SERVER_URL = "http://sugarman-server-test.herokuapp.com/";
  //http://sugarman-server-test.herokuapp.com/     http://sugarman-server.herokuapp.com

  public static final long RETROFIT_TIMEOUT = 20; // secs

  public static final String FB_FRIENDS_FIELDS = "id,name,picture.type(normal)";

  public static final int FB_FRIENDS_LIMIT = 600;

  public static final long VIBRATION_TIME = 400; // ms

  public static final int MAIN_INDICATOR_DEGREE_PERIOD = 4;

  public static final int NUMBER_STROKES_INDICATOR_LINE = 59;

  public static final int MAX_ACTIVE_CREATED_TRACKINGS = 100;

  public static final int MAX_PICTURE_SIZE_SEND_TO_SERVER = 512; // px

  public static final int DAYS_ON_STATS_SCREEN = 7; // 7 days on one screen

  public static final long BROKEN_AVATAR_TIME = 1000; // ms

  public static final long SHOW_ACTION_BACKGROUND_TIME = 500; // ms

  public static final long INVITE_TIME_LIVE = Constants.MS_IN_DAY;// 24 * 60 * 60 * 1000

  public static final long TIMEOUT_BETWEEN_STEP_REPORT = 5000; // 5 * 1000

  public static final long TIMEOUT_BETWEEN_STEP_REPORT_FOREGROUND = 60000;

  public static final long MAKE_SCREENSHOT_DELAY = 400;

  public static final int VISIBLE_NOTIFICATIONS = 4;

  public static final int SHOWING_DAYS_STATS = 22; // 21 day + 1 day of warming up

  public static final String[] FACEBOOK_PERMISSIONS = new String[] {
      "public_profile", "user_friends", "email", "read_custom_friendlists"
  };

  public static final int MAX_STEPS_PER_DAY = 10000;

  public static final int MAX_STEPS_STATS = 20000;

  public static final int ANIMATION_COUNT_ITERATIONS = 20;

  public static final long MAIN_ANIMATION_STEPS_ITERATION_DELAY = 100;

  public static final long STATS_ANIMATION_STEPS_ITERATION_DELAY = 30;

  public static final long MAIN_POKE_SOUND_DELAY = 1000;

  public static final int SCREEN_OFF_RECEIVER_DELAY = 500;

  private static final int RED_MEMBERS_FIRST_THRESHOLD = 5;
  private static final int RED_MEMBERS_SECOND_THRESHOLD = 8;
  private static final int RED_MEMBERS_FIRST_COUNT = 1;
  private static final int RED_MEMBERS_SECOND_COUNT = 2;
  private static final int RED_MEMBERS_THIRD_COUNT = 3;
  public static final String PAYPAL_CLIENT_ID =
      "Aa--fSuCgRvEzg4BjpTIyw8OApALM4Vt7_0QhhQmdIgtj7GCLczznXyxakNwxZVIF5Pmz0pDO2hWdkf9";

  public static int getCountRedMembers(int totalMembers) {
    int countRedMembers;
    if (totalMembers < RED_MEMBERS_FIRST_THRESHOLD) {
      countRedMembers = RED_MEMBERS_FIRST_COUNT;
    } else if (totalMembers < RED_MEMBERS_SECOND_THRESHOLD) {
      countRedMembers = RED_MEMBERS_SECOND_COUNT;
    } else {
      countRedMembers = RED_MEMBERS_THIRD_COUNT;
    }

    return countRedMembers;
  }

  private Config() {
    // only static methods and fields
  }
}
