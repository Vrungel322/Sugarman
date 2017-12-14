package com.sugarman.myb.ui.dialogs.dialogRescueBoldManKick;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

/**
 * Created by nikita on 14.12.2017.
 */
@StateStrategyType(AddToEndSingleStrategy.class) public interface IDialogRescueBoldManKickView
    extends MvpView {
  void superKickResponse();
}
