package com.sugarman.myb.utils;

import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sugarman.myb.api.models.requests.ReportStats;
import com.sugarman.myb.api.models.responses.facebook.FacebookFriend;
import com.sugarman.myb.api.models.responses.me.stats.Stats;
import com.sugarman.myb.api.models.responses.users.Tokens;
import com.sugarman.myb.api.models.responses.users.User;
import com.sugarman.myb.constants.Config;
import com.sugarman.myb.constants.Constants;
import com.sugarman.myb.constants.SharedPreferenceConstants;
import com.sugarman.myb.models.iab.SubscriptionEntity;
import com.sugarman.myb.models.mentor.MentorEntity;
import com.sugarman.myb.models.splash_activity.DataForMainActivity;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import timber.log.Timber;

public class SharedPreferenceHelper extends BaseSharedPreferenceHelper {

  public static void saveUser(User user) {
    if (user == null) {
      clearUser();
    } else {
      String userId = user.getId();
      putInt(SharedPreferenceConstants.ACTIVE_TRACKINGS_CREATED, user.getActiveTrackingsCreated());
      putInt(SharedPreferenceConstants.COMPLETED_CHALLENGES_COUNT,
          user.getCompletedChallengesCount());
      putString(SharedPreferenceConstants.CREATED_AT, user.getCreatedAt());
      //putString(SharedPreferenceConstants.FACEBOOK_ID, user.getFacebookId());
      putString(SharedPreferenceConstants.USER_ID, userId);
      putString(SharedPreferenceConstants.USER_NAME, user.getName());
      putString(SharedPreferenceConstants.USER_PICTURE_URL, user.getPictureUrl());
      putString(SharedPreferenceConstants.USER_TIMEZONE, user.getTimezone());
      putDouble(SharedPreferenceConstants.TODAY_PROGRESS_PERCENT, user.getTodayProgressPercent());
      putString(SharedPreferenceConstants.UPDATED_AT, user.getUpdatedAt());
      putInt(SharedPreferenceConstants.COMPLETED_DAYS_COUNT, user.getCompletedDaysCount());
      putInt(SharedPreferenceConstants.TODAY_STEPS_COUNT, user.getTodayStepsCount());
      putString(SharedPreferenceConstants.SUBSCRIPTIONS_JSON,
          new Gson().toJson(user.getSubscriptionEntities()));
      Timber.e("user json " + new Gson().toJson(user.getSubscriptionEntities()).toString());

      if (user.getIsMentor() != null) {
        setIsMentor(user.getIsMentor());
        Timber.e("Mentor set to " + user.getIsMentor());
      }
      if (user.getNeedOTP() != null) {
        setOTPStatus(user.getNeedOTP());
      } else {
        setOTPStatus(false);
      }
      putInt("level", user.getLevel());
    }
  }

  public static void saveListSubscriptionEntity(List<SubscriptionEntity> subscriptionEntities) {
    Timber.e(
        "saveListSubscriptionEntity json " + new Gson().toJson(subscriptionEntities).toString());
    putString(SharedPreferenceConstants.SUBSCRIPTIONS_JSON,
        new Gson().toJson(subscriptionEntities));
  }

  public static List<SubscriptionEntity> getListSubscriptionEntity() {
    Type type = new TypeToken<List<SubscriptionEntity>>() {
    }.getType();
    Timber.e(
        "getListSubscriptionEntity json " + getString(SharedPreferenceConstants.SUBSCRIPTIONS_JSON,
            ""));
    return new Gson().fromJson(getString(SharedPreferenceConstants.SUBSCRIPTIONS_JSON, ""), type);
  }

  public static boolean getContactsSent() {
    return getBoolean("contactsSent", false);
  }

  public static void setContactsSent(boolean contactsSent) {
    putBoolean("contactsSent", contactsSent);
  }

  public static int getLevel() {
    return getInt("level", 0);
  }

  public static void setLevel(int level) {
    putInt("level", level);
  }

  public static String getFacebookId() {
    return getString("facebook_id", "none");
  }

  public static String getTimezone() {
    return getString(SharedPreferenceConstants.USER_TIMEZONE, "");
  }

