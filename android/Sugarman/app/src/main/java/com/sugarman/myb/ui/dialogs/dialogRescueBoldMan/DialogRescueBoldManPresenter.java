package com.sugarman.myb.ui.dialogs.dialogRescueBoldMan;

import com.arellomobile.mvp.InjectViewState;
import com.sugarman.myb.App;
import com.sugarman.myb.base.BasicPresenter;

/**
 * Created by nikita on 11.12.2017.
 */
@InjectViewState
public class DialogRescueBoldManPresenter extends BasicPresenter<IDialogRescueBoldManView> {
  @Override protected void inject() {
    App.getAppComponent().inject(this);
  }
}
