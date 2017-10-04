package com.sugarman.myb.utils;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import com.sugarman.myb.api.models.responses.Member;
import com.sugarman.myb.api.models.responses.Tracking;
import com.sugarman.myb.api.models.responses.me.invites.Invite;
import com.sugarman.myb.api.models.responses.me.notifications.Notification;
import com.sugarman.myb.api.models.responses.me.requests.Request;
import com.sugarman.myb.api.models.responses.me.stats.Stats;
import com.sugarman.myb.constants.Constants;
import java.util.Arrays;

public abstract class IntentExtractorHelper {

  private static final String TAG = IntentExtractorHelper.class.getName();

  private IntentExtractorHelper() {
    // only static methods and fields
  }

  public static Invite[] getInvites(Intent intent) {
    Invite[] invites = new Invite[0];

    if (intent != null && intent.hasExtra(Constants.INTENT_MY_INVITES)) {
      Parcelable[] parcelableArray = intent.getParcelableArrayExtra(Constants.INTENT_MY_INVITES);
      if (parcelableArray != null) {
        invites = Arrays.copyOf(parcelableArray, parcelableArray.length, Invite[].class);
      } else {
        Log.e(TAG, "my actualInvites array is null");
      }
    } else {
      Log.e(TAG, "my actualInvites are absent in intent extra");
    }

    return invites;
  }

  public static Request[] getRequests(Intent intent) {
    Request[] requests = new Request[0];

    if (intent != null && intent.hasExtra(Constants.INTENT_MY_REQUESTS)) {
      Parcelable[] parcelableArray = intent.getParcelableArrayExtra(Constants.INTENT_MY_REQUESTS);
      if (parcelableArray != null) {
        requests = Arrays.copyOf(parcelableArray, parcelableArray.length, Request[].class);
      } else {
        Log.e(TAG, "my actualRequests array is null");
      }
    } else {
      Log.e(TAG, "my actualRequests are absent in intent extra");
    }

    return requests;
  }

  public static Tracking[] getTrackings(Intent intent) {
    Tracking[] trackings = new Tracking[0];

    if (intent != null && intent.hasExtra(Constants.INTENT_MY_TRACKINGS)) {
      Parcelable[] parcelableArray = intent.getParcelableArrayExtra(Constants.INTENT_MY_TRACKINGS);
      if (parcelableArray != null) {
        trackings = Arrays.copyOf(parcelableArray, parcelableArray.length, Tracking[].class);
      } else {
        Log.e(TAG, "my tracking array is null");
      }
    } else {
      Log.e(TAG, "my tracking are absent in intent extra");
    }

    return trackings;
  }

  public static Notification[] getNotifications(Intent intent) {
    Notification[] notifications = new Notification[0];

    if (intent != null && intent.hasExtra(Constants.INTENT_MY_NOTIFICATIONS)) {
      Parcelable[] parcelableArray =
          intent.getParcelableArrayExtra(Constants.INTENT_MY_NOTIFICATIONS);
      if (parcelableArray != null) {
        notifications =
            Arrays.copyOf(parcelableArray, parcelableArray.length, Notification[].class);
      } else {
        Log.e(TAG, "my actualNotifications array is null");
      }
    } else {
      Log.e(TAG, "my actualNotifications are absent in intent extra");
    }

    return notifications;
  }

  public static Member[] getMembers(Intent intent) {
    Member[] members = new Member[0];

    if (intent != null && intent.hasExtra(Constants.INTENT_MEMBERS)) {
      Parcelable[] parcelableArray = intent.getParcelableArrayExtra(Constants.INTENT_MEMBERS);
      if (parcelableArray != null) {
        members = Arrays.copyOf(parcelableArray, parcelableArray.length, Member[].class);
      } else {
        Log.e(TAG, "my members array is null");
      }
    } else {
      Log.e(TAG, "my members are absent in intent extra");
    }

    return members;
  }

  public static String getTrackingId(Intent intent) {
    String trackingId = "";

    if (intent != null && intent.hasExtra(Constants.INTENT_TRACKING_ID)) {
      trackingId = intent.getStringExtra(Constants.INTENT_TRACKING_ID);
    } else {
      Log.e(TAG, "tracking id are absent in intent extra");
    }

    return trackingId;
  }

  public static String getGroupName(Intent intent) {
    String groupName = "";

    if (intent != null && intent.hasExtra(Constants.INTENT_GROUP_NAME)) {
      groupName = intent.getStringExtra(Constants.INTENT_GROUP_NAME);
    } else {
      Log.e(TAG, "tracking id are absent in intent extra");
    }

    return groupName;
  }

