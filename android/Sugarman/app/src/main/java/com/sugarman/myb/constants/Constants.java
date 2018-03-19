package com.sugarman.myb.constants;

import android.Manifest;
import android.util.SparseIntArray;
import android.view.Surface;

public abstract class Constants {

  public static final long SPLASH_UPDATE_TIMEOUT = 300;

  public static final long SPLASH_TIMEOUT = 2000;// ms

  public static final int NOTIFICATION_LARGE_ICON_SIZE_PX = 128;

  public static final String DEF_TIMEZONE = "Asia/Jerusalem";

  public static final String CONNECTIVITY_CHANGE_ACTION = "android.net.conn.CONNECTIVITY_CHANGE";

  public static final String WIFI_CHANGE_ACTION = "android.net.wifi.WIFI_STATE_CHANGED";

  public static final String QUICK_BOOT_ACTION = "android.intent.action.QUICKBOOT_POWERON";

  public static final String PLATFORM = "android";

  public static final String INTENT_TRACKING = "intent_tracking";

  public static final String INTENT_CREATED_TRACKING = "intent_created_tracking";

  public static final String INTENT_RECREATE_TRACKING = "intent_recreate_tracking";

  public static final String INTENT_IS_NEED_REFRESH_TRACKINGS = "intent_is_need_refresh_trackings";

  public static final String INTENT_LAST_ACCEPT_INVITE_TRACKING_ID =
      "intent_last_accept_invite_tracking_id";

  public static final String INTENT_FCM_TRACKING_ID = "intent_fcm_tracking_id";

  public static final String INTENT_MEMBERS = "intent_members";

  public static final String INTENT_PENDINGS = "intent_pendings";

  public static final String INTENT_TRACKING_ID = "intent_tracking_id";

  public static final String INTENT_TIMESTAMP_CREATE = "intent_timestamp_create";

  public static final String INTENT_GROUP_NAME = "intent_name";

  public static final String INTENT_GROUP_PICTURE = "intent_picture";

  public static final String INTENT_MY_TRACKINGS = "intent_my_trackings";

  public static final String INTENT_MY_REQUESTS = "intent_my_requests";

  public static final String INTENT_MY_INVITES = "intent_my_invites";

  public static final String INTENT_MY_NOTIFICATIONS = "intent_my_notifications";

  public static final String INTENT_OPEN_ACTIVITY = "intent_open_activity";

  public static final int OPEN_MAIN_ACTIVITY = 1;

  public static final int OPEN_INVITES_ACTIVITY = 2;

  public static final int OPEN_REQUESTS_ACTIVITY = 3;

  public static final int OPEN_CONGRATULATION_ACTIVITY = 4;

  public static final int OPEN_DAILY_ACTIVITY = 5;

  public static final int OPEN_FAILED_ACTIVITY = 6;

  public static final int OPEN_EXTERNAL_URL = 7;

  public static final String BUNDLE_TRACKING_ITEM = "bundle_tracking_item";

  public static final String BUNDLE_TRACKING_POSITION = "bundle_tracking_position";

  public static final String BUNDLE_TRACKINS_COUNT = "bundle_trackings_count";

  public static final String BUNDLE_NOTIFICATIONS = "bundle_notifications";

  public static final String BUNDLE_MY_STATS = "bundle_my_stats";

  public static final String BUNDLE_POSITION = "bundle_position";

  public static final long MS_IN_DAY = 86400000; //1000 * 60 * 60 * 24

  public static final int MS_IN_HOUR = 3600000; // 1000 * 60 * 60

  public static final int MS_IN_MIN = 60000; // 1000 * 60

  public static final int MS_IN_SEC = 1000;

  public static final long OPEN_CREATED_CHALLENGE_TIMEOUT = 400; // ms

  public static final float CHALLENGES_PAGER_OUTSIDE_POSITION_OFFSET = 1f;
  public static final float CHALLENGES_PAGER_POSITION_OFFSET = 0.93f;

  // Rotations (for camera activities)
  private static final SparseIntArray ORIENTATIONS = new SparseIntArray();
  public static final String READ_PHONE_CONTACTS_PERMISSION = "android.permission.READ_CONTACTS";
  public static final String INTENT_FCM_URL = "intent_fcm_url";
  public static final String IMEI = "IMEI";
  public static final String PAY_PAL_PAYMENT_TYPE = "paypal";
  public static final String FREE_PAYMENT_TYPE = "free";
  public static final String INVITE_FRIENDS_PAYMENT_TYPE = "friends";
  public static final int ALL_SLOTS_NOT_EMPTY_ERROR = 222;
  public static final String EVENT_X_NEW_USERS_INVITE = "event_type_group_with_new_x_users";
  public static final String IS_MENTORS = "IS_MENTORS";
  public static final int OPEN_GROPEDETAILS_ACTIVITY_WHERE_WAS_POKE = 1112;
  public static final int FAKE_STEPS_COUNT = -1;
  public static final String TRACKING = "TRACKING";

