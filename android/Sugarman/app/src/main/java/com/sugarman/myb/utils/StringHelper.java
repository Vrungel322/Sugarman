package com.sugarman.myb.utils;

import android.graphics.Typeface;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import com.sugarman.myb.constants.Constants;
import com.sugarman.myb.constants.NotificationMessageType;
import com.sugarman.myb.ui.views.CustomFontSpan;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class StringHelper {

  private static final String TAG = StringHelper.class.getName();

  private static final String STATS_DATE_FORMAT = "yyyy-MM-dd";

  private static final String REPORT_STATS_DATE_FORMAT = "yyyy-MM-dd";

  public static final String SERVER_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";

  private static final String TAG_TITLE_OPEN = "<title>";
  private static final String TAG_TITLE_CLOSE = "</title>";
  private static final String REG_EXP_API_ERROR_TITLE =
      TAG_TITLE_OPEN + "[a-zA-Z0-9 ]*" + TAG_TITLE_CLOSE;

  private static final String TAG_P_OPEN = "<p>";
  private static final String TAG_P_CLOSE = "</p>";
  private static final String REG_EXP_API_ERROR_MESSAGE =
      TAG_P_OPEN + "[a-zA-Z .(),']*" + TAG_P_CLOSE;

  private static final DateFormat STATS_DATE_FORMATTER =
      new SimpleDateFormat(STATS_DATE_FORMAT, Locale.getDefault());

  private static final DateFormat API_DATE_FORMATTER =
      new SimpleDateFormat(SERVER_DATE_FORMAT, Locale.getDefault());

  private static final DateFormat REPORT_STATS_DATE_FORMATTER =
      new SimpleDateFormat(REPORT_STATS_DATE_FORMAT, Locale.getDefault());

  private StringHelper() {
    // only static methods and fields
  }

  public static long getStatsTimestamp(String date) {
    long timestamp = -1;
    if (!TextUtils.isEmpty(date)) {
      try {
        Date parsed = STATS_DATE_FORMATTER.parse(date);
        if (parsed != null) {
          timestamp = parsed.getTime();
        }
      } catch (ParseException e) {
        Log.e(TAG, "failure parse date: " + date + " expected format: " + STATS_DATE_FORMAT, e);
      }
    }
    return timestamp;
  }

  public static String getApiErrorTitle(String content) {
    String titleWithTags = getValueByRegExp(content, REG_EXP_API_ERROR_TITLE);
    return titleWithTags.replace(TAG_TITLE_OPEN, "").replace(TAG_TITLE_CLOSE, "");
  }

  public static String getApiErrorMessage(String content) {
    String messageWithTags = getValueByRegExp(content, REG_EXP_API_ERROR_MESSAGE);
    return messageWithTags.replace(TAG_P_OPEN, "").replace(TAG_P_CLOSE, "");
  }

  private static String getValueByRegExp(String content, String regExp) {
    Pattern p = Pattern.compile(regExp);
    Matcher m = p.matcher(content);
    return m.find() ? m.group() : "";
  }

  public static SpannableStringBuilder getNotificationMessageSpannable(int type, String[] flags,
      String message, Typeface bold, Typeface regular) {
    SpannableStringBuilder builder;
    String[] itemFlags;
    String flag1;
    String flag2;

    switch (type) {
      case NotificationMessageType.GROUP_NAME_IN_HOUR: // expected: Group_name will start the challenge in an hour, stay tuned!
      case NotificationMessageType.GROUP_NAME_GOOD_LUCK: // expected: Group_name group had start the challenge.Good luck!
      case NotificationMessageType.GROUP_NAME_HAS_FAILED: // expected: Group_name has failed the challenge
      case NotificationMessageType.USER_NAME_HAS_POKED_YOU: // expected: User_name has poked you!
      case NotificationMessageType.IS_UNABLE_TO_START: // expected: Group_name is unable to start 10000 Steps Per Day due to lack of users
        builder = getSingleStartBoldSpan(message, flags[type], bold, regular);
        break;
      case NotificationMessageType.USER_NAME_INVITED: // expected: User_name has invited you to Group_name group
      case NotificationMessageType.USER_NAME_JOINED: // expected: User_name has joined Group_name group
      case NotificationMessageType.CREATOR_NAME_APPROVED: // expected Creator_name has approved your request to join Group_name group
      case NotificationMessageType.USER_NAME_REQUESTED: // expected: User_name has requested to join Group_name group
      case NotificationMessageType.PINGED_YOU_TO_MYB: // expected: User_name pinged you to MYB on Group_name group
        itemFlags = flags[type].split(Constants.IN_APP_NOTIFICATION_MESSAGE_DIVIDER);
        flag1 = itemFlags[0];
        flag2 = itemFlags[1];
        builder = getDoubleBoldSpan(message, flag1, flag2, bold, regular);
        break;
      case NotificationMessageType.CONGRATS: // expected: Congrats! Your group (Group_name) has finished the challenge!
      case NotificationMessageType.DAILY_SUGARMAN: //expected: You are the daily sugarman in (Group_name) group
      case NotificationMessageType.ONE_MORE_DAY_TO_ADD_FRIENDS: // expected: You have one more day to add friends to your group_name group
      case NotificationMessageType.INVITATION_NO_AVAILABLE: // expected: Your invitation to group_name is no longer available
        itemFlags = flags[type].split(Constants.IN_APP_NOTIFICATION_MESSAGE_DIVIDER);
        flag1 = itemFlags[0];
        flag2 = itemFlags[1];
        builder = getSingleCenterBoldSpan(message, flag1, flag2, bold, regular);
        break;
      default:
        Log.d(TAG, "not supported notification type: " + type + " message: " + message);
        builder = new SpannableStringBuilder(message);
        break;
    }
    return builder;
  }

  private static SpannableStringBuilder getSingleStartBoldSpan(String message, String flag,
      Typeface bold, Typeface regular) {
    // bold regular
    SpannableStringBuilder builder = new SpannableStringBuilder(message);
    int regularStart = message.indexOf(flag);

    builder.setSpan(new CustomFontSpan("", bold), 0, regularStart,
        Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
    builder.setSpan(new CustomFontSpan("", regular), regularStart, message.length(),
        Spanned.SPAN_EXCLUSIVE_INCLUSIVE);

    return builder;
  }

  private static SpannableStringBuilder getSingleCenterBoldSpan(String message, String flag1,
      String flag2, Typeface bold, Typeface regular) {
    // regular bold regular
    SpannableStringBuilder builder = new SpannableStringBuilder(message);
    int regularStartSecond = message.indexOf(flag2);
    int boldStart = flag1.length();

    builder.setSpan(new CustomFontSpan("", regular), 0, boldStart,
        Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
    builder.setSpan(new CustomFontSpan("", bold), boldStart, regularStartSecond,
        Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
    builder.setSpan(new CustomFontSpan("", regular), regularStartSecond, message.length(),
        Spanned.SPAN_EXCLUSIVE_INCLUSIVE);

    return builder;
  }

  public static SpannableStringBuilder getDoubleBoldSpan(String message, String flag1, String flag2,
      Typeface bold, Typeface regular) {
    // bold regular bold regular
    SpannableStringBuilder builder = new SpannableStringBuilder(message);
    int regularStartFirst = message.indexOf(flag1);
    int regularStartSecond = message.indexOf(flag2);
    int secondBoldStart = regularStartFirst + flag1.length();

    //       builder.setSpan(new CustomFontSpan("", bold), 0, regularStartFirst, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
    //       builder.setSpan(new CustomFontSpan("", regular), regularStartFirst, secondBoldStart, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
    //       builder.setSpan(new CustomFontSpan("", bold), secondBoldStart, regularStartSecond, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
    //       builder.setSpan(new CustomFontSpan("", regular), regularStartSecond, message.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);

    return builder;
  }

  public static String convertApiDate(Date date) {
    return API_DATE_FORMATTER.format(date);
  }

  public static Date convertApiDate(String date, String timezone) {
    Date parsedDate = null;
    if (!TextUtils.isEmpty(date)) {
      if (TextUtils.isEmpty(timezone)) {
        timezone = Constants.DEF_TIMEZONE;
      }

      API_DATE_FORMATTER.setTimeZone(TimeZone.getTimeZone(timezone));

      try {
        parsedDate = API_DATE_FORMATTER.parse(date);
      } catch (ParseException e) {
        Log.e(TAG, "failure parse date", e);
      }
    }
    return parsedDate;
  }

  public static String getReportStepsDate(Date date) {
    String reportDate = "";

    if (date != null) {
      //    reportDate = REPORT_STATS_DATE_FORMATTER.format(date);
      reportDate = STATS_DATE_FORMATTER.format(date);
    }

    return reportDate;
  }

  public static Date convertReportStatDate(String date) {
    Date reportDate = null;
    if (!TextUtils.isEmpty(date)) {
      try {
        reportDate = REPORT_STATS_DATE_FORMATTER.parse(date);
      } catch (ParseException e) {
        Log.d(TAG, "Failure parse report date", e);
      }
    }

    if (reportDate == null) {
      try {
        reportDate = STATS_DATE_FORMATTER.parse(date);
      } catch (ParseException e) {
        Log.e(TAG, "Failure parse report date", e);
      }
    }

    return reportDate;
  }

  public static Date convertReportStatDateWithoutTimeZone(String date) {
    Date reportDate = null;
    if (!TextUtils.isEmpty(date)) {
      try {
        REPORT_STATS_DATE_FORMATTER.setTimeZone(TimeZone.getTimeZone("GMT"));
        reportDate = REPORT_STATS_DATE_FORMATTER.parse(date);
      } catch (ParseException e) {
        Log.d(TAG, "Failure parse report date", e);
      }
    }

    if (reportDate == null) {
      try {
        STATS_DATE_FORMATTER.setTimeZone(TimeZone.getTimeZone("GMT"));
        reportDate = STATS_DATE_FORMATTER.parse(date);
      } catch (ParseException e) {
        Log.e(TAG, "Failure parse report date", e);
      }
    }

    return reportDate;
  }

  public static int getNotificationMessageType(String[] flags, String message) {
    int type = NotificationMessageType.UNKNOWN;

    boolean isTypeDetected;

    for (int i = 0; i < flags.length; i++) {
      String text = flags[i];
      String[] itemFlags = text.split(Constants.IN_APP_NOTIFICATION_MESSAGE_DIVIDER);
      isTypeDetected = true;
      for (String flag : itemFlags) {
        isTypeDetected &= message.contains(flag);
      }

      if (isTypeDetected) {
        type = NotificationMessageType.IDS[i];
        break;
      }
    }

    return type;
  }
}
