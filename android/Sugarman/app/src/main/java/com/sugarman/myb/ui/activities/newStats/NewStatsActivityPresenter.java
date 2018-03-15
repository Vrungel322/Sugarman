package com.sugarman.myb.ui.activities.newStats;

import com.arellomobile.mvp.InjectViewState;
import com.sugarman.myb.App;
import com.sugarman.myb.base.BasicPresenter;

/**
 * Created by nikita on 15.03.2018.
 */
@InjectViewState
public class NewStatsActivityPresenter extends BasicPresenter<INewStatsActivityView> {
  @Override protected void inject() {
    App.getAppComponent().inject(this);
  }
}