  public static void saveToken(Tokens tokens) {
    Timber.e("SaveToken called");
    if (tokens != null) {
      if (tokens.getAccessToken().length() > 0 && tokens.getRefreshToken().length() > 0) {
        putString(SharedPreferenceConstants.ACCESS_TOKEN, tokens.getAccessToken());
        putString(SharedPreferenceConstants.REFRESH_TOKEN, tokens.getRefreshToken());
      }
    }
  }

  public static void savePhoneNumber(String phoneNumber) {
    putString("phone_number", phoneNumber);
  }

  public static String getPhoneNumber() {
    return getString("phone_number", "none");
  }

  public static String getFBAccessToken() {
    return getString("fb_access_token", "none");
  }

  public static void saveVkToken(String vkToken) {
    putString("vkToken", vkToken);
  }

  public static String getVkToken() {
    return getString("vkToken", "none");
  }

  public static void saveTrackingSeenDailySugarman(String trackingId, boolean seen) {
    putBoolean(trackingId + "_seen", seen);
  }

  public static boolean getTrackingSeenDailySugarman(String trackingId) {
    return getBoolean(trackingId + "_seen", false);
  }

  public static int getUserTodaySteps() {
    return getInt(SharedPreferenceConstants.TODAY_STEPS_COUNT, 0);
  }

  public static void saveUserTodaySteps(int steps) {
    putInt(SharedPreferenceConstants.TODAY_STEPS_COUNT, steps);
  }

  public static void saveCompletedDaysCount(int days) {
    putInt(SharedPreferenceConstants.COMPLETED_DAYS_COUNT, days);
  }

  public static boolean isInvitesMenuEnabled() {
    return getBoolean("isInvitesMenuEnabled", true);
  }

  public static String getLanguage() {
    if (getString("pref_app_language", "English").equals("ru")) {
      return "Русский";
    } else if (getString("pref_app_language", "English").equals("iw")) return "עברית";
    return "English";
  }

  public static void clearUser() {
    Timber.e("Clear User called");
    putInt(SharedPreferenceConstants.ACTIVE_TRACKINGS_CREATED, 0);
    putInt(SharedPreferenceConstants.COMPLETED_CHALLENGES_COUNT, 0);
    putDate(SharedPreferenceConstants.CREATED_AT, null);
    putString(SharedPreferenceConstants.FACEBOOK_ID, "");
    putString(SharedPreferenceConstants.USER_ID, "");
    putString(SharedPreferenceConstants.USER_NAME, "");
    putString(SharedPreferenceConstants.USER_PICTURE_URL, "");
    putString(SharedPreferenceConstants.USER_TIMEZONE, "");
    putDate(SharedPreferenceConstants.UPDATED_AT, null);
    putDouble(SharedPreferenceConstants.TODAY_PROGRESS_PERCENT, 0);
    putInt(SharedPreferenceConstants.COMPLETED_DAYS_COUNT, 0);
    putInt(SharedPreferenceConstants.TODAY_STEPS_COUNT, 0);

    putString(SharedPreferenceConstants.ACCESS_TOKEN, "");

    putString(SharedPreferenceConstants.GOOGLE_ID, "");

    putString(SharedPreferenceConstants.CACHED_FRIENDS,new Gson().toJson(null));
    putInt(SharedPreferenceConstants.COUNT_FB_MEMBERS, 0);

    putString(SharedPreferenceConstants.GOOGLE_TOKEN, "none");
    putString(SharedPreferenceConstants.REFRESH_TOKEN, "");
    putInt(SharedPreferenceConstants.SHOWED_STEPS, 0);
    putString(SharedPreferenceConstants.SUBSCRIPTIONS_JSON, "");
  }

  public static int getCompletedDaysCount() {
    return getInt(SharedPreferenceConstants.COMPLETED_DAYS_COUNT, 0);
  }

  public static boolean getOnLaunch() {
    return getBoolean("onLaunch", true);
  }

  public static void setOnLaunch(boolean onLaunch) {
    putBoolean("onLaunch", onLaunch);
  }

  public static String getUserId() {
    return getString(SharedPreferenceConstants.USER_ID, "");
  }

  public static Date getCreatedAt() {
    return getDate(SharedPreferenceConstants.CREATED_AT);
  }

  public static void saveBrokenGlassId(int id) {
    putInt(SharedPreferenceConstants.BROKEN_GLASS_ID, id);
  }

