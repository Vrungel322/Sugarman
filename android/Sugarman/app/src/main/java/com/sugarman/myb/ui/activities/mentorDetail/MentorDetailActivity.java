package com.sugarman.myb.ui.activities.mentorDetail;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import com.sugarman.myb.R;
import com.sugarman.myb.models.mentor.MentorEntity;
import timber.log.Timber;

public class MentorDetailActivity extends AppCompatActivity {
  private MentorEntity mMentorEntity;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_mentor_detail);
    mMentorEntity = getIntent().getExtras().getParcelable(MentorEntity.MENTOR_ENTITY);
    Timber.e(mMentorEntity.getMentorName() + " " + mMentorEntity.getMentorSkills().size());
  }
}
