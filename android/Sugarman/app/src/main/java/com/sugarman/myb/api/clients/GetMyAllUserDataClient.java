package com.sugarman.myb.api.clients;

import com.sugarman.myb.App;
import com.sugarman.myb.api.models.responses.AllMyUserDataResponse;
import com.sugarman.myb.constants.Constants;
import com.sugarman.myb.listeners.ApiBaseListener;
import com.sugarman.myb.listeners.ApiGetMyAllUserInfoListener;
import com.sugarman.myb.utils.SharedPreferenceHelper;
import java.lang.ref.WeakReference;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class GetMyAllUserDataClient extends BaseApiClient {

  private static final String TAG = GetMyAllUserDataClient.class.getName();

  private final Callback<AllMyUserDataResponse> mCallback = new Callback<AllMyUserDataResponse>() {

    @Override public void onResponse(Call<AllMyUserDataResponse> call,
        Response<AllMyUserDataResponse> response) {
      AllMyUserDataResponse dataResponse = response.body();
      ResponseBody errorBody = response.errorBody();

      if (clientListener.get() != null) {
        if (dataResponse != null) {
          Timber.e("limitGMAUDC "+response.body().getUser().getGroupsLimit());
          SharedPreferenceHelper.saveGroupsLimit(response.body().getUser().getGroupsLimit());
          if (response.code() == Constants.RESPONSE_228) {
            SharedPreferenceHelper.setOTPStatus(dataResponse.getUser().getNeedOTP());
            ((ApiGetMyAllUserInfoListener) clientListener.get()).onApiGetMyAllUserInfoNeedApproveOTP(
                dataResponse.getUser().getPhoneNumber());
          } else {
            ((ApiGetMyAllUserInfoListener) clientListener.get()).onApiGetMyAllUserInfoSuccess(
                dataResponse);
          }
        } else if (errorBody != null) {
          String errorMessage = parseErrorBody(errorBody);
          responseFailure(TAG, errorMessage);
          if (response.code() == 401) {
            clientListener.get().onApiUnauthorized();
          } else if (response.code() == OLD_VERSION_CODE) {
            clientListener.get().onUpdateOldVersion();
          } else if (response.code() == OLD_VERSION_CODE) {
            clientListener.get().onUpdateOldVersion();
          } else {
            Timber.e("First else");
            ((ApiGetMyAllUserInfoListener) clientListener.get()).onApiGetMyAllUserInfoFailure(
                errorMessage);
            //getMyAllUserData();
          }
        } else {
          responseIsNull(TAG);
          Timber.e("Second else");
          ((ApiGetMyAllUserInfoListener) clientListener.get()).onApiGetMyAllUserInfoFailure(
              RESPONSE_IS_NULL);
        }
      } else {
        listenerNotRegistered(TAG);
      }
    }

    @Override public void onFailure(Call<AllMyUserDataResponse> call, Throwable t) {
      if (clientListener.get() != null) {
        String message = requestFailure(TAG, t);
        Timber.e("OnFailure " + message);
        ((ApiGetMyAllUserInfoListener) clientListener.get()).onApiGetMyAllUserInfoFailure(message);
      } else {
        listenerNotRegistered(TAG);
      }
    }
  };

  @Override public void registerListener(ApiBaseListener listener) {
    clientListener = new WeakReference<>(listener);
  }

  public void getMyAllUserData() {
    Call<AllMyUserDataResponse> call = App.getApiInstance().getMyAllUserData();
    call.enqueue(mCallback);
  }
}
