package com.sugarman.myb.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import com.sugarman.myb.R;
import com.sugarman.myb.api.models.responses.me.notifications.Notification;
import com.sugarman.myb.constants.Constants;
import com.sugarman.myb.constants.NotificationMessageType;
import com.sugarman.myb.listeners.ItemNotificationActionListener;
import com.sugarman.myb.listeners.OnNotificationActionListener;
import com.sugarman.myb.models.NotificationItem;
import com.sugarman.myb.ui.views.StrokeImage;
import com.sugarman.myb.utils.StringHelper;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class NotificationsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
    implements ItemNotificationActionListener {

  private static final String TAG = NotificationsAdapter.class.getName();

  private final WeakReference<OnNotificationActionListener> actionListener;

  private final String[] flags; // 14 types of actualNotifications

  private final List<NotificationItem> mData = new ArrayList<>();

  private final Context context;

  private final Typeface fontBold;
  private final Typeface fontRegular;

  public NotificationsAdapter(Context context, OnNotificationActionListener actionListener) {
    this.context = context;

    AssetManager assets = context.getAssets();
    fontBold = Typeface.createFromAsset(assets, context.getString(R.string.font_roboto_bold));
    fontRegular = Typeface.createFromAsset(assets, context.getString(R.string.font_roboto_regular));

    flags = context.getResources().getStringArray(R.array.notifications_types);

    this.actionListener = new WeakReference<>(actionListener);
    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

    String appLang = prefs.getString("pref_app_language", Locale.getDefault().getLanguage());
    Log.e("Language", appLang);
    Resources res = context.getResources();
    DisplayMetrics dm = res.getDisplayMetrics();
    android.content.res.Configuration conf = res.getConfiguration();
    conf.locale = new Locale(appLang);
    res.updateConfiguration(conf, dm);
  }

  @Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    Context context = parent.getContext();
    View view =
        LayoutInflater.from(context).inflate(R.layout.layout_item_notification, parent, false);

    return new NotificationsHolder(view, this);
  }

  @Override public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
    NotificationItem item = getValue(position);
    if (item != null) {
      NotificationsHolder notificationsHolder = (NotificationsHolder) holder;
      notificationsHolder.tvMessage.setText(item.getSpan());
      String pictureUrl = item.getOriginator().getPictureUrl();

      if (!TextUtils.isEmpty(pictureUrl)) {
        Picasso.with(context)
            .load(pictureUrl)
            .fit()
            .centerCrop()
            .placeholder(R.drawable.ic_gray_avatar)
            .error(R.drawable.ic_gray_avatar)
            .into(notificationsHolder.ivAvatar.getImageView());
      } else {
        notificationsHolder.ivAvatar.setImageResource(R.drawable.ic_gray_avatar);
      }
    }
  }

  @Override public int getItemCount() {
    return mData.size();
  }

  @Override public void onNotificationItemClick(int position) {
    if (position >= 0 && position < mData.size()) {
      if (actionListener.get() != null) {
        NotificationItem notificationItem = mData.get(position);
        actionListener.get().onNotificationClick(position, notificationItem);
      }
    } else {
      notifyDataSetChanged();
    }
  }

  @Override public void onNotificationCrossItemClick(int position) {
    if (position >= 0 && position < mData.size()) {
      if (actionListener.get() != null) {
        NotificationItem notificationItem = mData.get(position);
        actionListener.get().onNotificationRemove(position, notificationItem);
      }
    } else {
      notifyDataSetChanged();
    }
  }

  public void setValues(List<Notification> values) {
    mData.clear();
    for (Notification notification : values) {
      NotificationItem item = new NotificationItem(notification);
      String message = item.getText();
      boolean isTypeDetected = true;

      for (int i = 0; i < flags.length; i++) {
        String text = flags[i];
        String[] itemFlags = text.split(Constants.IN_APP_NOTIFICATION_MESSAGE_DIVIDER);
        isTypeDetected = true;
        for (String flag : itemFlags) {
          isTypeDetected &= message.contains(flag);
        }

        if (isTypeDetected) {
          int type = NotificationMessageType.IDS[i];
          item.setType(type);
          item.setSpan(
              StringHelper.getNotificationMessageSpannable(type, flags, item.getText(), fontBold,
                  fontRegular));
          break;
        }
      }

      if (!isTypeDetected) {
        Log.d(TAG, "not supported template for messages: " + message);
        item.setType(NotificationMessageType.UNKNOWN);
        item.setSpan(new SpannableStringBuilder(message));
      }

      mData.add(item);
    }

    notifyDataSetChanged();
  }

  public NotificationItem getValue(int position) {
    if (position >= 0 && position < mData.size()) {
      return mData.get(position);
    } else {
      return null;
    }
  }

  public void removeItem(int position) {
    int total = getItemCount();
    if (position <= total - 1 && position >= 0) {
      mData.remove(position);

      if (actionListener.get() != null) {
        actionListener.get().onNotificationRemoved();
      }
      notifyItemRemoved(position);
    } else {
      Log.e(TAG, "try remove item with position: " + position + " total items: " + total);
    }
  }

  private static class NotificationsHolder extends RecyclerView.ViewHolder
      implements View.OnClickListener {

    private final WeakReference<ItemNotificationActionListener> actionListener;

    private final TextView tvMessage;
    private final StrokeImage ivAvatar;

    NotificationsHolder(View itemView, ItemNotificationActionListener clickItemListener) {
      super(itemView);

      actionListener = new WeakReference<>(clickItemListener);

      View container = itemView.findViewById(R.id.ll_notification_container);
      View cross = itemView.findViewById(R.id.iv_cross);
      tvMessage = (TextView) itemView.findViewById(R.id.tv_message);
      ivAvatar = (StrokeImage) itemView.findViewById(R.id.iv_avatar);

      cross.setOnClickListener(this);
      container.setOnClickListener(this);
    }

    @Override public void onClick(View v) {
      int id = v.getId();
      int position = getAdapterPosition();

      switch (id) {
        case R.id.ll_notification_container:
          if (actionListener.get() != null) {
            actionListener.get().onNotificationItemClick(position);
          }
          break;
        case R.id.iv_cross:
          if (actionListener.get() != null) {
            actionListener.get().onNotificationCrossItemClick(position);
          }
          break;
        default:
          Log.d(TAG,
              "Click on not processed view with id " + v.getResources().getResourceEntryName(id));
          break;
      }
    }
  }
}