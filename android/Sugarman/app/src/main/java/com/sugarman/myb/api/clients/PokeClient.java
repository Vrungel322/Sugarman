package com.sugarman.myb.api.clients;

import com.sugarman.myb.App;
import com.sugarman.myb.api.models.requests.PokeRequest;
import com.sugarman.myb.listeners.ApiBaseListener;
import com.sugarman.myb.listeners.ApiPokeListener;
import java.lang.ref.WeakReference;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PokeClient extends BaseApiClient {

  private static final String TAG = PokeClient.class.getName();

  private final Callback<Object> mCallback = new Callback<Object>() {

    @Override public void onResponse(Call<Object> call, Response<Object> response) {
      Object dataResponse = response.body();
      ResponseBody errorBody = response.errorBody();

      if (clientListener.get() != null) {
        if (dataResponse != null) {
          ((ApiPokeListener) clientListener.get()).onApiPokeSuccess();
        } else if (errorBody != null) {
          String errorMessage = parseErrorBody(errorBody);
          responseFailure(TAG, errorMessage);
          if (response.code() == 401) {
            clientListener.get().onApiUnauthorized();
          } else if (response.code() == OLD_VERSION_CODE) {
            clientListener.get().onUpdateOldVersion();
          } else {
            ((ApiPokeListener) clientListener.get()).onApiPokeFailure(errorMessage);
          }
        } else {
          //responseIsNull(TAG);
          ((ApiPokeListener) clientListener.get()).onApiPokeFailure(RESPONSE_IS_NULL);
        }
      } else {
        listenerNotRegistered(TAG);
      }
    }

    @Override public void onFailure(Call<Object> call, Throwable t) {
      if (clientListener.get() != null) {
        String message = requestFailure(TAG, t);
        ((ApiPokeListener) clientListener.get()).onApiPokeFailure(message);
      } else {
        listenerNotRegistered(TAG);
      }
    }
  };

  @Override public void registerListener(ApiBaseListener listener) {
    clientListener = new WeakReference<>(listener);
  }

  public void poke(String memberId, String trackingId) {
    PokeRequest request = new PokeRequest();
    request.setUserId(memberId);
    request.setTrackingId(trackingId);

    Call<Object> call = App.getApiInstance().poke(request);
    call.enqueue(mCallback);
  }
}