package com.sugarman.myb.ui.dialogs.sendVkInvitation;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

/**
 * Created by nikita on 29.09.2017.
 */
@StateStrategyType(AddToEndSingleStrategy.class) public interface ISendVkInvitationDialogView
    extends MvpView {
  void doAction();
}
