package com.sugarman.myb.ui.activities.mentorList;

import android.support.v7.widget.AppCompatRatingBar;
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
import com.sugarman.myb.models.mentor.MentorEntity;
import com.sugarman.myb.ui.views.MaskTransformation;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by nikita on 27.10.2017.
 */

public class MentorsListAdapter extends RecyclerView.Adapter<MentorsListAdapter.MentorViewHolder> {
  private List<MentorEntity> mMentorEntities = new ArrayList<>();

  public void setMentorEntity(List<MentorEntity> mentorEntities) {
    mMentorEntities.clear();
    mMentorEntities.addAll(mentorEntities);
    notifyDataSetChanged();
  }

  @Override public MentorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mentor, parent, false);
    return new MentorsListAdapter.MentorViewHolder(v);
  }

  @Override public void onBindViewHolder(MentorViewHolder holder, int position) {

    Picasso.with(holder.mImageViewAvatar.getContext())
        .load(mMentorEntities.get(position).getMentorImgUrl())
        .placeholder(R.drawable.ic_gray_avatar)
        .fit()
        .centerCrop()
        .error(R.drawable.ic_gray_avatar)
        .transform(
            new MaskTransformation(holder.mImageViewAvatar.getContext(), R.drawable.profile_mask,
                false, 0x00ffffff))
        .into(holder.mImageViewAvatar);

    holder.mTextViewName.setText(mMentorEntities.get(position).getMentorName());
    if(mMentorEntities.get(position).getMentorRating().isEmpty())
      mMentorEntities.get(position).setMentorRating("0.0");
    holder.mRatingBar.setRating(Float.valueOf(mMentorEntities.get(position).getMentorRating()));
    holder.mTextViewCommentsNumber.setText(mMentorEntities.get(position).getMentorNumComments()
        + " "
        + holder.mTextViewCommentsNumber.getContext().getString(R.string.comments));
    holder.mTextViewDescription.setText(mMentorEntities.get(position).getMentorDescription());
  }

  @Override public int getItemCount() {
    return mMentorEntities.size();
  }

  public MentorEntity getItem(int position) {
    return mMentorEntities.get(position);
  }

  static class MentorViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.iv_avatar) ImageView mImageViewAvatar;
    @BindView(R.id.tv_name) TextView mTextViewName;
    @BindView(R.id.appCompatRatingBar) AppCompatRatingBar mRatingBar;
    @BindView(R.id.tv_comments_number) TextView mTextViewCommentsNumber;
    @BindView(R.id.tv_description) TextView mTextViewDescription;

    MentorViewHolder(View view) {
      super(view);
      ButterKnife.bind(this, view);
    }
  }
}
