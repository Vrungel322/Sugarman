package com.sugarman.myb.ui.activities.mentorDetail;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import butterknife.BindView;
import butterknife.OnClick;
import com.sugarman.myb.R;
import com.sugarman.myb.base.BasicActivity;
import com.sugarman.myb.models.mentor.MentorEntity;
import timber.log.Timber;

public class MentorDetailActivity extends BasicActivity {
  private MentorEntity mMentorEntity;

  @BindView(R.id.iv_back) ImageView ivBack;

  @OnClick(R.id.iv_back) public void onBackPressed()
  {
    finish();
  }

  @Override protected void onCreate(Bundle savedInstanceState) {
    setContentView(R.layout.activity_mentor_detail);
    super.onCreate(savedInstanceState);
    mMentorEntity = getIntent().getExtras().getParcelable(MentorEntity.MENTOR_ENTITY);
    Timber.e(mMentorEntity.getMentorName() + " " + mMentorEntity.getMentorSkills().size());
  }
}
