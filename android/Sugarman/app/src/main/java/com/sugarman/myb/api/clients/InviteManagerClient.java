package com.sugarman.myb.api.clients;

import com.sugarman.myb.App;
import com.sugarman.myb.listeners.ApiBaseListener;
import com.sugarman.myb.listeners.ApiManageInvitesListener;
import java.lang.ref.WeakReference;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InviteManagerClient extends BaseApiClient {

  private static final String TAG = InviteManagerClient.class.getName();

  private final Callback<Object> mDeclineCallback = new Callback<Object>() {

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
            ((ApiManageInvitesListener) clientListener.get()).onApiDeclineInviteFailure(
                errorMessage);
          }
        } else {
          ((ApiManageInvitesListener) clientListener.get()).onApiDeclineInviteSuccess();
        }
      } else {
        listenerNotRegistered(TAG);
      }
    }

    @Override public void onFailure(Call<Object> call, Throwable t) {
      if (clientListener.get() != null) {
        String message = requestFailure(TAG, t);
        ((ApiManageInvitesListener) clientListener.get()).onApiDeclineInviteFailure(message);
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
          ((ApiManageInvitesListener) clientListener.get()).onApiAcceptInviteSuccess();
        } else if (errorBody != null) {
          String errorMessage = parseErrorBody(errorBody);
          responseFailure(TAG, errorMessage);
          if (response.code() == 401) {
            clientListener.get().onApiUnauthorized();
          } else {
            ((ApiManageInvitesListener) clientListener.get()).onApiAcceptInviteFailure(
                errorMessage);
          }
        } else {
          responseIsNull(TAG);
          ((ApiManageInvitesListener) clientListener.get()).onApiAcceptInviteFailure(
              RESPONSE_IS_NULL);
        }
      } else {
        listenerNotRegistered(TAG);
      }
    }

    @Override public void onFailure(Call<Object> call, Throwable t) {
      if (clientListener.get() != null) {
        String message = requestFailure(TAG, t);
        ((ApiManageInvitesListener) clientListener.get()).onApiAcceptInviteFailure(message);
      } else {
        listenerNotRegistered(TAG);
      }
    }
  };

  @Override public void registerListener(ApiBaseListener listener) {
    clientListener = new WeakReference<>(listener);
  }

  public void decline(String inviteId) {
    Call<Object> call = App.getApiInstance().declineInvite(inviteId);
    call.enqueue(mDeclineCallback);
  }

  public void accept(String inviteId) {
    Call<Object> call = App.getApiInstance().acceptInvite(inviteId);
    call.enqueue(mAcceptCallback);
  }
}
