package com.sugarman.myb.api.clients;

import android.util.Log;
import com.sugarman.myb.App;
import com.sugarman.myb.api.models.requests.ApproveOtpRequest;
import com.sugarman.myb.api.models.responses.ApproveOtpResponse;
import com.sugarman.myb.listeners.ApiApproveOtp;
import com.sugarman.myb.listeners.ApiBaseListener;
import com.sugarman.myb.listeners.ApiRefreshUserDataListener;
import java.lang.ref.WeakReference;
import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class ApproveOtpClient extends BaseApiClient {

  private static final String TAG = ApproveOtpClient.class.getName();

  private final Callback<ApproveOtpResponse> mCallback = new Callback<ApproveOtpResponse>() {

    @Override
    public void onResponse(Call<ApproveOtpResponse> call, Response<ApproveOtpResponse> response) {
      ApproveOtpResponse dataResponse = response.body();
      ResponseBody errorBody = response.errorBody();

      Log.e("Token", "ATLICHNA");
      Timber.e("onResponce");

      if (clientListener.get() != null) {
        Timber.e("onResponce apiClient != null");

        if (dataResponse != null) {
          Timber.e("onResponce dataResponse != null");

          ((ApiApproveOtp) clientListener.get()).onApiApproveOtpSuccess(dataResponse);
        } else if (errorBody != null) {
          String errorMessage = parseErrorBody(errorBody);
          responseFailure(TAG, errorMessage);
          if (response.code() == 401) {
            clientListener.get().onApiUnauthorized();
          } else if (response.code() == OLD_VERSION_CODE) {
            clientListener.get().onUpdateOldVersion();
          } else {
            Log.e("Token", "NE ATLICHNA NULL");
            Log.e("Token", "HUETA" + errorMessage);
            ((ApiRefreshUserDataListener) clientListener.get()).onApiRefreshUserDataFailure(
                errorMessage);
          }
        } else {
          responseIsNull(TAG);
          ((ApiRefreshUserDataListener) clientListener.get()).onApiRefreshUserDataFailure(
              RESPONSE_IS_NULL);
        }
      } else {
        listenerNotRegistered(TAG);
      }
    }

    @Override public void onFailure(Call<ApproveOtpResponse> call, Throwable t) {
      Log.e("Token", "OCHKO" + t.getLocalizedMessage());
      if (clientListener.get() != null) {
        String message = requestFailure(TAG, t);
        ((ApiRefreshUserDataListener) clientListener.get()).onApiRefreshUserDataFailure(message);
      } else {
        listenerNotRegistered(TAG);
      }
    }
  };

  @Override public void registerListener(ApiBaseListener listener) {
    clientListener = new WeakReference<>(listener);
  }

  public void approveOtp(String userId, String phoneNumber, String otp) {

    MultipartBody.Part filePart = null;

    ApproveOtpRequest request = new ApproveOtpRequest();
    request.setUserId(userId);
    request.setPhoneNumber(phoneNumber);
    request.setPhoneOtp(otp);
    Log.e("Request", request.toString());
    Log.e("token", "editProfileClient rabotaem");

    Call<ApproveOtpResponse> call = App.getApiInstance().approveOtp(request);
    call.enqueue(mCallback);
  }
}