  public static void saveBrokenGlassIds(int[] id) {
    for (int i = 0; i < id.length; i++)
      putInt(SharedPreferenceConstants.BROKEN_GLASS_ID + "_" + i, id[i]);
  }

  public static int getBrokenGlassId() {
    return getInt(SharedPreferenceConstants.BROKEN_GLASS_ID, -1);
  }

  public static int[] getBrokenGlassIds() {
    int[] brokenGlassIds = new int[7];
    for (int i = 0; i < 7; i++)
      brokenGlassIds[i] = getInt(SharedPreferenceConstants.BROKEN_GLASS_ID + "_" + i, -1);
    return brokenGlassIds;
  }

  public static int getActiveTrackingsCreated() {
    return getInt(SharedPreferenceConstants.ACTIVE_TRACKINGS_CREATED, 0);
  }

  public static void saveActiveTrackingsCreated(int count) {
    putInt(SharedPreferenceConstants.ACTIVE_TRACKINGS_CREATED, count);
  }

  public static void saveShowedSteps(int steps) {
    Timber.e("Save Showed Steps " + steps);
    putInt(SharedPreferenceConstants.SHOWED_STEPS, steps);
  }

  public static int getShowedSteps() {
    String userId = getUserId();
    return getInt(SharedPreferenceConstants.SHOWED_STEPS, 0);
  }

  public static void saveStats(Stats[] stats) {
    String userId = SharedPreferenceHelper.getUserId();
    Arrays.sort(stats, Stats.BY_DATE_DESC);
    for (int i = 0; i < Constants.REPORTED_DAYS; i++) {
      SharedPreferenceHelper.saveStatsToPrefs(userId, stats[i]);
    }
  }

  public static void saveBaseline(int baseline) {
    putInt("baseline", baseline);
  }

  public static int getBaseline() {
    return getInt("baseline", 0);
  }

  public static void shiftStatsOnOneDay(String userId) {

    putBoolean("new_day", true);
    ReportStats[] oldStats = getReportStats(userId);
    ReportStats[] newStats = new ReportStats[oldStats.length];

    System.arraycopy(oldStats, 0, newStats, 1, oldStats.length - 1);
    ReportStats emptyStats = new ReportStats();
    emptyStats.setDate(StringHelper.getReportStepsDate(new Date()));
    newStats[0] = emptyStats;

    for (int i = 0; i < newStats.length; i++) {
      ReportStats stats = newStats[i];
      putString(userId + SharedPreferenceConstants.STATS_DATE + "_" + i, stats.getDate());
      putInt(userId + SharedPreferenceConstants.STATS_STEPS_COUNT + "_" + i, stats.getStepsCount());
      Log.d("Helper global", "" + stats.getDate() + " - " + stats.getStepsCount());
    }
  }

  public static int getStepsForTheDate(String date) {
    return getInt(getUserId() + "_" + date + "_", 0);
  }

  public static ReportStats[] getReportStats(String userId) {
    List<ReportStats> reportStats = new ArrayList<>(Constants.REPORTED_DAYS);

    List<String> dates = DeviceHelper.getLast21Days();

    for (int i = 0; i < Constants.REPORTED_DAYS; i++) {
      ReportStats stats = new ReportStats();
      int steps = getInt(userId + "_" + dates.get(i) + "_", 0);

      stats.setStepsCount(steps);
      stats.setDate(dates.get(i));
      reportStats.add(stats);
    }

    return reportStats.toArray(new ReportStats[reportStats.size()]);
  }

  private static void saveStatsToPrefs(String userId, Stats stats) {
    int stepsInPrefs = getInt(userId + "_" + stats.getDate() + "_", stats.getStepsCount());

    //putInt(userId + "_" + stats.getDate()+"_", Math.max(stepsInPrefs,stats.getStepsCount()));
  }

  public static int getStatsTodaySteps(String userId) {
    return getInt(userId + SharedPreferenceConstants.STATS_STEPS_COUNT + "_" + 0, 0);
  }

  public static void saveStatsTodaySteps(String userId, int steps) {
    putInt(userId + SharedPreferenceConstants.STATS_STEPS_COUNT + "_" + 0, steps);
  }

  public static void saveFbId(String fbId) {
    putString("fb_id", fbId);
    putString("facebook_id", fbId);
  }

  public static String getFbId() {
    return getString("fb_id", "none");
  }

