package com.sugarman.myb.ui.dialogs.dialogRescueBoldMan;

import android.content.Intent;
import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

/**
 * Created by nikita on 11.12.2017.
 */
@StateStrategyType(AddToEndSingleStrategy.class)
public interface IDialogRescueBoldManView   extends MvpView {
  void enableButton();

  void showCongratulationsDialog();

  void showProgressBar();

  void hideProgressBar();

  void handleActivityResult(int requestCode, int resultCode, Intent data);
}
