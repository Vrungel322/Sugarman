package com.sugarman.myb.ui.dialogs.dialogRescueGirlCongratulations;

import com.arellomobile.mvp.InjectViewState;
import com.sugarman.myb.App;
import com.sugarman.myb.base.BasicPresenter;

/**
 * Created by nikita on 14.12.2017.
 */
@InjectViewState public class DialogRescueGirCongratulationsPresenter
    extends BasicPresenter<IDialogRescueGirlCongratulationsView> {
  @Override protected void inject() {
    App.getAppComponent().inject(this);
  }
}
