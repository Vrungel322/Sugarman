package com.sugarman.myb.ui.activities.mentorDetail;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.squareup.picasso.CustomPicasso;
import com.sugarman.myb.R;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by nikita on 31.10.2017.
 */

public class MentorsVideosAdapter
    extends RecyclerView.Adapter<MentorsVideosAdapter.MentorsVideosViewHolder> {
  private List<String> mMentorsVideoLinks = new ArrayList<>();

  public void setMentorsVideosEntities(List<String> mentorsVideos) {
    mMentorsVideoLinks.clear();
    mMentorsVideoLinks.addAll(mentorsVideos);
    notifyDataSetChanged();
  }

  @Override public MentorsVideosViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View v =
        LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video_card, parent, false);
    return new MentorsVideosAdapter.MentorsVideosViewHolder(v);
  }

  private String getVideoImage(String videoLink) {
    Uri uri = Uri.parse(videoLink);
    String videoID = uri.getQueryParameter("v");
    String url = "http://img.youtube.com/vi/" + videoID + "/0.jpg";
    return url;
  }

  @Override public void onBindViewHolder(MentorsVideosViewHolder holder, int position) {

    CustomPicasso.with(holder.mImageViewVideoThumbnail.getContext())
        .load(getVideoImage(mMentorsVideoLinks.get(position)))
        .fit()
        .centerCrop()
        .placeholder(R.drawable.ic_gray_avatar)
        .error(R.drawable.ic_red_avatar)
        .into(holder.mImageViewVideoThumbnail);
  }

  @Override public int getItemCount() {
    return mMentorsVideoLinks.size();
  }

  static class MentorsVideosViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.ivVideoThumbnail) ImageView mImageViewVideoThumbnail;

    MentorsVideosViewHolder(View view) {
      super(view);
      ButterKnife.bind(this, view);
    }
  }
}
