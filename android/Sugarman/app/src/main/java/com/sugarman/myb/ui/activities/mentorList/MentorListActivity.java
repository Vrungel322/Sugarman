package com.sugarman.myb.ui.activities.mentorList;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import butterknife.BindView;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.sugarman.myb.R;
import com.sugarman.myb.base.BasicActivity;
import com.sugarman.myb.models.mentor.MentorEntity;
import com.sugarman.myb.utils.ItemClickSupport;
import java.util.List;
import timber.log.Timber;

public class MentorListActivity extends BasicActivity implements IMentorListActivityView {
  @InjectPresenter MentorListActivityPresenter mPresenter;
  @BindView(R.id.rvMentors) RecyclerView mRecyclerViewMentors;
  private MentorsListAdapter mMentorsListAdapter;

  @Override protected void onCreate(Bundle savedInstanceState) {
    setContentView(R.layout.activity_mentor_list);
    super.onCreate(savedInstanceState);
  }

  @Override public void setUpUI() {
    mMentorsListAdapter = new MentorsListAdapter();
    mRecyclerViewMentors.setLayoutManager(new LinearLayoutManager(this));
    mRecyclerViewMentors.setAdapter(mMentorsListAdapter);
    ItemClickSupport.addTo(mRecyclerViewMentors)
        .setOnItemClickListener((recyclerView, position, v) -> {
      Timber.e("RV Clicked" + position+ mMentorsListAdapter.getItem(position).getMentorName());
        });
  }

  @Override public void fillMentorsList(List<MentorEntity> mentorEntities) {
    Timber.e(String.valueOf(mentorEntities.size()));
    mMentorsListAdapter.setMentorEntity(mentorEntities);
  }
}