  static {
    ORIENTATIONS.append(Surface.ROTATION_0, 90);
    ORIENTATIONS.append(Surface.ROTATION_90, 0);
    ORIENTATIONS.append(Surface.ROTATION_180, 270);
    ORIENTATIONS.append(Surface.ROTATION_270, 180);
  }

  public static final int INTENT_CHOOSER_IMAGE_REQUEST_CODE = 1;

  public static final int CREATE_GROUP_ACTIVITY_REQUEST_CODE = 2;

  public static final int ADD_MEMBER_ACTIVITY_REQUEST_CODE = 3;

  public static final int GROUP_DETAILS_ACTIVITY_REQUEST_CODE = 4;

  public static final int OPEN_INVITES_ACTIVITY_REQUEST_CODE = 5;

  public static final int OPEN_REQUESTS_ACTIVITY_REQUEST_CODE = 6;

  public static final int OPEN_PROFILE_ACTIVITY_REQUEST_CODE = 7;

  public static final int OPEN_FAILED_ACTIVITY_REQUEST_CODE = 8;

  public static final int REQUEST_EXTERNAL_STORAGE_PERMISSION_CODE = 100;

  public static final String[] PERMISSION_EXTERNAL_STORAGE = new String[] {
      Manifest.permission.WRITE_EXTERNAL_STORAGE
  };

  public static final String FB_FIELDS = "fields";

  public static final String FB_FRIENDS_GRAPH_PATH_TEMPLATE = "/%1$s/friends?limit=%2$d";

  public static final String FB_INVITABLE_FRIENDS_GRAPH_PATH_TEMPLATE =
      "/%1$s/invitable_friends?limit=%2$d";

  public static final String FB_GET_FRIEND_INFO_GRAPH_PATH_TEMPLATE = "/%1$s/?ids=%2$s";

  public static final String COMMAND_START_STEP_DETECTOR_SERVICE =
      "command_start_step_detector_service";

  public static final String COMMAND_STOP_STEP_DETECTOR_SERVICE =
      "command_stop_step_detector_service";

  public static final int STEP_DETECTOR_FOREGROUND_NOTIFICATION_ID = 1814;

  public static final String IMAGE_JPEG_TYPE = "image/jpeg";

  public static final String TEXT_PLAIN_TYPE = "text/plain";

  public static final String PICTURE = "picture";

  public static final String IN_APP_NOTIFICATION_MESSAGE_DIVIDER = "_ae_314_qp_";

  public static final String FCM_MESSAGE = "message";

  public static final String FCM_NOTIFICATION = "notification";

  public static final int REPORTED_DAYS = 21;

  public static final String FCM_REPORT_STEPS_KEY = "report-request";

  public static final String POST = "POST";
  public static final String REPORT_ENDPOINT = "/v1/stats/day";
  public static final String AUTHORIZATION = "authorization";
  public static final String TIMESTAMP = "timestamp";
  public static final String TIMEZONE = "timezone";
  public static final String VERSION = "android_app_version";
  public static final String CONTENT_TYPE = "Content-type";
  public static final String APP_JSON = "application/json";
  public static final String BEARER = "Bearer ";
  public static final String UTF_8 = "UTF-8";

  public static final String FACEBOOK_PACKAGE = "com.facebook.katana";

  public static final String GOOGLE_PLAY_APP_URL_TEMPLATE =
      "https://play.google.com/store/apps/details?id=%1$s";

  public static final String STATUS_FAILED = "failed";

  public static final String STATUS_COMPLETED = "completed";

  public static final int SUCCESS_RESPONSE_CODE = 200;

  public static final int RESPONSE_228 = 228;

  public static final int FIREBASE_NOTIFICATION_ID = 1011;

  public static final int SWITCH_TO_NEXT_DAY_REQUEST_CODE = 2;

  public static final int STEP_DETECTOR_CODE = 41;

  public static final String LOGIN = "login";

  public static final String SIGN_UP = "sign-up";

  public static final String CHALLENGE = "challenge";

  public static final String INVITE = "invite";

  public static final String TAG_TEST_GO_TO_NEXT_DAY = "GO_TO_NEXT_DAY_TEST";

  public static final String EVENT_X_STEPS_DONE = "event_type_reach_steps";
  public static final String EVENT_PLAY_ANIMATION = "event_type_play_animation";

  private Constants() {
    // only static methods and fields
  }
}
