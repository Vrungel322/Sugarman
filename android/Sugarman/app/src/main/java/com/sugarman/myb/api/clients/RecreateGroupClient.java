package com.sugarman.myb.api.clients;

import com.sugarman.myb.App;
import com.sugarman.myb.listeners.ApiBaseListener;
import com.sugarman.myb.listeners.ApiRecreateGroupListener;
import java.lang.ref.WeakReference;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecreateGroupClient extends BaseApiClient {

  private static final String TAG = RecreateGroupClient.class.getName();

  private final Callback<Object> mCallback = new Callback<Object>() {

    @Override public void onResponse(Call<Object> call, Response<Object> response) {
      Object dataResponse = response.body();
      ResponseBody errorBody = response.errorBody();

      if (clientListener.get() != null) {
        if (dataResponse != null) {
          ((ApiRecreateGroupListener) clientListener.get()).onApiRecreateSuccess();
        } else if (errorBody != null) {
          String errorMessage = parseErrorBody(errorBody);
          responseFailure(TAG, errorMessage);
          if (response.code() == 401) {
            clientListener.get().onApiUnauthorized();
          } else if (response.code() == OLD_VERSION_CODE) {
            clientListener.get().onUpdateOldVersion();
          } else {
            ((ApiRecreateGroupListener) clientListener.get()).onApiRecreateFailure(errorMessage);
          }
        } else {
          responseIsNull(TAG);
          ((ApiRecreateGroupListener) clientListener.get()).onApiRecreateFailure(RESPONSE_IS_NULL);
        }
      } else {
        listenerNotRegistered(TAG);
      }
    }

    @Override public void onFailure(Call<Object> call, Throwable t) {
      if (clientListener.get() != null) {
        String message = requestFailure(TAG, t);
        ((ApiRecreateGroupListener) clientListener.get()).onApiRecreateFailure(message);
      } else {
        listenerNotRegistered(TAG);
      }
    }
  };

  @Override public void registerListener(ApiBaseListener listener) {
    clientListener = new WeakReference<>(listener);
  }

  public void recreate(String trackingId) {
    Call<Object> call = App.getApiInstance().recreateGroup(trackingId);
    call.enqueue(mCallback);
  }
}