  public static void saveVkId(String id) {
    putString("vk_id", id);
  }

  public static String getVkId() {
    return getString("vk_id", "none");
  }

  public static void saveAvatar(String pictureUrl) {
    putString(SharedPreferenceConstants.USER_PICTURE_URL, pictureUrl);
  }

  public static String getAvatar() {
    return getString(SharedPreferenceConstants.USER_PICTURE_URL, "");
  }

  public static String getEmail() {
    return getString("email", "");
  }

  public static void saveEmail(String email) {
    putString("email", email);
  }

  public static void saveUserName(String userName) {
    putString(SharedPreferenceConstants.USER_NAME, userName.trim());
  }

  public static String getUserName() {
    return getString(SharedPreferenceConstants.USER_NAME, "").trim();
  }

  public static String getAccessToken() {
    return getString(SharedPreferenceConstants.ACCESS_TOKEN, "");
  }

  public static void clearFBDate() {
    putString(SharedPreferenceConstants.FB_ACCESS_TOKEN, "none");
    putDate(SharedPreferenceConstants.FB_EXPIRED_TOKEN_DATE, null);
    saveFbId("none");
  }

  public static void saveFBAccessToken(String token) {
    putString(SharedPreferenceConstants.FB_ACCESS_TOKEN, token);
  }

  public static void saveFBExipredTokenDate(Date date) {
    putDate(SharedPreferenceConstants.FB_EXPIRED_TOKEN_DATE, date);
  }

  public static void saveGCMToken(String token) {
    putString(SharedPreferenceConstants.GCM_TOKEN, token);
  }

  public static String getGCMToken() {
    return getString(SharedPreferenceConstants.GCM_TOKEN, "");
  }

  public static void saveCaptureCameraUri(String uri) {
    putString(SharedPreferenceConstants.CAPTURE_CAMERA_URI, uri);
  }

  public static String getCaptureCameraUri() {
    return getString(SharedPreferenceConstants.CAPTURE_CAMERA_URI, "");
  }

  public static void saveCaptureCameraPath(String path) {
    putString(SharedPreferenceConstants.CAPTURE_CAMERA_PATH, path);
  }

  public static String getCaptureCameraPath() {
    return getString(SharedPreferenceConstants.CAPTURE_CAMERA_PATH, "");
  }

  public static void saveIsNeedStartStepDetectorService(boolean isStart) {
    putBoolean(SharedPreferenceConstants.IS_NEED_START_STEP_DETECTOR_AFTER_REBOOT, isStart);
  }

  public static boolean isNeedStartStepDetectorService() {
    return getBoolean(SharedPreferenceConstants.IS_NEED_START_STEP_DETECTOR_AFTER_REBOOT, false);
  }

  static boolean isFirstLogin() {
    return getBoolean(SharedPreferenceConstants.IS_FIRST_LOGIN, true);
  }

  static void saveIsFirstLogin(boolean isFirst) {
    putBoolean(SharedPreferenceConstants.IS_FIRST_LOGIN, isFirst);
  }

  public static String getTodayDate() {
    String date = getString("todayDate", "0");
    return date;
  }

  public static void setTodayDate(String date) {
    putString("todayDate", date);
  }

  public static String getTodayDateForShowedSteps() {
    String date = getString("todayDateForShowedSteps", "0");
    return date;
  }

  public static void setTodayDateForShowedSteps(String date) {
    putString("todayDateForShowedSteps", date);
  }

  public static boolean isFirstLaunchOfTheDay(String userId) {
    DateFormat dfDate = new SimpleDateFormat("yyyy-MM-dd");
    String date = dfDate.format(Calendar.getInstance().getTime());
    if (getTodayDate().equals(date)) {
      //System.out.println("DAAAAAATE IN SETTINGS" + getTodayDate());
      //System.out.println("DAAAAAATE FALSE" + date);
      //Log.d("IS FIRST LAUNCH TODAY", "NO" + false);
      return false;
    }
    //Log.d("IS FIRST LAUNCH TODAY", "YES" + true);
    //System.out.println("DAAAAAATE TRUE" + date);
    setFirstLaunchOfTheDay(true);
    return true;
  }

  public static void setFirstLaunchOfTheDay(boolean firstLaunch) {
    putBoolean("new_day", firstLaunch);
  }

