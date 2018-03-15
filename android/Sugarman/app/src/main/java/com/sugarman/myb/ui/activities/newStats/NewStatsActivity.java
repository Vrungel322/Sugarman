package com.sugarman.myb.ui.activities.newStats;

import android.os.Bundle;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.sugarman.myb.R;
import com.sugarman.myb.base.BasicActivity;

public class NewStatsActivity extends BasicActivity implements INewStatsActivityView {
  @InjectPresenter NewStatsActivityPresenter mPresenter;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_new_stats);
  }
}
