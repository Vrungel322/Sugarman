package com.sugarman.myb.ui.activities.invitesScreen;

import com.arellomobile.mvp.InjectViewState;
import com.sugarman.myb.App;
import com.sugarman.myb.base.BasicPresenter;

/**
 * Created by nikita on 02.03.2018.
 */
@InjectViewState public class InvitesActivityPresenter
    extends BasicPresenter<IInvitesActivityView> {
  @Override protected void inject() {
    App.getAppComponent().inject(this);
  }
}
