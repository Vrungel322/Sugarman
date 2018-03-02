package com.sugarman.myb.api.clients;

import android.util.Log;
import com.sugarman.myb.App;
import com.sugarman.myb.api.models.requests.SendFirebaseTokenRequest;
import com.sugarman.myb.api.models.responses.devices.DevicesResponse;
import com.sugarman.myb.constants.Constants;
import com.sugarman.myb.listeners.ApiBaseListener;
import com.sugarman.myb.listeners.ApiSendFirebaseTokenListener;
import java.lang.ref.WeakReference;
import java.util.TimeZone;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class SendFirebaseTokenClient extends BaseApiClient {

  private static final String TAG = SendFirebaseTokenClient.class.getName();

  private final Callback<DevicesResponse> mCallback = new Callback<DevicesResponse>() {

    @Override
    public void onResponse(Call<DevicesResponse> call, Response<DevicesResponse> response) {
      DevicesResponse dataResponse = response.body();
      ResponseBody errorBody = response.errorBody();
      Log.d("fcm", "fcm token sent. Error: " + errorBody);

      if (clientListener.get() != null) {
        if (dataResponse != null) {
          Timber.e("dataResponse OK");
          ((ApiSendFirebaseTokenListener) clientListener.get()).onApiSendFirebaseTokenSuccess();
        } else if (errorBody != null) {
          String errorMessage = parseErrorBody(errorBody);
          responseFailure(TAG, errorMessage);
          if (response.code() == 401) {
            clientListener.get().onApiUnauthorized();
          } else if (response.code() == OLD_VERSION_CODE) {
            clientListener.get().onUpdateOldVersion();
          } else {
            ((ApiSendFirebaseTokenListener) clientListener.get()).onApiSendFirebaseTokenFailure(
                errorMessage);
          }
        } else {
          responseIsNull(TAG);
          ((ApiSendFirebaseTokenListener) clientListener.get()).onApiSendFirebaseTokenFailure(
              RESPONSE_IS_NULL);
        }
      } else {
        listenerNotRegistered(TAG);
      }
    }

    @Override public void onFailure(Call<DevicesResponse> call, Throwable t) {
      Log.d("fcm", "fcm token sent failure. Error: " + t);
      if (clientListener.get() != null) {
        String message = requestFailure(TAG, t);
        ((ApiSendFirebaseTokenListener) clientListener.get()).onApiSendFirebaseTokenFailure(
            message);
      } else {
        listenerNotRegistered(TAG);
      }
    }
  };

  @Override public void registerListener(ApiBaseListener listener) {
    clientListener = new WeakReference<>(listener);
  }

  public void sendFirebaseToken(String token) {
    SendFirebaseTokenRequest request = new SendFirebaseTokenRequest();
    request.setFirebaseToken(token);
    request.setDevelopment(false);
    request.setPlatform(Constants.PLATFORM);
    TimeZone tz = TimeZone.getDefault();
    request.setTimezone(tz.getID());

    Log.d("fcm", "send fcm token: " + token);

    Call<DevicesResponse> call = App.getApiInstance().sendFirebaseToken(request);
    call.enqueue(mCallback);
  }
}
