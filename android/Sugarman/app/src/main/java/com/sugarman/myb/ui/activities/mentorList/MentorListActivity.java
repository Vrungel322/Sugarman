package com.sugarman.myb.ui.activities.mentorList;

import android.os.Bundle;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.sugarman.myb.R;
import com.sugarman.myb.base.BasicActivity;
import com.sugarman.myb.models.mentor.MentorEntity;
import java.util.List;
import timber.log.Timber;

public class MentorListActivity extends BasicActivity implements IMentorListActivityView {
  @InjectPresenter MentorListActivityPresenter mPresenter;

  @Override protected void onCreate(Bundle savedInstanceState) {
    setContentView(R.layout.activity_mentor_list);
    super.onCreate(savedInstanceState);
  }

  @Override public void fillMentorsList(List<MentorEntity> mentorEntities) {
    Timber.e(String.valueOf(mentorEntities.size()));
  }
}
