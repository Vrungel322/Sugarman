package com.sugarman.myb.ui.activities.approveOtp;

import android.text.TextUtils;
import android.util.Log;
import com.arellomobile.mvp.InjectViewState;
import com.google.gson.JsonSyntaxException;
import com.sugarman.myb.App;
import com.sugarman.myb.api.clients.BaseApiClient;
import com.sugarman.myb.api.models.responses.ApproveOtpResponse;
import com.sugarman.myb.api.models.responses.ErrorResponse;
import com.sugarman.myb.base.BasicPresenter;
import com.sugarman.myb.utils.StringHelper;
import com.sugarman.myb.utils.ThreadSchedulers;
import java.io.IOException;
import okhttp3.ResponseBody;
import rx.Subscription;
import timber.log.Timber;

/**
 * Created by nikita on 30.11.2017.
 */
@InjectViewState public class ApproveOtpActivityPresenter
    extends BasicPresenter<IApproveOtpActivityView> {
  @Override protected void inject() {
    App.getAppComponent().inject(this);
  }

  public void approveOtp(String userId, String phoneNumberStr, String otp) {
    Subscription subscription = mDataManager.approveOtp(userId, phoneNumberStr, otp)
        .compose(ThreadSchedulers.applySchedulers())
        .subscribe(otpResponseResponse -> {
          ApproveOtpResponse dataResponse = otpResponseResponse.body();
          ResponseBody errorBody = otpResponseResponse.errorBody();

          if (dataResponse != null) {
            getViewState().onApiApproveOtpSuccess(dataResponse);
          } else if (errorBody != null) {
            String errorMessage = parseErrorBody(errorBody);
            Timber.e("response is failure: " + errorMessage);
            if (otpResponseResponse.code() == 401) {
              getViewState().onApiUnauthorized();
            } else if (otpResponseResponse.code() == BaseApiClient.OLD_VERSION_CODE) {
              getViewState().onUpdateOldVersion();
            } else {
              getViewState().onApiRefreshUserDataFailure(errorMessage);
            }
          } else {
            Timber.e("response is null");
            getViewState().onApiRefreshUserDataFailure(BaseApiClient.RESPONSE_IS_NULL);
          }
        }, throwable -> {
          Log.e("Token", "OCHKO" + throwable.getLocalizedMessage());
          String message = BaseApiClient.requestFailure("", throwable);
          getViewState().onApiRefreshUserDataFailure(message);
        });
    addToUnsubscription(subscription);
  }

  String parseErrorBody(ResponseBody errorBody) {
    return getErrorMessage(errorBody);
  }

  private String getErrorMessage(ResponseBody errorBody) {
    String error = "";
    String message;
    try {
      error = errorBody.string();
    } catch (IOException e) {
      Timber.e("failure parse error body", e);
    }
    if (TextUtils.isEmpty(error)) {
      message = BaseApiClient.FAILURE_PARSE_ERROR_RESPONSE;
    } else {
      try {
        message = App.getGsonInstance().fromJson(error, ErrorResponse.class).getMessage();
      } catch (JsonSyntaxException e) {
        String title = StringHelper.getApiErrorTitle(error);
        String errorMessage = StringHelper.getApiErrorMessage(error);
        if (TextUtils.isEmpty(title) && TextUtils.isEmpty(errorMessage)) {
          Timber.e("failure parse error", e);
          message = BaseApiClient.FAILURE_PARSE_ERROR_RESPONSE;
        } else {
          message = title + ". " + errorMessage;
        }
      }
    }
    return message;
  }
}
