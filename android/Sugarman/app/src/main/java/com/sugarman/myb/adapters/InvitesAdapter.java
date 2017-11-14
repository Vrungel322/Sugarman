package com.sugarman.myb.adapters;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.appsflyer.AFInAppEventParameterName;
import com.appsflyer.AppsFlyerLib;
import com.squareup.picasso.Picasso;
import com.sugarman.myb.App;
import com.sugarman.myb.R;
import com.sugarman.myb.api.models.responses.Member;
import com.sugarman.myb.api.models.responses.Tracking;
import com.sugarman.myb.api.models.responses.me.invites.Invite;
import com.sugarman.myb.listeners.ItemInvitesActionListener;
import com.sugarman.myb.listeners.OnInvitesActionListener;
import com.sugarman.myb.ui.views.StrokeImage;
import com.sugarman.myb.utils.StringHelper;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InvitesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
    implements ItemInvitesActionListener {

  private static final String TAG = InvitesAdapter.class.getName();

  private final List<Invite> mData = new ArrayList<>();

  private final Context context;

  private final WeakReference<OnInvitesActionListener> mActionListener;

  private final Typeface fontBold;
  private final Typeface fontRegular;

  private final String descTemplate;
  private final String flag1;
  private final String flag2;

  public InvitesAdapter(Context context, OnInvitesActionListener listener) {
    mActionListener = new WeakReference<>(listener);
    this.context = context;

    AssetManager assets = context.getAssets();
    fontBold = Typeface.createFromAsset(assets, context.getString(R.string.font_roboto_bold));
    fontRegular = Typeface.createFromAsset(assets, context.getString(R.string.font_roboto_regular));

    descTemplate = context.getString(R.string.invite_description_template);
    flag1 = context.getString(R.string.invite_description_flag1);
    flag2 = context.getString(R.string.invite_description_flag2);
  }

  @Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    Context context = parent.getContext();
    View view = LayoutInflater.from(context).inflate(R.layout.layout_item_invites, parent, false);
    return new InvitesHolder(view, this);
  }

  @Override public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
    Invite invite = getValue(position);
    if (invite != null) {
      InvitesAdapter.InvitesHolder invitesHolder = (InvitesAdapter.InvitesHolder) holder;
      Tracking tracking = invite.getTracking();
      String groupName = tracking.getGroup().getName();
      String ownerName = tracking.getGroupOnwerName();

      String description = String.format(descTemplate, ownerName, groupName);
      SpannableStringBuilder span =
          StringHelper.getDoubleBoldSpan(description, flag1, flag2, fontBold, fontRegular);

      invitesHolder.tvInviteDescription.setText(span);

      String ownerId = tracking.getGroupOwnerId();
      Member[] members = tracking.getMembers();
      String pictureUrl = "";
      for (Member member : members) {
        if (TextUtils.equals(member.getId(), ownerId)) {
          pictureUrl = member.getPictureUrl();
          break;
        }
      }

      if (TextUtils.isEmpty(pictureUrl)) {
        invitesHolder.ivAvatar.setImageResource(R.drawable.ic_gray_avatar);
      } else {
        Picasso.with(context)
            .load(pictureUrl)
            .fit()
            .centerCrop()
            .placeholder(R.drawable.ic_gray_avatar)
            .error(R.drawable.ic_gray_avatar)
            .into(invitesHolder.ivAvatar.getImageView());
      }
    }
  }

  @Override public int getItemCount() {
    return mData.size();
  }

  @Override public void onClickAccept(int position) {

    Map<String, Object> eventValue = new HashMap<>();
    eventValue.put(AFInAppEventParameterName.LEVEL, 9);
    eventValue.put(AFInAppEventParameterName.SCORE, 100);
    AppsFlyerLib.getInstance().trackEvent(App.getInstance().getApplicationContext(), "af_accept_invite", eventValue);

    if (position >= 0 && position < mData.size()) {
      if (mActionListener.get() != null) {
        mActionListener.get().onAcceptInvite(mData.get(position), position);
      }
    } else {
      notifyDataSetChanged();
    }
  }

  @Override public void onClickDecline(int position) {

    Map<String, Object> eventValue = new HashMap<>();
    eventValue.put(AFInAppEventParameterName.LEVEL, 9);
    eventValue.put(AFInAppEventParameterName.SCORE, 100);
    AppsFlyerLib.getInstance().trackEvent(App.getInstance().getApplicationContext(), "af_decline_invite", eventValue);

    if (position >= 0 && position < mData.size()) {
      if (mActionListener.get() != null) {
        mActionListener.get().onDeclineInvite(mData.get(position), position);
      }
    } else {
      notifyDataSetChanged();
    }
  }

  public void setValues(List<Invite> values) {
    mData.clear();
    mData.addAll(values);
    notifyDataSetChanged();
  }

  public void removeItem(int position) {
    int total = getItemCount();
    if (position <= total - 1 && position >= 0) {
      mData.remove(position);
      notifyItemRemoved(position);
    } else {
      Log.e(TAG, "try remove item with position: " + position + " total items: " + total);
    }
  }

  public Invite getValue(int position) {
    if (position >= 0 && position < mData.size()) {
      return mData.get(position);
    } else {
      return null;
    }
  }

  private static class InvitesHolder extends RecyclerView.ViewHolder
      implements View.OnClickListener {

    private final WeakReference<ItemInvitesActionListener> mActionItemListener;

    private final TextView tvInviteDescription;
    private final StrokeImage ivAvatar;

    InvitesHolder(View itemView, ItemInvitesActionListener clickItemListener) {
      super(itemView);

      mActionItemListener = new WeakReference<>(clickItemListener);

      tvInviteDescription = (TextView) itemView.findViewById(R.id.tv_invite_description);
      ivAvatar = (StrokeImage) itemView.findViewById(R.id.iv_avatar);

      View vDecline = itemView.findViewById(R.id.tv_decline);
      View vAccept = itemView.findViewById(R.id.tv_accept);

      vDecline.setOnClickListener(this);
      vAccept.setOnClickListener(this);
    }

    @Override public void onClick(View v) {
      int id = v.getId();
      int position = getAdapterPosition();

      switch (id) {
        case R.id.tv_decline:
          if (mActionItemListener.get() != null) {
            mActionItemListener.get().onClickDecline(position);
          }
          break;
        case R.id.tv_accept:
          if (mActionItemListener.get() != null) {
            mActionItemListener.get().onClickAccept(position);
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
