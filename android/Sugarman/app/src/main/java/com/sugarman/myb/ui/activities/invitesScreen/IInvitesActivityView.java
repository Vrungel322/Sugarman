package com.sugarman.myb.ui.activities.invitesScreen;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;
import com.sugarman.myb.api.models.responses.me.invites.Invite;
import java.util.List;

/**
 * Created by nikita on 02.03.2018.
 */
@StateStrategyType(AddToEndSingleStrategy.class) public interface IInvitesActivityView
    extends MvpView {
  void declineInviteAction();

  void errorMsg(String errorMsg);

  void acceptInviteAction();

  void showInvites(List<Invite> invites);
}
