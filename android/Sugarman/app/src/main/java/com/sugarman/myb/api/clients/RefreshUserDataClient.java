package com.sugarman.myb.api.clients;

import android.os.Build;
import android.util.Log;
import com.sugarman.myb.App;
import com.sugarman.myb.api.models.requests.RefreshUserDataRequest;
import com.sugarman.myb.api.models.responses.users.UsersResponse;
import com.sugarman.myb.listeners.ApiBaseListener;
import com.sugarman.myb.listeners.ApiRefreshUserDataListener;
import com.sugarman.myb.utils.SharedPreferenceHelper;
import java.lang.ref.WeakReference;
import java.util.TimeZone;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RefreshUserDataClient extends BaseApiClient {

  private static final String TAG = RefreshUserDataClient.class.getName();

  private final Callback<UsersResponse> mCallback = new Callback<UsersResponse>() {

    @Override public void onResponse(Call<UsersResponse> call, Response<UsersResponse> response) {
      UsersResponse dataResponse = response.body();
      ResponseBody errorBody = response.errorBody();

      Log.e("Token", "ATLICHNA");

      if (clientListener.get() != null) {
        if (dataResponse != null) {
          Log.e("Token", "ATLICHNA 2");
          ((ApiRefreshUserDataListener) clientListener.get()).onApiRefreshUserDataSuccess(
              dataResponse);
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

    @Override public void onFailure(Call<UsersResponse> call, Throwable t) {
      Log.e("Token", t.getLocalizedMessage());
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

  public void refreshUserData(String fbToken, String vkToken, String gToken, String phoneNumber,
      String email, String name, String vkId, String fbId, String pictureUrl) {
    RefreshUserDataRequest request = new RefreshUserDataRequest();
    request.setToken(fbToken);
    request.setVkToken(vkToken);
    request.setgToken(gToken);
    request.setPhoneToken(phoneNumber);
    request.setPhoneNumber(phoneNumber);
    request.setEmail(email);
    request.setName(name);
    request.setVkId(vkId);
    request.setFbId(fbId);
    request.setPictureUrl(pictureUrl);
    request.setTimezone(TimeZone.getDefault().getID());
    request.setVOS(Build.VERSION.RELEASE);
    request.setImei(SharedPreferenceHelper.getIMEI());
    Log.e("Request", request.toString());
    Log.e("token", "refreshUserData rabotaem");

    Call<UsersResponse> call = App.getApiInstance().refreshUserData(request);
    call.enqueue(mCallback);
  }
}
