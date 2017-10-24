package com.sugarman.myb.api.clients;

import android.util.Log;
import com.sugarman.myb.App;
import com.sugarman.myb.api.models.requests.ResendMessageRequest;
import com.sugarman.myb.api.models.responses.ResendMessageResponse;
import com.sugarman.myb.listeners.ApiBaseListener;
import com.sugarman.myb.listeners.ApiRefreshUserDataListener;
import java.lang.ref.WeakReference;
import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class ResendMessageClient extends BaseApiClient {

  private static final String TAG = ResendMessageClient.class.getName();

  private final Callback<ResendMessageResponse> mCallback = new Callback<ResendMessageResponse>() {

    @Override public void onResponse(Call<ResendMessageResponse> call,
        Response<ResendMessageResponse> response) {
      ResendMessageResponse dataResponse = response.body();
      ResponseBody errorBody = response.errorBody();

      Log.e("Token", "ATLICHNA");
      Timber.e("onResponce");
    }

    @Override public void onFailure(Call<ResendMessageResponse> call, Throwable t) {
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

  public void resendMessage(String phoneNumber) {

    MultipartBody.Part filePart = null;

    ResendMessageRequest request = new ResendMessageRequest();
    request.setPhoneNumber(phoneNumber);
    Log.e("Request", request.toString());

    Call<ResendMessageResponse> call = App.getApiInstance().resendMessage(request);
    call.enqueue(mCallback);
  }
}
