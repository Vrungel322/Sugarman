package com.sugarman.myb.ui.activities.mentorList;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import com.sugarman.myb.models.mentor.MentorEntity;
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
    //View v = LayoutInflater.from(parent.getContext())
    //    .inflate(R.layout.item_booking_service, parent, false);
    //return new MentorsListAdapter.MentorViewHolder(v);
    return null;
  }

  @Override public void onBindViewHolder(MentorViewHolder holder, int position) {

  }

  @Override public int getItemCount() {
    return mMentorEntities.size();
  }

  static class MentorViewHolder extends RecyclerView.ViewHolder {

    MentorViewHolder(View view) {
      super(view);
      ButterKnife.bind(this, view);
    }
  }
}
