package com.sugarman.myb.ui.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import com.sugarman.myb.App;
import com.sugarman.myb.R;
import com.sugarman.myb.adapters.NotificationsAdapter;
import com.sugarman.myb.api.clients.MarkNotificationClient;
import com.sugarman.myb.api.models.responses.me.notifications.Notification;
import com.sugarman.myb.constants.Config;
import com.sugarman.myb.constants.Constants;
import com.sugarman.myb.constants.NotificationMessageType;
import com.sugarman.myb.eventbus.events.ChallengeSelectedEvent;
import com.sugarman.myb.eventbus.events.NotificationRemovedEvent;
import com.sugarman.myb.eventbus.events.RefreshTrackingsEvent;
import com.sugarman.myb.listeners.ApiMarkNotificationListener;
import com.sugarman.myb.listeners.OnNotificationActionListener;
import com.sugarman.myb.models.NotificationItem;
import com.sugarman.myb.ui.activities.base.BaseActivity;
import com.sugarman.myb.ui.activities.mainScreeen.MainActivity;
import com.sugarman.myb.utils.DeviceHelper;
import com.sugarman.myb.utils.IntentExtractorHelper;
import java.util.Arrays;
import java.util.Locale;

public class NotificationsFragment extends BaseFragment
    implements View.OnClickListener, OnNotificationActionListener, ApiMarkNotificationListener {

  private static final String TAG = NotificationsFragment.class.getName();

  private NotificationsAdapter notificationsAdapter;

  private MarkNotificationClient mMarkNotificationClient;

  private RecyclerView rcvNotifications;

  private final ItemTouchHelper.SimpleCallback notificationsCallback =
      new ItemTouchHelper.SimpleCallback(ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT,
          ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

        @Override
        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
          return makeMovementFlags(0,
              ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT); // disable drag and drop
        }

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
            RecyclerView.ViewHolder target) {
          return true;// true if moved, false otherwise
        }

        @Override public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
          int position = viewHolder.getAdapterPosition();
          swipeNotification(position);
        }
      };

  public static NotificationsFragment newInstance(Notification[] notifications, Context context) {

    NotificationsFragment fragment = new NotificationsFragment();

    Bundle b = new Bundle();
    b.putParcelableArray(Constants.BUNDLE_NOTIFICATIONS, notifications);
    fragment.setArguments(b);

    return fragment;
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());

    String appLang = prefs.getString("pref_app_language", Locale.getDefault().getLanguage());

    Log.d("APP LANGUAGE", appLang);

    Resources res = getContext().getResources();
    DisplayMetrics dm = res.getDisplayMetrics();
    android.content.res.Configuration conf = res.getConfiguration();
    conf.locale = new Locale(appLang);
    res.updateConfiguration(conf, dm);
    super.onCreate(savedInstanceState);
  }

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {

    View root = inflater.inflate(R.layout.fragment_notifications, container, false);
    Context context = getContext();
    Resources resources = context.getResources();

    mMarkNotificationClient = new MarkNotificationClient();
    notificationsAdapter = new NotificationsAdapter(context, this);

    View vRootContainer = root.findViewById(R.id.ll_root_container);
    View vNotificationContainer = root.findViewById(R.id.ll_notification_container);
    rcvNotifications = (RecyclerView) root.findViewById(R.id.rcv_notifications);

    rcvNotifications.setLayoutManager(new LinearLayoutManager(context));
    rcvNotifications.setAdapter(notificationsAdapter);

    Bundle args = getArguments();
    Notification[] notifications = IntentExtractorHelper.getNotifications(args);
    notificationsAdapter.setValues(Arrays.asList(notifications));

    int itemHeight = resources.getDimensionPixelOffset(R.dimen.notifications_container_height)
        + 2; // 2 px for 2 dividers by 1px
    int bottomItemHeight =
        resources.getDimensionPixelOffset(R.dimen.notifications_bottom_container_height);

    LinearLayout.LayoutParams params =
        (LinearLayout.LayoutParams) vNotificationContainer.getLayoutParams();
    params.height = itemHeight * Config.VISIBLE_NOTIFICATIONS + bottomItemHeight;
    vNotificationContainer.setLayoutParams(params);

    if (notificationsAdapter.getItemCount() > Config.VISIBLE_NOTIFICATIONS) {
      params = (LinearLayout.LayoutParams) rcvNotifications.getLayoutParams();
      params.height = itemHeight * Config.VISIBLE_NOTIFICATIONS;
      rcvNotifications.setLayoutParams(params);
    }

    updateNotificationsHeight();

    ItemTouchHelper itemTouchHelper = new ItemTouchHelper(notificationsCallback);
    itemTouchHelper.attachToRecyclerView(rcvNotifications);

    vRootContainer.setOnClickListener(this);

    return root;
  }

  @Override public void onStart() {
    super.onStart();

    mMarkNotificationClient.registerListener(this);
  }

  @Override public void onStop() {
    super.onStop();

    mMarkNotificationClient.unregisterListener();
  }

  @Override public void onClick(View v) {
    int id = v.getId();
    switch (id) {
      case R.id.ll_root_container:
        // nothing
        break;
      default:
        Log.d(TAG,
            "Click on not processed view with id " + getResources().getResourceEntryName(id));
        break;
    }
  }

  @Override public void onApiUnauthorized() {
    Activity activity = getActivity();
    if (activity != null
        && activity instanceof BaseActivity
        && ((BaseActivity) activity).isReady()) {
      ((BaseActivity) activity).logout();
    }
  }

  @Override public void onUpdateOldVersion() {
    Activity activity = getActivity();
    if (activity != null
        && activity instanceof BaseActivity
        && ((BaseActivity) activity).isReady()) {
      ((BaseActivity) activity).showUpdateOldVersionDialog();
    }
  }

  @Override public void onApiMarkNotificationSuccess(String notificationId) {
    // nothing
  }

  @Override public void onApiMarkNotificationFailure(String notificationId, String message) {
    // nothing
  }

  @Override public void onNotificationClick(int position, NotificationItem item) {
    int type = item.getType();
    removeNotificationItem(position, item.getId());
    String trackingId = item.getTrackingId();

    switch (type) {
      case NotificationMessageType.GROUP_NAME_GOOD_LUCK:
      case NotificationMessageType.ONE_MORE_DAY_TO_ADD_FRIENDS:
      case NotificationMessageType.INVITATION_NO_AVAILABLE:
      case NotificationMessageType.IS_UNABLE_TO_START:
        // nothing
        break;
      case NotificationMessageType.USER_NAME_JOINED:
      case NotificationMessageType.CREATOR_NAME_APPROVED:
        App.getEventBus().post(new RefreshTrackingsEvent(trackingId));
        break;
      case NotificationMessageType.USER_NAME_HAS_POKED_YOU:
      case NotificationMessageType.PINGED_YOU_TO_MYB:
      case NotificationMessageType.GROUP_NAME_IN_HOUR:
        App.getEventBus().post(new ChallengeSelectedEvent(trackingId));
        break;
      case NotificationMessageType.USER_NAME_INVITED:
        openInvitesActivity();
        break;
      case NotificationMessageType.CONGRATS:
        openCongratulationActivity(trackingId);
        break;
      case NotificationMessageType.DAILY_SUGARMAN:
        openDailyActivity(trackingId);
        break;
      case NotificationMessageType.GROUP_NAME_HAS_FAILED:
        openFailedActivity(trackingId);
        break;
      case NotificationMessageType.USER_NAME_REQUESTED:
        openRequestsActivity();
        break;
      default:
        if (item.getUrl() != null) {
          String url = item.getUrl();
          Intent i = new Intent(Intent.ACTION_VIEW);
          if (!url.contains("://")) {
            url = new String("http://" + url);
          }
          i.setData(Uri.parse(url));
          startActivity(i);
        }
        Log.e(TAG, "not processed notification type: " + type);
        break;
    }
  }

  @Override public void onNotificationRemove(int position, NotificationItem item) {
    removeNotificationItem(position, item.getId());
  }

  @Override public void onNotificationRemoved() {
    updateNotificationsHeight();
  }

  private void swipeNotification(int position) {
    if (position < 0 || position > notificationsAdapter.getItemCount()) {
      notificationsAdapter.notifyDataSetChanged();
    } else {
      NotificationItem notification = notificationsAdapter.getValue(position);
      String notificationId = notification.getId();
      if (!TextUtils.isEmpty(notificationId)) {
        removeNotificationItem(position, notificationId);
      }
    }
  }

  private void removeNotificationItem(int position, String notificationId) {
    App.getEventBus().post(new NotificationRemovedEvent(notificationId));
    notificationsAdapter.removeItem(position);

    if (DeviceHelper.isNetworkConnected()) {
      mMarkNotificationClient.mark(notificationId);
    } else {
      Activity activity = getActivity();
      if (activity != null
          && activity instanceof BaseActivity
          && ((BaseActivity) activity).isReady()) {
        ((BaseActivity) activity).showNoInternetConnectionDialog();
      }
    }

    if (notificationsAdapter.getItemCount() == 0) {
      closeNotificationsFragment();
    }
  }

  private void closeNotificationsFragment() {
    Activity activity = getActivity();
    if (activity != null
        && activity instanceof MainActivity
        && ((MainActivity) activity).isReady()) {
      ((MainActivity) activity).closeFragment(this);
    }
  }

  private void openInvitesActivity() {
    Activity activity = getActivity();
    if (activity != null
        && activity instanceof MainActivity
        && ((MainActivity) activity).isReady()) {
      ((MainActivity) activity).openInvitesActivity();
    }
  }

  private void openRequestsActivity() {
    Activity activity = getActivity();
    if (activity != null
        && activity instanceof MainActivity
        && ((MainActivity) activity).isReady()) {
      ((MainActivity) activity).openRequestsActivity();
    }
  }

  private void openCongratulationActivity(String trackingId) {
    Activity activity = getActivity();
    if (activity != null
        && activity instanceof MainActivity
        && ((MainActivity) activity).isReady()) {
      ((MainActivity) activity).openCongratulationActivity(trackingId);
    }
  }

  private void openDailyActivity(String trackingId) {
    Activity activity = getActivity();
    if (activity != null
        && activity instanceof MainActivity
        && ((MainActivity) activity).isReady()) {
      ((MainActivity) activity).openDailyActivity(trackingId);
    }
  }

  private void openFailedActivity(String trackingId) {
    Activity activity = getActivity();
    if (activity != null
        && activity instanceof MainActivity
        && ((MainActivity) activity).isReady()) {
      ((MainActivity) activity).openFailedActivity(trackingId);
    }
  }

  private void updateNotificationsHeight() {
    int notificationsCount = notificationsAdapter.getItemCount();
    if (notificationsCount <= Config.VISIBLE_NOTIFICATIONS) {
      LinearLayout.LayoutParams params =
          (LinearLayout.LayoutParams) rcvNotifications.getLayoutParams();
      params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
      rcvNotifications.setLayoutParams(params);
    }
  }
}