  public static String getGroupPicture(Intent intent) {
    String groupPicture = "";

    if (intent != null && intent.hasExtra(Constants.INTENT_GROUP_PICTURE)) {
      groupPicture = intent.getStringExtra(Constants.INTENT_GROUP_PICTURE);
    } else {
      Log.e(TAG, "tracking id are absent in intent extra");
    }

    return groupPicture;
  }

  public static String getTrackingIdFromFcm(Intent intent) {
    String trackingId = "";

    if (intent != null && intent.hasExtra(Constants.INTENT_FCM_TRACKING_ID)) {
      trackingId = intent.getStringExtra(Constants.INTENT_FCM_TRACKING_ID);
    } else {
      Log.e(TAG, "tracking id from fcm are absent in intent extra");
    }

    return trackingId;
  }

  public static Member[] getPendings(Intent intent) {
    Member[] members = new Member[0];

    if (intent != null && intent.hasExtra(Constants.INTENT_PENDINGS)) {
      Parcelable[] parcelableArray = intent.getParcelableArrayExtra(Constants.INTENT_PENDINGS);
      if (parcelableArray != null) {
        members = Arrays.copyOf(parcelableArray, parcelableArray.length, Member[].class);
      } else {
        Log.e(TAG, "pending array is null");
      }
    } else {
      Log.e(TAG, "pending are absent in intent extra");
    }

    return members;
  }

  public static Tracking getTracking(Intent intent) {
    Tracking tracking = null;

    if (intent != null && intent.hasExtra(Constants.INTENT_TRACKING)) {
      tracking = intent.getParcelableExtra(Constants.INTENT_TRACKING);
    } else {
      Log.e(TAG, "tracking is absent in intent extra");
    }

    return tracking;
  }

  public static int getOpenActivityCode(Intent intent) {
    int activityCode = -1;
    if (intent != null && intent.hasExtra(Constants.INTENT_OPEN_ACTIVITY)) {
      activityCode = intent.getIntExtra(Constants.INTENT_OPEN_ACTIVITY, -1);
    }

    return activityCode;
  }

  public static Tracking getTracking(Bundle args) {
    Tracking tracking = null;
    if (args != null && args.containsKey(Constants.BUNDLE_TRACKING_ITEM)) {
      tracking = args.getParcelable(Constants.BUNDLE_TRACKING_ITEM);
    } else {
      Log.e(TAG, "item is absent");
    }

    return tracking;
  }

  public static int getTrackingPosition(Bundle args) {
    int position = -1;
    if (args != null && args.containsKey(Constants.BUNDLE_TRACKING_POSITION)) {
      position = args.getInt(Constants.BUNDLE_TRACKING_POSITION);
    } else {
      Log.e(TAG, "position is absent");
    }

    return position;
  }

  public static int getTrackingsCount(Bundle args) {
    int position = -1;
    if (args != null && args.containsKey(Constants.BUNDLE_TRACKINS_COUNT)) {
      position = args.getInt(Constants.BUNDLE_TRACKINS_COUNT);
    } else {
      Log.e(TAG, "total count is absent");
    }

    return position;
  }

  public static Notification[] getNotifications(Bundle args) {
    Notification[] notifications = new Notification[0];

    if (args != null && args.containsKey(Constants.BUNDLE_NOTIFICATIONS)) {
      Parcelable[] parcelableArray = args.getParcelableArray(Constants.BUNDLE_NOTIFICATIONS);
      if (parcelableArray != null) {
        notifications =
            Arrays.copyOf(parcelableArray, parcelableArray.length, Notification[].class);
      } else {
        Log.e(TAG, "my actualNotifications array is null");
      }
    } else {
      Log.e(TAG, "my actualNotifications are absent in bundle");
    }

    return notifications;
  }

  public static Stats[] getStats(Bundle args) {
    Stats[] stats = new Stats[0];
    if (args != null && args.containsKey(Constants.BUNDLE_MY_STATS)) {
      Parcelable[] parcelableArray = args.getParcelableArray(Constants.BUNDLE_MY_STATS);
      if (parcelableArray != null) {
        stats = Arrays.copyOf(parcelableArray, parcelableArray.length, Stats[].class);
      } else {
        Log.e(TAG, "stats array is null");
      }
    } else {
      Log.e(TAG, "stats array is absent");
    }

    return stats;
  }

  public static int getPosition(Bundle args) {
    int position = 0;
    if (args != null && args.containsKey(Constants.BUNDLE_POSITION)) {
      position = args.getInt(Constants.BUNDLE_POSITION);
    } else {
      Log.e(TAG, "position is absent");
    }

    return position;
  }
}

