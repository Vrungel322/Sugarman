package com.sugarman.myb.ui.dialogs.dialogCuteRule;

import com.arellomobile.mvp.InjectViewState;
import com.sugarman.myb.App;
import com.sugarman.myb.base.BasicPresenter;

/**
 * Created by nikita on 30.01.2018.
 */
@InjectViewState public class DialogCuteRulePresenter extends BasicPresenter<IDialogCuteRuleView> {
  @Override protected void inject() {
    App.getAppComponent().inject(this);
  }
}
