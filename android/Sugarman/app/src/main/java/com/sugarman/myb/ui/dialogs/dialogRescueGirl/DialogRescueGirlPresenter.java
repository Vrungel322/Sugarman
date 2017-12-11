package com.sugarman.myb.ui.dialogs.dialogRescueGirl;

import com.arellomobile.mvp.InjectViewState;
import com.sugarman.myb.App;
import com.sugarman.myb.base.BasicPresenter;

/**
 * Created by nikita on 11.12.2017.
 */
@InjectViewState
public class DialogRescueGirlPresenter extends BasicPresenter<IDialogRescueGirlView> {
  @Override protected void inject() {
    App.getAppComponent().inject(this);
  }
}
