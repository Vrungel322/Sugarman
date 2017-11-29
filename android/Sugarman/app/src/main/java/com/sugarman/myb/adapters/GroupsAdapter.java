package com.sugarman.myb.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.appsflyer.AFInAppEventParameterName;
import com.appsflyer.AppsFlyerLib;
import com.squareup.picasso.CustomPicasso;
import com.sugarman.myb.App;
import com.sugarman.myb.R;
import com.sugarman.myb.api.models.responses.Group;
import com.sugarman.myb.constants.Constants;
import com.sugarman.myb.listeners.ItemGroupsActionListener;
import com.sugarman.myb.listeners.OnGroupsActionListener;
import com.sugarman.myb.models.SearchTracking;
import com.sugarman.myb.ui.views.MaskTransformation;
import com.sugarman.myb.ui.views.StrokeImage;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GroupsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
    implements ItemGroupsActionListener {

  private static final String TAG = GroupsAdapter.class.getName();

  private final List<SearchTracking> mData = new ArrayList<>();

  private final WeakReference<OnGroupsActionListener> actionListener;

  private final Context context;

  private final String membersTemplate;
  private final String createdByTemplate;
  private final String daysAgoTemplate;
  private final String join;
  private final String requested;
  private final String yesterday;
  private final String today;

  private final int colorWhite;
  private final int colorRed;

  private boolean isAvailable = true;

  private final Calendar now;
  private final Calendar created;

  public GroupsAdapter(Context context, OnGroupsActionListener listener) {
    actionListener = new WeakReference<>(listener);

    this.context = context;

    membersTemplate = context.getString(R.string.plural_decimal_template);
    createdByTemplate = context.getString(R.string.created_by_template);
    daysAgoTemplate = context.getString(R.string.days_ago_template);
    yesterday = context.getString(R.string.yesterday);
    today = context.getString(R.string.today);

    join = context.getString(R.string.join);
    requested = context.getString(R.string.requested);

    colorWhite = ContextCompat.getColor(context, android.R.color.white);
    colorRed = ContextCompat.getColor(context, R.color.red_transparent);

    now = GregorianCalendar.getInstance();
    created = GregorianCalendar.getInstance();
  }

  @Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    Context context = parent.getContext();
    View view = LayoutInflater.from(context).inflate(R.layout.layout_item_groups, parent, false);
    return new GroupsHolder(view, this);
  }

  @Override public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
    SearchTracking searchTracking = getValue(position);
    if (searchTracking != null) {
      Group group = searchTracking.getGroup();
      GroupsHolder groupsHolder = (GroupsHolder) holder;
      groupsHolder.tvGroupName.setText(group.getName());
      groupsHolder.tvCreatedBy.setText(
          String.format(createdByTemplate, searchTracking.getGroupOnwerName()));
      int membersCount = searchTracking.getMembers().length + searchTracking.getPending().length;

      String membersPlural =
          context.getResources().getQuantityString(R.plurals.members, membersCount, membersCount);
      groupsHolder.tvMembers.setText(String.format(membersTemplate, membersCount, membersPlural));

      now.setTimeInMillis(System.currentTimeMillis());
      created.setTimeInMillis(searchTracking.getCreatedAtUTCDate().getTime());

      created.set(Calendar.HOUR_OF_DAY, 0);
      created.set(Calendar.MINUTE, 0);
      created.set(Calendar.SECOND, 0);
      created.set(Calendar.MILLISECOND, 0);

      now.set(Calendar.HOUR_OF_DAY, 0);
      now.set(Calendar.MINUTE, 0);
      now.set(Calendar.SECOND, 0);
      now.set(Calendar.MILLISECOND, 0);

      int nowDay = now.get(Calendar.DAY_OF_MONTH);
      int nowMonth = now.get(Calendar.MONTH);
      int nowYear = now.get(Calendar.YEAR);
      int createdDay = created.get(Calendar.DAY_OF_MONTH);
      int createdMonth = created.get(Calendar.MONTH);
      int createdYear = created.get(Calendar.YEAR);

      String days;
      if (nowDay == createdDay && nowMonth == createdMonth && nowYear == createdYear) {
        days = today;
      } else if (nowDay - 1 == createdDay && nowMonth == createdMonth && nowYear == createdYear) {
        days = yesterday;
      } else {
        float diff =
            (now.getTimeInMillis() - created.getTimeInMillis()) / (float) Constants.MS_IN_DAY;
        days = String.format(daysAgoTemplate, (int) diff);
      }

      groupsHolder.tvTime.setText(days);

      String pictureUrl = group.getPictureUrl();
      if (TextUtils.isEmpty(pictureUrl)) {
        groupsHolder.ivAvatar.setImageResource(R.drawable.ic_group);
      } else {
        CustomPicasso.with(context)
            .load(pictureUrl)
            .fit()
            .centerCrop()
            .placeholder(R.drawable.ic_gray_avatar)
            .transform(new MaskTransformation(context, R.drawable.group_avatar, false, 0xfff))
            .error(R.drawable.ic_group)
            .into(groupsHolder.ivAvatar.getImageView());
      }

      groupsHolder.tvAction.setVisibility(isAvailable ? View.VISIBLE : View.GONE);

      if (searchTracking.isRequested()) {
        groupsHolder.tvAction.setText(requested);
        groupsHolder.tvAction.setBackgroundResource(R.drawable.remove);
        groupsHolder.tvAction.setTextColor(colorRed);
      } else {
        groupsHolder.tvAction.setText(join);
        groupsHolder.tvAction.setBackgroundResource(R.drawable.add_and_remove);
        groupsHolder.tvAction.setTextColor(colorWhite);
      }
    }
  }

  @Override public int getItemCount() {
    return mData.size();
  }

  @Override public void onClickJoinGroup(int position) {

    Map<String, Object> eventValue = new HashMap<>();
    eventValue.put(AFInAppEventParameterName.LEVEL, 9);
    eventValue.put(AFInAppEventParameterName.SCORE, 100);
    AppsFlyerLib.getInstance()
        .trackEvent(App.getInstance().getApplicationContext(), "af_join_group", eventValue);

    if (position >= 0 && position < mData.size()) {
      if (actionListener.get() != null) {
        SearchTracking searchTracking = mData.get(position);
        if (!searchTracking.isRequested()) {
          actionListener.get()
              .onJoinGroup(position, searchTracking.getId(), searchTracking.getGroup().getId());
        }
      }
    } else {
      notifyDataSetChanged();
    }
  }

  @Override public void onClickGroup(int position) {

    Map<String, Object> eventValue = new HashMap<>();
    eventValue.put(AFInAppEventParameterName.LEVEL, 9);
    eventValue.put(AFInAppEventParameterName.SCORE, 100);
    AppsFlyerLib.getInstance()
        .trackEvent(App.getInstance().getApplicationContext(), "af_open_group_details_from_search",
            eventValue);

    if (position >= 0 && position < mData.size()) {
      if (actionListener.get() != null) {
        SearchTracking searchTracking = mData.get(position);
        if (!searchTracking.isRequested()) {
          actionListener.get()
              .onClickGroup(position, searchTracking.getId(), searchTracking.getGroup().getId());
        }
      }
    } else {
      notifyDataSetChanged();
    }
  }

  public void setValues(List<SearchTracking> values, boolean isAvailable) {
    this.isAvailable = isAvailable;
    mData.clear();
    mData.addAll(values);
    notifyDataSetChanged();
  }

  public SearchTracking getValue(int position) {
    if (position >= 0 && position < mData.size()) {
      return mData.get(position);
    } else {
      return null;
    }
  }

  private static class GroupsHolder extends RecyclerView.ViewHolder
      implements View.OnClickListener {

    private final WeakReference<ItemGroupsActionListener> actionItemListener;

    private final TextView tvCreatedBy;
    private final TextView tvTime;
    private final TextView tvMembers;
    private final TextView tvGroupName;
    private final TextView tvAction;
    private final StrokeImage ivAvatar;
    private final CardView cvContainer;

    GroupsHolder(View itemView, ItemGroupsActionListener clickItemListener) {
      super(itemView);

      actionItemListener = new WeakReference<>(clickItemListener);
      cvContainer = (CardView) itemView.findViewById(R.id.card_container);
      tvCreatedBy = (TextView) itemView.findViewById(R.id.tv_created_by);
      tvGroupName = (TextView) itemView.findViewById(R.id.tv_group_name);
      tvTime = (TextView) itemView.findViewById(R.id.tv_time);
      tvMembers = (TextView) itemView.findViewById(R.id.tv_count_members);
      tvAction = (TextView) itemView.findViewById(R.id.tv_action);
      ivAvatar = (StrokeImage) itemView.findViewById(R.id.iv_group_avatar);

      cvContainer.setOnClickListener(this);
      tvAction.setOnClickListener(this);
    }

    @Override public void onClick(View v) {
      int id = v.getId();
      int position = getAdapterPosition();

      switch (id) {
        case R.id.tv_action:
          if (actionItemListener.get() != null) {
            actionItemListener.get().onClickJoinGroup(position);
          }
          break;
        case R.id.card_container:
          if (actionItemListener.get() != null) {
            actionItemListener.get().onClickGroup(position);
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
