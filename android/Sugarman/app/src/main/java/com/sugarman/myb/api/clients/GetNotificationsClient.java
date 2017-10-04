package com.sugarman.myb.api.clients;

import com.sugarman.myb.App;
import com.sugarman.myb.api.models.responses.me.notifications.Notification;
import com.sugarman.myb.api.models.responses.me.notifications.NotificationsResponse;
import com.sugarman.myb.listeners.ApiBaseListener;
import com.sugarman.myb.listeners.ApiGetNotificationsListener;
import java.lang.ref.WeakReference;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GetNotificationsClient extends BaseApiClient {

  private static final String TAG = GetNotificationsClient.class.getName();

  private final Callback<NotificationsResponse> mCallback = new Callback<NotificationsResponse>() {

    @Override public void onResponse(Call<NotificationsResponse> call,
        Response<NotificationsResponse> response) {
      NotificationsResponse dataResponse = response.body();
      ResponseBody errorBody = response.errorBody();

      if (clientListener.get() != null) {
        if (dataResponse != null) {
          Notification[] result = dataResponse.getResult();
          ((ApiGetNotificationsListener) clientListener.get()).onApiGetNotificationsSuccess(result);
        } else if (errorBody != null) {
          String errorMessage = parseErrorBody(errorBody);
          responseFailure(TAG, errorMessage);
          if (response.code() == 401) {
            clientListener.get().onApiUnauthorized();
          } else if (response.code() == OLD_VERSION_CODE) {
            clientListener.get().onUpdateOldVersion();
          } else {
            ((ApiGetNotificationsListener) clientListener.get()).onApiGetNotificationsFailure(
                errorMessage);
          }
        } else {
          responseIsNull(TAG);
          ((ApiGetNotificationsListener) clientListener.get()).onApiGetNotificationsFailure(
              RESPONSE_IS_NULL);
        }
      } else {
        listenerNotRegistered(TAG);
      }
    }

    @Override public void onFailure(Call<NotificationsResponse> call, Throwable t) {
      if (clientListener.get() != null) {
        String message = requestFailure(TAG, t);
        ((ApiGetNotificationsListener) clientListener.get()).onApiGetNotificationsFailure(message);
      } else {
        listenerNotRegistered(TAG);
      }
    }
  };

  @Override public void registerListener(ApiBaseListener listener) {
    clientListener = new WeakReference<>(listener);
  }

  public void getNotifications() {
    Call<NotificationsResponse> call = App.getApiInstance().getMyNotifications();
    call.enqueue(mCallback);
  }
}
