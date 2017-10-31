package com.sugarman.myb.ui.activities.mentorDetail;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.squareup.picasso.Picasso;
import com.sugarman.myb.R;
import com.sugarman.myb.models.mentor.MemberOfMentorsGroup;
import com.sugarman.myb.ui.views.CropCircleTransformation;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by nikita on 31.10.2017.
 */

public class MentorsFriendAdapter
    extends RecyclerView.Adapter<MentorsFriendAdapter.MentorsFriendViewHolder> {
  private List<MemberOfMentorsGroup> mMemberOfMentorsGroups = new ArrayList<>();

  public void setMemberOfMentorsGroupEntity(List<MemberOfMentorsGroup> memberOfMentorsGroups) {
    mMemberOfMentorsGroups.clear();
    mMemberOfMentorsGroups.addAll(memberOfMentorsGroups);
    notifyDataSetChanged();
  }

  @Override public MentorsFriendViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View v = LayoutInflater.from(parent.getContext())
        .inflate(R.layout.item_member_of_mentors_group, parent, false);
    return new MentorsFriendAdapter.MentorsFriendViewHolder(v);
  }

  @Override public void onBindViewHolder(MentorsFriendViewHolder holder, int position) {
    Picasso.with(holder.mImageViewFriendsAvater.getContext())
        .load(mMemberOfMentorsGroups.get(position).getImgUrl())
        .fit()
        .centerCrop()
        .placeholder(R.drawable.ic_gray_avatar)
        .error(R.drawable.ic_red_avatar)
        .transform(new CropCircleTransformation(0xfff, 4))
        .into(holder.mImageViewFriendsAvater);
    holder.mTextViewFriendsName.setText(mMemberOfMentorsGroups.get(position).getName());
  }

  @Override public int getItemCount() {
    return mMemberOfMentorsGroups.size();
  }

  static class MentorsFriendViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.ivFriendAvatar) ImageView mImageViewFriendsAvater;
    @BindView(R.id.tvFriendsName) TextView mTextViewFriendsName;

    MentorsFriendViewHolder(View view) {
      super(view);
      ButterKnife.bind(this, view);
    }
  }
}
