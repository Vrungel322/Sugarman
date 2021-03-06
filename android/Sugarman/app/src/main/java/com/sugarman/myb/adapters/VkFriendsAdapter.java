package com.sugarman.myb.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.CustomPicasso;
import com.sugarman.myb.R;
import com.sugarman.myb.listeners.ItemMemberActionListener;
import com.sugarman.myb.models.VkFriend;
import com.sugarman.myb.ui.views.CropCircleTransformation;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Y500 on 04.09.2017.
 */

public class VkFriendsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
    implements ItemMemberActionListener {
  private static final String TAG = VkFriendsAdapter.class.getName();
  private final List<VkFriend> mUnselected = new ArrayList<>();
  private final List<VkFriend> mSelected = new ArrayList<>();

  private final Context context;

  private final String add;
  private final String remove;
  private final String added;
  private final String pending;

  private final int colorWhite;
  private final int colorRed;

  public VkFriendsAdapter(Context context) {
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
    return new VkFriendsAdapter.MembersHolder(view, this);
  }

  private VkFriend getValue(int position) {
    if (position >= 0 && position < mSelected.size()) {
      return mSelected.get(position);
    } else if (position >= mSelected.size() && position < getItemCount()) {
      return mUnselected.get(position - mSelected.size());
    } else {
      return null;
    }
  }

  @Override public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
    VkFriend friend = getValue(position);
    if (friend != null) {
      VkFriendsAdapter.MembersHolder membersHolder = (VkFriendsAdapter.MembersHolder) holder;
      membersHolder.ivAvatar.setImageResource(R.drawable.ic_gray_avatar);

      String name = friend.getFirstName() + " " + friend.getLastName();
      membersHolder.tvMemberName.setText(name);

      membersHolder.tvActionBtn.setBackgroundResource(R.drawable.remove);
      membersHolder.tvActionBtn.setTextColor(colorRed);
      membersHolder.ivIndicatorInvitable.setVisibility(View.INVISIBLE);

      boolean isPending = friend.isPending();
      boolean isAdded = friend.isAdded();
      if (isPending || isAdded) {
        membersHolder.tvActionBtn.setText(isPending ? pending : added);
        //membersHolder.tvActionBtn.setBackgroundResource(R.drawable.gray_double_stroke_background);
        membersHolder.tvActionBtn.setBackgroundResource(R.drawable.remove);
        membersHolder.tvActionBtn.setTextColor(colorRed);
      } else {
        membersHolder.tvActionBtn.setText(friend.isSelected() ? remove : add);
        //membersHolder.tvActionBtn.setBackgroundResource(R.drawable.dark_gray_double_stroke_background);
        membersHolder.tvActionBtn.setBackgroundResource(R.drawable.add_and_remove);
        membersHolder.tvActionBtn.setTextColor(colorWhite);
      }

      String url = friend.getPhotoUrl();
      CustomPicasso.with(context)
          .load(url)
          .placeholder(R.drawable.ic_gray_avatar)
          .error(R.drawable.ic_gray_avatar)
          .transform(new CropCircleTransformation(0x00ffffff, 4))
          .into(membersHolder.ivAvatar);
    }
  }

  @Override public int getItemCount() {
    return mSelected.size() + mUnselected.size();
  }

  @Override public void onClickMemberManage(int position) {

    if (position >= 0 && position < getItemCount()) {
      VkFriend friend;
      if (position < mSelected.size()) {
        friend = mSelected.get(position);
      } else {
        friend = mUnselected.get(position - mSelected.size());
      }

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

        Collections.sort(mSelected);
        Collections.sort(mUnselected);
      }
    }

    notifyDataSetChanged();
  }

  public void setValue(List<VkFriend> values) {
    mUnselected.clear();
    mSelected.clear();

    for (VkFriend friend : values) {
      mUnselected.add(friend);
    }

    notifyDataSetChanged();
  }

  private static class MembersHolder extends RecyclerView.ViewHolder
      implements View.OnClickListener {

    private final WeakReference<ItemMemberActionListener> mActionItemListener;

    private final TextView tvMemberName;
    private final TextView tvActionBtn;
    private final ImageView ivAvatar;
    private final ImageView ivIndicatorInvitable;

    MembersHolder(View itemView, ItemMemberActionListener clickItemListener) {
      super(itemView);

      mActionItemListener = new WeakReference<>(clickItemListener);

      ivIndicatorInvitable = (ImageView) itemView.findViewById(R.id.iv_indicataor_invitable);
      tvMemberName = (TextView) itemView.findViewById(R.id.tv_user_name);
      tvActionBtn = (TextView) itemView.findViewById(R.id.tv_remove_add);
      ivAvatar = (ImageView) itemView.findViewById(R.id.iv_avatar);

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