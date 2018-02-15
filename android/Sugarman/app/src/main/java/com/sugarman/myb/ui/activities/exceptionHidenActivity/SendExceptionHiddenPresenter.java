package com.sugarman.myb.ui.activities.exceptionHidenActivity;

import com.arellomobile.mvp.InjectViewState;
import com.sugarman.myb.App;
import com.sugarman.myb.base.BasicPresenter;

/**
 * Created by nikita on 15.02.2018.
 */
@InjectViewState public class SendExceptionHiddenPresenter
    extends BasicPresenter<ISendExceptionHiddenView> {
  @Override protected void inject() {
    App.getAppComponent().inject(this);
  }
}