  public static boolean introIsShown() {
    return getBoolean(SharedPreferenceConstants.INTRO_IS_SHOWN, false);
  }

  public static boolean isChatEnabled() {
    return getBoolean("isChatEnabled", false);
  }

  public static boolean isFirstLaunchForStats() {
    return getBoolean("firstrunforstats", true);
  }

  public static void setFirstLaunchForStats(boolean firstLaunch) {
    putBoolean("firstrunforstats", firstLaunch);
  }

  public static boolean isFirstLaunch() {
    return getBoolean("firstrun", true);
  }

  public static void setFirstLaunch(boolean firstLaunch) {
    putBoolean("firstrun", firstLaunch);
  }

  public static void showedIntro() {
    putBoolean(SharedPreferenceConstants.INTRO_IS_SHOWN, true);
  }

  public static void saveCacheTodaySteps(int cacheSteps) {
    putInt(SharedPreferenceConstants.LAST_DAY_CACHE_STEPS, cacheSteps);
  }

  public static int getCacheTodaySteps() {
    return getInt(SharedPreferenceConstants.LAST_DAY_CACHE_STEPS, 0);
  }

  public static ReportStats[] getReportStatsLocal(String userId) {
    List<ReportStats> reportStats = new ArrayList<>(Constants.REPORTED_DAYS);

    List<String> dates = DeviceHelper.getLast21Days();

    for (int i = 0; i < Constants.REPORTED_DAYS; i++) {
      ReportStats stats = new ReportStats();
      int steps = getInt(userId + "_" + dates.get(i) + "_", 0);
      stats.setStepsCount(steps);
      stats.setDate(dates.get(i));
      reportStats.add(stats);
    }

    return reportStats.toArray(new ReportStats[reportStats.size()]);
  }

  public static void saveReportStatsLocal(ReportStats[] reportStats) {
    String userId = SharedPreferenceHelper.getUserId();
    Arrays.sort(reportStats, ReportStats.BY_DATE_DESC);
    for (int i = 0; i < Constants.REPORTED_DAYS; i++) {
      if (i < reportStats.length) {
        SharedPreferenceHelper.saveStatsLocal(i, userId, reportStats[i]);
      }
    }
  }

  private static void saveStatsLocal(int position, String userId, ReportStats reportStats) {
    putInt(userId + "_" + reportStats.getDate() + "_", reportStats.getStepsCount());
  }

  public static void shiftStatsLocalOnOneDay(String userId) {
    ReportStats[] oldStats = getReportStatsLocal(userId);
    ReportStats[] newStats = new ReportStats[oldStats.length];

    System.arraycopy(oldStats, 0, newStats, 1, oldStats.length - 1);
    ReportStats emptyStats = new ReportStats();
    Date dt = StringHelper.convertReportStatDate(newStats[1].getDate());
    Calendar c = Calendar.getInstance();
    c.setTime(dt);
    c.add(Calendar.DATE, 1);
    dt = c.getTime();
    emptyStats.setDate(StringHelper.getReportStepsDate(dt));
    newStats[0] = emptyStats;

    for (int i = 0; i < newStats.length; i++) {
      ReportStats stats = newStats[i];
      putString(userId + SharedPreferenceConstants.STATS_DATE_LOCAL + "_" + i, stats.getDate());
      putInt(userId + SharedPreferenceConstants.STATS_STEPS_COUNT_LOCAL + "_" + i,
          stats.getStepsCount());
      Log.d("Helper local", "" + stats.getDate() + " - " + stats.getStepsCount());
    }
  }

  public static void setLocale(String locale) {
    putString("locale", locale);
  }

  public static String getLocale(String defaultValue) {
    String locale = getString("locale", defaultValue);
    return locale;
  }

  public final static void saveLastStepCounterValue(int value) {
    putInt(SharedPreferenceConstants.STEP_COUNTER_LAST_VALUE, value);
    //   putLong(SharedPreferenceConstants.STEP_COUNTER_TIMESTAMP_LAST_VALUE, System.currentTimeMillis());
  }

  public final static int getLastStepCounterValue() {
    return getInt(SharedPreferenceConstants.STEP_COUNTER_LAST_VALUE, 0);
  }

  public final static long getTimestampLastStepCounterValue() {
    return getLong(SharedPreferenceConstants.STEP_COUNTER_TIMESTAMP_LAST_VALUE, 0);
  }

