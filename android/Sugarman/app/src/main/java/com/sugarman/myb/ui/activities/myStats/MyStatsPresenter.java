package com.sugarman.myb.ui.activities.myStats;

import com.sugarman.myb.App;
import com.sugarman.myb.base.BasicPresenter;
import com.sugarman.myb.ui.activities.profile.IProfileActivityView;

/**
 * Created by yegoryeriomin on 2/28/18.
 */

public class MyStatsPresenter extends BasicPresenter<IMyStatsActivityView> {
  @Override protected void inject() {
    App.getAppComponent().inject(this);
  }
}
