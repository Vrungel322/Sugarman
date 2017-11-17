package com.sugarman.myb.ui.activities.profile;

import com.arellomobile.mvp.InjectViewState;
import com.sugarman.myb.App;
import com.sugarman.myb.base.BasicPresenter;

/**
 * Created by nikita on 02.10.2017.
 */

@InjectViewState public class ProfileActivityPresenter
    extends BasicPresenter<IProfileActivityView> {
  @Override protected void inject() {
    App.getAppComponent().inject(this);
  }

  public void startAnimation() {

  }
}
