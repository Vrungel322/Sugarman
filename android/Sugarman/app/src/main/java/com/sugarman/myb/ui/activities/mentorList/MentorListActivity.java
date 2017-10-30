package com.sugarman.myb.ui.activities.mentorList;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import butterknife.BindView;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.sugarman.myb.R;
import com.sugarman.myb.base.BasicActivity;
import com.sugarman.myb.models.mentor.MentorEntity;
import com.sugarman.myb.ui.activities.mentorDetail.MentorDetailActivity;
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
          Intent intent = new Intent(MentorListActivity.this, MentorDetailActivity.class);
          intent.putExtra(MentorEntity.MENTOR_ENTITY,mMentorsListAdapter.getItem(position));
          startActivity(intent);
        });
  }

  @Override public void fillMentorsList(List<MentorEntity> mentorEntities) {
    mMentorsListAdapter.setMentorEntity(mentorEntities);
  }
}