  public static boolean getOTPStatus() {
    return getBoolean(SharedPreferenceConstants.OTP_STATUS, false);
  }

  public static void setOTPStatus(boolean otpStatus) {
    putBoolean(SharedPreferenceConstants.OTP_STATUS, otpStatus);
  }

  public static String getIMEI() {
    Timber.e(getString(SharedPreferenceConstants.IMEI, "No IMEI"));
    return getString(SharedPreferenceConstants.IMEI, "No IMEI");
  }

  public static void setIMEI(String deviceId) {
    putString(SharedPreferenceConstants.IMEI, deviceId);
  }

  public static boolean getIsMentor() {
    return getBoolean("isMentor", false);
  }

  public static void setIsMentor(boolean isMentor) {
    putBoolean("isMentor", isMentor);
  }

  public static String getBaseUrl() {
    Timber.e("getBaseUrl" + getString(SharedPreferenceConstants.BASE_URL, Config.SERVER_URL));
    if (getString(SharedPreferenceConstants.BASE_URL, Config.SERVER_URL).isEmpty()) {
      return Config.SERVER_URL;
    } else {
      return getString(SharedPreferenceConstants.BASE_URL, Config.SERVER_URL);
    }
  }

  public static void saveBaseUrl(String url) {
    if (!url.isEmpty()) {
      Timber.e("saveBaseUrl" + url);
      putString(SharedPreferenceConstants.BASE_URL, url);
    }
  }

  public static void saveGroupsLimit(String groupsLimit) {
    if (groupsLimit == null || groupsLimit.isEmpty()) {
      putString(SharedPreferenceConstants.GROUPS_LIMIT, "3");
    } else {
      putString(SharedPreferenceConstants.GROUPS_LIMIT, groupsLimit);
    }
  }

  public static String getGroupsLimit() {
    return getString(SharedPreferenceConstants.GROUPS_LIMIT, "3");
  }

  public static void setEventGroupWithXNewUsersDone() {
    putBoolean(SharedPreferenceConstants.EVENT_GROUP_WITH_X_NEW_USERS_DONE, true);
  }

  public static boolean isEventGroupWithXNewUsersDone() {
    return getBoolean(SharedPreferenceConstants.EVENT_GROUP_WITH_X_NEW_USERS_DONE, false);
  }

  public static void setEventXStepsDone(Integer numValue) {
    Timber.e("setEventXStepsDone " + numValue);
    putBoolean(SharedPreferenceConstants.EVENT_X_STEPS_DONE + numValue, true);
  }

  public static void disableEventXStepsDone(Integer numValue) {
    putBoolean(SharedPreferenceConstants.EVENT_X_STEPS_DONE + numValue, false);
  }

  public static boolean isEventXStepsDone(Integer numValue) {
    Timber.e("isEventXStepsDone " + numValue);
    return getBoolean(SharedPreferenceConstants.EVENT_X_STEPS_DONE + numValue, false);
  }

  public static void setCampaignParam(String key, String value) {
    putString(key, value);
  }

  public static String getCampaignParam(String param) {
    return getString(param, "");
  }

  public static String getCampaign() {
    return getString("campaign", "");
  }

  public static void setCampaign(String campaign) {
    putString("campaign", campaign);
  }

  public static void saveAorB(Integer aOrB) {
    putInt(SharedPreferenceConstants.A_OR_B, aOrB);
  }

  public static int getAorB() {
    return getInt(SharedPreferenceConstants.A_OR_B, 0);
  }

  public static void blockRules() {
    Timber.e("blockRules");
    putBoolean(SharedPreferenceConstants.BLOCK_RULES, true);
  }

  public static void unBlockRules() {
    Timber.e("blockRules!");

    putBoolean(SharedPreferenceConstants.BLOCK_RULES, false);
  }

  public static boolean isRulesBlocked() {
    return getBoolean(SharedPreferenceConstants.BLOCK_RULES, true);
  }

  public static void unBlockGetAnimsByName() {
    putBoolean(SharedPreferenceConstants.BLOCK_GET_ANIMS_BY_NAME, false);
  }

  public static void blockGetAnimsByName() {
    putBoolean(SharedPreferenceConstants.BLOCK_GET_ANIMS_BY_NAME, true);
  }

