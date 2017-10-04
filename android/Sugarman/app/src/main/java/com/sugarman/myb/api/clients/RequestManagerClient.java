package com.sugarman.myb.api.clients;

import com.sugarman.myb.App;
import com.sugarman.myb.listeners.ApiBaseListener;
import com.sugarman.myb.listeners.ApiManageRequestsListener;
import java.lang.ref.WeakReference;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RequestManagerClient extends BaseApiClient {

  private static final String TAG = RequestManagerClient.class.getName();

  private final Callback<Object> mDeclineCallback = new Callback<Object>() {

    @Override public void onResponse(Call<Object> call, Response<Object> response) {
      Object dataResponse = response.body();

      ResponseBody errorBody = response.errorBody();

      if (clientListener.get() != null) {
        if (dataResponse != null) {
          ((ApiManageRequestsListener) clientListener.get()).onApiDeclineRequestSuccess();
        } else if (errorBody != null) {
          String errorMessage = parseErrorBody(errorBody);
          responseFailure(TAG, errorMessage);
          if (response.code() == 401) {
            clientListener.get().onApiUnauthorized();
          } else {
            ((ApiManageRequestsListener) clientListener.get()).onApiDeclineRequestFailure(
                errorMessage);
          }
        } else {
          responseIsNull(TAG);
          ((ApiManageRequestsListener) clientListener.get()).onApiDeclineRequestFailure(
              RESPONSE_IS_NULL);
        }
      } else {
        listenerNotRegistered(TAG);
      }
    }

    @Override public void onFailure(Call<Object> call, Throwable t) {
      if (clientListener.get() != null) {
        String message = requestFailure(TAG, t);
        ((ApiManageRequestsListener) clientListener.get()).onApiDeclineRequestFailure(message);
      } else {
        listenerNotRegistered(TAG);
      }
    }
  };

  private final Callback<Object> mAcceptCallback = new Callback<Object>() {

    @Override public void onResponse(Call<Object> call, Response<Object> response) {
      Object dataResponse = response.body();

      ResponseBody errorBody = response.errorBody();

      if (clientListener.get() != null) {
        if (dataResponse != null) {
          ((ApiManageRequestsListener) clientListener.get()).onApiAcceptRequestSuccess();
        } else if (errorBody != null) {
          String errorMessage = parseErrorBody(errorBody);
          responseFailure(TAG, errorMessage);
          if (response.code() == 401) {
            clientListener.get().onApiUnauthorized();
          } else {
            ((ApiManageRequestsListener) clientListener.get()).onApiAcceptRequestFailure(
                errorMessage);
          }
        } else {
          responseIsNull(TAG);
          ((ApiManageRequestsListener) clientListener.get()).onApiAcceptRequestFailure(
              RESPONSE_IS_NULL);
        }
      } else {
        listenerNotRegistered(TAG);
      }
    }

    @Override public void onFailure(Call<Object> call, Throwable t) {
      if (clientListener.get() != null) {
        String message = requestFailure(TAG, t);
        ((ApiManageRequestsListener) clientListener.get()).onApiAcceptRequestFailure(message);
      } else {
        listenerNotRegistered(TAG);
      }
    }
  };

  @Override public void registerListener(ApiBaseListener listener) {
    clientListener = new WeakReference<>(listener);
  }

  public void decline(String requestId) {
    Call<Object> call = App.getApiInstance().declineRequest(requestId);
    call.enqueue(mDeclineCallback);
  }

  public void accept(String requestId) {
    Call<Object> call = App.getApiInstance().acceptRequest(requestId);
    call.enqueue(mAcceptCallback);
  }
}
