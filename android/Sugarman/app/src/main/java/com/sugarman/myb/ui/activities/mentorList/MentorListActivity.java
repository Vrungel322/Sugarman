package com.sugarman.myb.ui.activities.mentorList;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import butterknife.BindView;
import butterknife.OnClick;
import com.appsflyer.AFInAppEventParameterName;
import com.appsflyer.AppsFlyerLib;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.sugarman.myb.App;
import com.sugarman.myb.R;
import com.sugarman.myb.base.BasicActivity;
import com.sugarman.myb.models.mentor.MentorEntity;
import com.sugarman.myb.ui.activities.mentorDetail.MentorDetailActivity;
import com.sugarman.myb.utils.ItemClickSupport;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import timber.log.Timber;

public class MentorListActivity extends BasicActivity implements IMentorListActivityView {
  @InjectPresenter MentorListActivityPresenter mPresenter;
  @BindView(R.id.rvMentors) RecyclerView mRecyclerViewMentors;
  @BindView(R.id.iv_back) ImageView ivBack;
  private MentorsListAdapter mMentorsListAdapter;

  @Override protected void onCreate(Bundle savedInstanceState) {
    setContentView(R.layout.activity_mentor_list);
    super.onCreate(savedInstanceState);

    Map<String, Object> eventValue = new HashMap<>();
    eventValue.put(AFInAppEventParameterName.LEVEL, 9);
    eventValue.put(AFInAppEventParameterName.SCORE, 100);
    AppsFlyerLib.getInstance().trackEvent(App.getInstance().getApplicationContext(), "af_shop_open_mentor_item", eventValue);

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

  @OnClick (R.id.iv_back) public void onClickBack()
  {
    finish();
  }

  @Override public void fillMentorsList(List<MentorEntity> mentorEntities) {
    mMentorsListAdapter.setMentorEntity(mentorEntities);
  }
}
