package com.sugarman.myb.adapters.membersAdapter;

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
import com.arellomobile.mvp.MvpDelegate;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.squareup.picasso.Picasso;
import com.sugarman.myb.R;
import com.sugarman.myb.api.models.responses.facebook.FacebookFriend;
import com.sugarman.myb.base.MvpBaseRecyclerAdapter;
import com.sugarman.myb.listeners.ItemMemberActionListener;
import com.sugarman.myb.ui.views.CropCircleTransformation;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import timber.log.Timber;

public class MembersAdapter extends MvpBaseRecyclerAdapter<RecyclerView.ViewHolder>
    implements ItemMemberActionListener, IMembersAdapterView {
  @InjectPresenter MembersAdapterPresenter mPresenter;

  private static final String TAG = MembersAdapter.class.getName();

  private final List<FacebookFriend> mUnselected = new ArrayList<>();
  private final List<FacebookFriend> mSelected = new ArrayList<>();

  private final Context context;

  private final String add;
  private final String remove;
  private final String added;
  private final String pending;

  private final int colorWhite;
  private final int colorRed;

  public MembersAdapter(MvpDelegate<?> parentDelegate,Context context) {
    super(parentDelegate,"MembersAdapter");
    this.context = context;

    add = context.getString(R.string.add);
    added = context.getString(R.string.added);
    remove = context.getString(R.string.remove);
    pending = context.getString(R.string.pending);

    colorWhite = ContextCompat.getColor(context, android.R.color.white);
    colorRed = ContextCompat.getColor(context, R.color.red_transparent);
  }

  @Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    Context context = parent.getContext();
    View view =
        LayoutInflater.from(context).inflate(R.layout.layout_item_add_member, parent, false);
    return new MembersHolder(view, this);
  }

  @Override public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
    FacebookFriend friend = getValue(position);
    if (friend != null) {
      MembersHolder membersHolder = (MembersHolder) holder;

      if (friend.getPicture() != null && friend.getPicture() != null && !TextUtils.isEmpty(
          friend.getPicture())) {

        String url = friend.getPicture();
        Timber.e("URLSTART"+url+"URLEND");
        if(url == null||url.equals("")||url.equals(" "))
          url = "https://sugarman-myb.s3.amazonaws.com/Group_New.png";
        Picasso.with(context)
            .load(url)
            .placeholder(R.drawable.ic_gray_avatar)
            .error(R.drawable.ic_gray_avatar)
            .transform(new CropCircleTransformation(0x00ffffff, 4))
            .into(membersHolder.ivAvatar);
      } else {
        membersHolder.ivAvatar.setImageResource(R.drawable.ic_gray_avatar);
      }

      //Timber.e("Friend " + friend.getName() + " is invitable = " + friend.getId());

      if (friend.getIsInvitable() == FacebookFriend.CODE_INVITABLE) {
        membersHolder.ivIndicatorInvitable.setVisibility(View.INVISIBLE);
      } else {
        membersHolder.ivIndicatorInvitable.setVisibility(View.VISIBLE);
      }

      switch (friend.getSocialNetwork()) {
        case "fb":
          membersHolder.ivSocialNetwork.setBackgroundResource(R.drawable.fb_icon);
          break;
        case "vk":
          membersHolder.ivSocialNetwork.setBackgroundResource(R.drawable.vk_icon);
          break;
        default:
          membersHolder.ivSocialNetwork.setBackgroundResource(R.drawable.phone_icon);
          break;
      }

      String name = friend.getName();
      membersHolder.tvMemberName.setText(name);

      boolean isPending = friend.isPending();
      boolean isAdded = friend.isAdded();
      boolean isSelected = friend.isSelected();
      if (isPending || isAdded || isSelected) {
        if(isPending) membersHolder.tvActionBtn.setText(pending);
        if(isAdded) membersHolder.tvActionBtn.setText(added);
        if(isSelected) membersHolder.tvActionBtn.setText(remove);
        //membersHolder.tvActionBtn.setBackgroundResource(R.drawable.gray_double_stroke_background);
        membersHolder.tvActionBtn.setBackgroundResource(R.drawable.remove);
        membersHolder.tvActionBtn.setTextColor(colorRed);
      } else {
        membersHolder.tvActionBtn.setText(add);
        //membersHolder.tvActionBtn.setBackgroundResource(R.drawable.dark_gray_double_stroke_background);
        membersHolder.tvActionBtn.setBackgroundResource(R.drawable.add_and_remove);
        membersHolder.tvActionBtn.setTextColor(colorWhite);
      }
    }
  }

  @Override public int getItemCount() {
    return mSelected.size() + mUnselected.size();
  }

  @Override public void onClickMemberManage(int position) {
    if (position >= 0 && position < getItemCount()) {
      FacebookFriend friend;
      if (position < mSelected.size()) {
        friend = mSelected.get(position);
      } else {
        friend = mUnselected.get(position - mSelected.size());
      }

      Timber.e("Friend network " + friend.getSocialNetwork());

      if (!friend.isPending() && !friend.isAdded()) {
        boolean isSelected = !friend.isSelected();
        friend.setSelected(isSelected);

        if (isSelected) {
          mSelected.add(friend);
          mUnselected.remove(friend);
        } else {
          mSelected.remove(friend);
          mUnselected.add(friend);
        }

        Collections.sort(mSelected, FacebookFriend.BY_NAME_ASC);
        Collections.sort(mUnselected, FacebookFriend.BY_NAME_ASC);
      }
    }
    if (mSelected.size()>0){
      mPresenter.postShowAddFriendBtn();
    }
    else {
      mPresenter.postHideAddFriendBtn();
    }

    notifyDataSetChanged();
  }

  public void setValue(List<FacebookFriend> values) {
    //mUnselected.clear();
    //mSelected.clear();
    Timber.e("setFilteredValue");

    for (FacebookFriend friend : values) {
      if (friend.isSelected()) {
        mSelected.add(friend);
      } else {
        mUnselected.add(friend);
      }
    }

    Collections.sort(mSelected, FacebookFriend.BY_NAME_ASC);
    Collections.sort(mUnselected, FacebookFriend.BY_NAME_ASC);
    notifyItemRangeChanged(0, values.size());
  }

  public void setValuesClearList(List<FacebookFriend> values) {
    mUnselected.clear();
    mSelected.clear();
    Timber.e("setFilteredValue");

    for (FacebookFriend friend : values) {
      if (friend.isSelected()) {
        mSelected.add(friend);
      } else {
        mUnselected.add(friend);
      }
    }

    Collections.sort(mSelected, FacebookFriend.BY_NAME_ASC);
    Collections.sort(mUnselected, FacebookFriend.BY_NAME_ASC);
    notifyDataSetChanged();
  }

  public void addVkFriends(List<FacebookFriend> values){
    Collections.sort(values, FacebookFriend.BY_NAME_ASC);
    mUnselected.addAll(values);
    notifyDataSetChanged();
  }

  public void markSelectedAsPending() {
    for (FacebookFriend friend : mSelected) {
      friend.setPending(true);
      notifyItemChanged(mUnselected.indexOf(friend));
    }
  }

  public List<FacebookFriend> getSelectedMembers() {
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

  public void addPhoneContacts(List<FacebookFriend> facebookFriends) {
    Collections.sort(facebookFriends, FacebookFriend.BY_NAME_ASC);
    mUnselected.addAll(facebookFriends);
    notifyDataSetChanged();
  }

  public void setFilteredValue(List<FacebookFriend> filtered) {
    mUnselected.clear();
    mSelected.clear();
    Timber.e("setFilteredValue");

    for (FacebookFriend friend : filtered) {
      if (friend.isSelected()) {
        mSelected.add(friend);
      } else {
        mUnselected.add(friend);
      }
    }

    Collections.sort(mSelected, FacebookFriend.BY_NAME_ASC);
    Collections.sort(mUnselected, FacebookFriend.BY_NAME_ASC);
    notifyDataSetChanged();
  }

  public void clearLists(){
    mUnselected.clear();
    mSelected.clear();
  }

  private static class MembersHolder extends RecyclerView.ViewHolder
      implements View.OnClickListener {

    private final WeakReference<ItemMemberActionListener> mActionItemListener;

    private final TextView tvMemberName;
    private final TextView tvActionBtn;
    private final ImageView ivAvatar;
    private final ImageView ivIndicatorInvitable;
    private final ImageView ivSocialNetwork;

    MembersHolder(View itemView, ItemMemberActionListener clickItemListener) {
      super(itemView);

      mActionItemListener = new WeakReference<>(clickItemListener);

      ivIndicatorInvitable = (ImageView) itemView.findViewById(R.id.iv_indicataor_invitable);
      tvMemberName = (TextView) itemView.findViewById(R.id.tv_user_name);
      tvActionBtn = (TextView) itemView.findViewById(R.id.tv_remove_add);
      ivAvatar = (ImageView) itemView.findViewById(R.id.iv_avatar);
      ivSocialNetwork = (ImageView) itemView.findViewById(R.id.social_network_icon);

      tvActionBtn.setOnClickListener(this);
    }

    @Override public void onClick(View v) {
      int id = v.getId();
      int position = getAdapterPosition();

      switch (id) {
        case R.id.tv_remove_add:
          if (mActionItemListener.get() != null) {
            mActionItemListener.get().onClickMemberManage(position);
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