package com.sugarman.myb.ui.activities.requestsScreen;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;
import com.sugarman.myb.api.models.responses.me.requests.Request;
import java.util.List;

/**
 * Created by nikita on 06.03.2018.
 */
@StateStrategyType(AddToEndSingleStrategy.class) public interface IRequestsActivityView
    extends MvpView {
  void declineRequestAction();

  void acceptRequestAction();

  void errorMsg(String message);

  void showRequests(List<Request> request);
}