  public static boolean isBlockedGetAnimationByName() {
    return getBoolean(SharedPreferenceConstants.BLOCK_GET_ANIMS_BY_NAME, false);
  }

  public static void saveNumberOfContacts(int size) {
    putInt(SharedPreferenceConstants.CONTACTS_COUNT, size);
  }

  public static int getNumberOfContacts() {
    return getInt(SharedPreferenceConstants.CONTACTS_COUNT, 0);
  }

  public static void putMentorEntity(String s) {
    putString(SharedPreferenceConstants.CACHED_MENTORS, s);
  }

  public static List<MentorEntity> getCachedMentors() {
    Type type = new TypeToken<List<MentorEntity>>() {
    }.getType();
    return new Gson().fromJson(getString(SharedPreferenceConstants.CACHED_MENTORS, ""), type);
  }

  public static void saveNameOfCurrentAnim(String animName) {
    putString(SharedPreferenceConstants.NAME_OF_CURRENT_ANIM, animName);
  }

  public static String getNameOfCurrentAnim() {
    return getString(SharedPreferenceConstants.NAME_OF_CURRENT_ANIM, "NoNe anim");
  }

  public static boolean isCanLaunchLastAnim() {
    return getBoolean(SharedPreferenceConstants.CAN_LAUNCH_LAST_ANIM, true);
  }

  public static void canLaunchLastAnim(boolean b) {
    putBoolean(SharedPreferenceConstants.CAN_LAUNCH_LAST_ANIM, b);
  }

  public static void saveDataForMainActivity(DataForMainActivity dataForMainActivity) {
    putString(SharedPreferenceConstants.DATA_FOR_MAIN_ACTIVITY,
        new Gson().toJson(dataForMainActivity));
  }

  public static DataForMainActivity getSavedDataForMainActivity() {
    return new Gson().fromJson(getString(SharedPreferenceConstants.DATA_FOR_MAIN_ACTIVITY, ""),
        DataForMainActivity.class);
  }

  public static void saveGoogleToken(String gToken){
    putString(SharedPreferenceConstants.GOOGLE_TOKEN, gToken);
  }

  public static String getGoogleToken() {
    return getString(SharedPreferenceConstants.GOOGLE_TOKEN, "none");
  }

  public static void saveGoogleId(String userId) {
    putString(SharedPreferenceConstants.GOOGLE_ID, userId);
  }

  public static String getGoogleId() {
    return getString(SharedPreferenceConstants.GOOGLE_ID, "none");
  }

  public static void saveCountOfMembersFb(String countOfFbMembers){
    putInt(SharedPreferenceConstants.COUNT_FB_MEMBERS, Integer.valueOf(countOfFbMembers));
  }

  public static int getCountOfMembersFb(){
    return getInt(SharedPreferenceConstants.COUNT_FB_MEMBERS,0);
  }

  public static void saveCountOfMembersPh(String countOfPhMembers){
    putInt(SharedPreferenceConstants.COUNT_PH_MEMBERS, Integer.valueOf(countOfPhMembers));
  }

  public static int getCountOfMembersPh(){
    return getInt(SharedPreferenceConstants.COUNT_PH_MEMBERS,0);
  }

  public static void saveCountOfMembersVk(String countOfVkMembers){
    putInt(SharedPreferenceConstants.COUNT_VK_MEMBERS, Integer.valueOf(countOfVkMembers));
  }

  public static int getCountOfMembersVk(){
    return getInt(SharedPreferenceConstants.COUNT_VK_MEMBERS,0);
  }

  public static void cacheFriends(List<FacebookFriend> friends) {
    Timber.e("cacheFriends "+friends.size());
    putString(SharedPreferenceConstants.CACHED_FRIENDS,new Gson().toJson(friends));
  }

  public static List<FacebookFriend> getCachedFriends() {
    Type type = new TypeToken<List<FacebookFriend>>() {
    }.getType();
    return new Gson().fromJson(getString(SharedPreferenceConstants.CACHED_FRIENDS, ""), type);
  }

  public static boolean getRemoteLoggingEnabled() {
    return getBoolean("remote_logging_enabled", false);
  }

  public static void setRemoteLoggingEnabled(boolean b) {
    putBoolean("remote_logging_enabled", b);
  }
}
