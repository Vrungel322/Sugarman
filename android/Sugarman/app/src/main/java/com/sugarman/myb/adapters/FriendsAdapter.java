package com.sugarman.myb.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.CustomPicasso;
import com.sugarman.myb.R;
import com.sugarman.myb.api.models.responses.facebook.FacebookFriend;
import com.sugarman.myb.listeners.ItemUsersActionListener;
import com.sugarman.myb.ui.views.CropCircleTransformation;
import io.realm.Realm;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import timber.log.Timber;

public class FriendsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
    implements ItemUsersActionListener {

  private static final String TAG = FriendsAdapter.class.getName();

  private final List<FacebookFriend> mUnselected = new ArrayList<>();
  private final List<FacebookFriend> mSelected = new ArrayList<>();
  //need for  getAllList ONLY !!!
  private final List<FacebookFriend> mAllItems = new ArrayList<>();

  private final Context context;

  private final String add;
  private final String remove;

  private final int colorWhite;
  private final int colorRed;
  private final int colorGrey;
  private final int colorLightGrey;
  FriendsHolder friendsHolder;

  public FriendsAdapter(Context context) {

    this.context = context;

    add = context.getString(R.string.add);
    remove = context.getString(R.string.remove);

    colorWhite = ContextCompat.getColor(context, android.R.color.white);
    colorRed = ContextCompat.getColor(context, R.color.red_transparent);
    colorGrey = ContextCompat.getColor(context, R.color.dark_gray);
    colorLightGrey = ContextCompat.getColor(context, R.color.selected_user_grey);
  }

  @Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    Context context = parent.getContext();
    View view = LayoutInflater.from(context).inflate(R.layout.layout_item_friends, parent, false);
    return new FriendsHolder(view, this);
  }

  @Override public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
    FacebookFriend friend = getValue(position);
    if (friend != null) {
      friendsHolder = (FriendsHolder) holder;

      if (friend.getPicture() != null && friend.getPicture() != null && !TextUtils.isEmpty(
          friend.getPicture())) {

        String url = friend.getPicture();
        CustomPicasso.with(context)
            .load(url)
            .placeholder(R.drawable.ic_gray_avatar)
            .error(R.drawable.ic_gray_avatar)
            .transform(new CropCircleTransformation(0x00ffffff, 4))
            .into(friendsHolder.ivAvatar);
      } else {
        friendsHolder.ivAvatar.setImageResource(R.drawable.ic_gray_avatar);
      }

      String name = friend.getName();
      friendsHolder.tvUserName.setText(name);

      //Timber.e("Friend " + friend.getName() +" is invitable: " + friend.getIsInvitable());
      if (friend.getIsInvitable() == FacebookFriend.CODE_INVITABLE) {
        friendsHolder.ivIndicatorInvitable.setVisibility(View.INVISIBLE);
      } else {
        friendsHolder.ivIndicatorInvitable.setVisibility(View.VISIBLE);
      }

      switch (friend.getSocialNetwork()) {
        case "fb":
          friendsHolder.ivSocialNetwork.setBackgroundResource(R.drawable.fb_icon);
          break;
        case "vk":
          friendsHolder.ivSocialNetwork.setBackgroundResource(R.drawable.vk_icon);
          break;
        default:
          friendsHolder.ivSocialNetwork.setBackgroundResource(R.drawable.phone_icon);
          break;
      }

      if (friend.isSelected()) {
        friendsHolder.tvActionBtn.setText(remove);
        friendsHolder.tvActionBtn.setBackgroundResource(R.drawable.remove);
        friendsHolder.tvActionBtn.setTextColor(0xffee6a66);
        friendsHolder.tvUserName.setTextColor(colorLightGrey);
        //friendsHolder.ivAvatar.setBackgroundColor(colorLightGrey);
      } else {
        friendsHolder.tvActionBtn.setText(add);
        friendsHolder.tvActionBtn.setBackgroundResource(R.drawable.add_and_remove);
        friendsHolder.tvActionBtn.setTextColor(colorWhite);
        friendsHolder.tvUserName.setTextColor(colorGrey);
        //friendsHolder.ivAvatar.setBackgroundColor(colorGrey);
      }

      boolean isPending = friend.isPending();
      boolean isAdded = friend.isAdded();
      boolean isSelected = friend.isSelected();
      if (isPending || isAdded || isSelected) {
        if (isPending) {
          friendsHolder.tvActionBtn.setText(
              friendsHolder.tvActionBtn.getContext().getString(R.string.pending));
          friendsHolder.container.setEnabled(false);
        }
        if (isAdded) {
          friendsHolder.tvActionBtn.setText(
              friendsHolder.tvActionBtn.getContext().getString(R.string.added));
          friendsHolder.container.setEnabled(false);
        }
        if (isSelected) {
          friendsHolder.tvActionBtn.setText(remove);
        }
        //friendsHolder.tvActionBtn.setBackgroundResource(R.drawable.gray_double_stroke_background);
        friendsHolder.tvActionBtn.setBackgroundResource(R.drawable.remove);
        friendsHolder.tvActionBtn.setTextColor(colorRed);
      } else {
        friendsHolder.tvActionBtn.setText(add);
        //friendsHolder.tvActionBtn.setBackgroundResource(R.drawable.dark_gray_double_stroke_background);
        friendsHolder.tvActionBtn.setBackgroundResource(R.drawable.add_and_remove);
        friendsHolder.tvActionBtn.setTextColor(colorWhite);
      }
    }
  }

  @Override public int getItemCount() {
    return mSelected.size() + mUnselected.size();
  }

  @Override public void onClickFriendManage(int position) {
    if (position >= 0 && position < getItemCount()) {
      FacebookFriend friend;
      if (position < mSelected.size()) {
        friend = mSelected.get(position);
      } else {
        friend = mUnselected.get(position - mSelected.size());
      }
      Timber.e("click friendName:" + friend.getName());

      boolean isSelected = !friend.isSelected();
      Realm.getDefaultInstance().executeTransaction(realm -> {
        FacebookFriend myObject = friend;
        myObject.setSelected(isSelected);
        realm.insertOrUpdate(myObject); // could be copyToRealmOrUpdate
      });

      if (isSelected) {
        mSelected.add(friend);
        mUnselected.remove(friend);
      } else {
        mSelected.remove(friend);
        mUnselected.add(friend);
      }
    }

    Collections.sort(mSelected, FacebookFriend.BY_NAME_ASC);
    Collections.sort(mUnselected, FacebookFriend.BY_NAME_ASC);

    notifyDataSetChanged();
  }

  public void setValue(List<FacebookFriend> values) {
    mUnselected.clear();
    mSelected.clear();
    mAllItems.clear();
    List<FacebookFriend> newFriends = new ArrayList<>();
    newFriends.addAll(values);

    for (FacebookFriend friend : newFriends) {
      if (friend.isSelected()) {
        mSelected.add(friend);
      } else {
        mUnselected.add(friend);
      }
    }

    Collections.sort(mSelected, FacebookFriend.BY_NAME_ASC);
    Collections.sort(mUnselected, FacebookFriend.BY_NAME_ASC);
    mAllItems.addAll(mSelected);
    mAllItems.addAll(mUnselected);
    notifyDataSetChanged();
  }

  public void addValue(List<FacebookFriend> values) {
    mAllItems.clear();
    List<FacebookFriend> newFriends = new ArrayList<>();
    newFriends.addAll(values);

    for (FacebookFriend friend : newFriends) {
      if (friend.isSelected()) {
        mSelected.add(friend);
      } else {
        mUnselected.add(friend);
      }
    }

    Collections.sort(mSelected, FacebookFriend.BY_NAME_ASC);
    Collections.sort(mUnselected, FacebookFriend.BY_NAME_ASC);
    mAllItems.addAll(mSelected);
    mAllItems.addAll(mUnselected);
    notifyDataSetChanged();
  }

  public List<FacebookFriend> getSelectedFriends() {
    return mSelected;
  }

  private FacebookFriend getValue(int position) {
    if (position >= 0 && position < mSelected.size()) {
      return mSelected.get(position);
    } else if (position >= mSelected.size() && position < getItemCount()) {
      return mUnselected.get(position - mSelected.size());
    } else {
      return null;
    }
  }

  public List<FacebookFriend> getAllList() {
    return mAllItems;
  }

  private static class FriendsHolder extends RecyclerView.ViewHolder
      implements View.OnClickListener {

    private final WeakReference<ItemUsersActionListener> mActionItemListener;

    private final TextView tvUserName;
    private final TextView tvActionBtn;
    private final ImageView ivAvatar;
    private final ImageView ivIndicatorInvitable;
    private final ImageView ivSocialNetwork;
    private final View container;

    FriendsHolder(View itemView, ItemUsersActionListener clickItemListener) {
      super(itemView);

      mActionItemListener = new WeakReference<>(clickItemListener);

      container = itemView.findViewById(R.id.ll_friend_container);
      ivIndicatorInvitable = (ImageView) itemView.findViewById(R.id.iv_indicataor_invitable);
      tvUserName = (TextView) itemView.findViewById(R.id.tv_user_name);
      tvActionBtn = (TextView) itemView.findViewById(R.id.tv_remove_add);
      ivAvatar = (ImageView) itemView.findViewById(R.id.iv_avatar);
      ivSocialNetwork = (ImageView) itemView.findViewById(R.id.social_network_icon);

      container.setOnClickListener(this);
    }

    @Override public void onClick(View v) {
      int id = v.getId();
      int position = getAdapterPosition();
      Timber.e("click position:" + position);

      switch (id) {
        case R.id.ll_friend_container:
          if (mActionItemListener.get() != null) {
            mActionItemListener.get().onClickFriendManage(position);
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
