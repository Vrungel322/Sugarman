package com.sugarman.myb.api.clients;

import com.sugarman.myb.App;
import com.sugarman.myb.listeners.ApiBaseListener;
import com.sugarman.myb.listeners.ApiMarkNotificationListener;
import java.lang.ref.WeakReference;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MarkNotificationClient extends BaseApiClient {

  private static final String TAG = MarkNotificationClient.class.getName();

  private String notificationId;

  private Call<Object> call;

  private final Callback<Object> mCallback = new Callback<Object>() {

    @Override public void onResponse(Call<Object> call, Response<Object> response) {
      //Object dataResponse = response.body(); // null response - expected value
      ResponseBody errorBody = response.errorBody();

      if (clientListener.get() != null) {
        if (errorBody != null) {
          String errorMessage = parseErrorBody(errorBody);
          responseFailure(TAG, errorMessage);
          if (response.code() == 401) {
            clientListener.get().onApiUnauthorized();
          } else if (response.code() == OLD_VERSION_CODE) {
            clientListener.get().onUpdateOldVersion();
          } else {
            ((ApiMarkNotificationListener) clientListener.get()).onApiMarkNotificationFailure(
                notificationId, errorMessage);
          }
        } else {
          ((ApiMarkNotificationListener) clientListener.get()).onApiMarkNotificationSuccess(
              notificationId);
        }
      } else {
        listenerNotRegistered(TAG);
      }

      notificationId = "";
    }

    @Override public void onFailure(Call<Object> call, Throwable t) {
      if (clientListener.get() != null) {
        String message = requestFailure(TAG, t);
        if (!call.isCanceled()) {
          ((ApiMarkNotificationListener) clientListener.get()).onApiMarkNotificationFailure(
              notificationId, message);
        }
      } else {
        listenerNotRegistered(TAG);
      }

      notificationId = "";
    }
  };

  @Override public void registerListener(ApiBaseListener listener) {
    clientListener = new WeakReference<>(listener);
  }

  public void mark(String notificationId) {
    this.notificationId = notificationId;

    if (call != null) {
      call.cancel();
    }

    call = App.getApiInstance().markNotification(notificationId);
    call.enqueue(mCallback);
  }
}