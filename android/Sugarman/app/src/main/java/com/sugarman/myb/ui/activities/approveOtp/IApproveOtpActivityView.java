package com.sugarman.myb.ui.activities.approveOtp;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;
import com.sugarman.myb.api.models.responses.ApproveOtpResponse;

/**
 * Created by nikita on 30.11.2017.
 */
@StateStrategyType(AddToEndSingleStrategy.class) public interface IApproveOtpActivityView
    extends MvpView {
  void onApiApproveOtpSuccess(ApproveOtpResponse dataResponse);

  void onApiUnauthorized();

  void onUpdateOldVersion();

  void onApiRefreshUserDataFailure(String errorMessage);
}
