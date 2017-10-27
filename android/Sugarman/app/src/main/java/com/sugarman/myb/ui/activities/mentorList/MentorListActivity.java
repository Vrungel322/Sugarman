package com.sugarman.myb.ui.activities.mentorList;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.sugarman.myb.R;

public class MentorListActivity extends AppCompatActivity implements IMentorListActivityView{

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_mentor_list);
  }
}
