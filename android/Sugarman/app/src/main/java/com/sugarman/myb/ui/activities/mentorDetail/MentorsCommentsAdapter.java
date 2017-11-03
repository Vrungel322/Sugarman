package com.sugarman.myb.ui.activities.mentorDetail;

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
import com.sugarman.myb.models.mentor.comments.MentorsCommentsEntity;
import com.sugarman.myb.ui.views.CropCircleTransformation;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by nikita on 31.10.2017.
 */

public class MentorsCommentsAdapter
    extends RecyclerView.Adapter<MentorsCommentsAdapter.MentorsFriendViewHolder> {
  private List<MentorsCommentsEntity> mMentorsCommentsEntities = new ArrayList<>();

  public void setMentorsCommentsEntities(List<MentorsCommentsEntity> mentorsComments) {
    mMentorsCommentsEntities.clear();
    mMentorsCommentsEntities.addAll(mentorsComments);
    notifyDataSetChanged();
  }

  @Override public MentorsFriendViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View v =
        LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment_card, parent, false);
    return new MentorsCommentsAdapter.MentorsFriendViewHolder(v);
  }

  @Override public void onBindViewHolder(MentorsFriendViewHolder holder, int position) {

    Picasso.with(holder.mImageViewCommentAuthor.getContext())
        .load(mMentorsCommentsEntities.get(position).getAuthorsImg())
        .fit()
        .centerCrop()
        .placeholder(R.drawable.ic_gray_avatar)
        .error(R.drawable.ic_red_avatar)
        .transform(new CropCircleTransformation(0xff0, 4))
        .into(holder.mImageViewCommentAuthor);

    holder.mTextViewAuthotName.setText(mMentorsCommentsEntities.get(position).getAuthorsName());
    holder.mRatingBar.setRating(
        Float.valueOf(mMentorsCommentsEntities.get(position).getAuthorsRating()));
    holder.mTextViewCommentText.setText(mMentorsCommentsEntities.get(position).getAuthorsComment());
  }

  @Override public int getItemCount() {
    return mMentorsCommentsEntities.size();
  }

  static class MentorsFriendViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.ivCommentAuthor) ImageView mImageViewCommentAuthor;
    @BindView(R.id.tvAuthorName) TextView mTextViewAuthotName;
    @BindView(R.id.acrbAuthorsRating) AppCompatRatingBar mRatingBar;
    @BindView(R.id.tvCommentText) TextView mTextViewCommentText;

    MentorsFriendViewHolder(View view) {
      super(view);
      ButterKnife.bind(this, view);
    }
  }
}
