package com.sugarman.myb.api.clients;

import com.sugarman.myb.App;
import com.sugarman.myb.api.models.responses.me.invites.Invite;
import com.sugarman.myb.api.models.responses.me.invites.InvitesResponse;
import com.sugarman.myb.listeners.ApiBaseListener;
import com.sugarman.myb.listeners.ApiGetMyInvitesListener;
import java.lang.ref.WeakReference;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GetMyInvitesClient extends BaseApiClient {

  private static final String TAG = GetMyInvitesClient.class.getName();

  private boolean isRefreshTrackings;

  private final Callback<InvitesResponse> mCallback = new Callback<InvitesResponse>() {

    @Override
    public void onResponse(Call<InvitesResponse> call, Response<InvitesResponse> response) {
      InvitesResponse dataResponse = response.body();
      ResponseBody errorBody = response.errorBody();

      if (clientListener.get() != null) {
        if (dataResponse != null) {
          Invite[] result = dataResponse.getResult();
          ((ApiGetMyInvitesListener) clientListener.get()).onApiGetMyInvitesSuccess(result,
              isRefreshTrackings);
        } else if (errorBody != null) {
          String errorMessage = parseErrorBody(errorBody);
          responseFailure(TAG, errorMessage);
          if (response.code() == 401) {
            clientListener.get().onApiUnauthorized();
          } else if (response.code() == OLD_VERSION_CODE) {
            clientListener.get().onUpdateOldVersion();
          } else {
            ((ApiGetMyInvitesListener) clientListener.get()).onApiGetMyInvitesFailure(errorMessage);
          }
        } else {
          responseIsNull(TAG);
          ((ApiGetMyInvitesListener) clientListener.get()).onApiGetMyInvitesFailure(
              RESPONSE_IS_NULL);
        }
      } else {
        listenerNotRegistered(TAG);
      }
    }

    @Override public void onFailure(Call<InvitesResponse> call, Throwable t) {
      if (clientListener.get() != null) {
        String message = requestFailure(TAG, t);
        ((ApiGetMyInvitesListener) clientListener.get()).onApiGetMyInvitesFailure(message);
      } else {
        listenerNotRegistered(TAG);
      }
    }
  };

  @Override public void registerListener(ApiBaseListener listener) {
    clientListener = new WeakReference<>(listener);
  }

  public void getInvites(boolean isRefreshTrackings) {
    this.isRefreshTrackings = isRefreshTrackings;

    Call<InvitesResponse> call = App.getApiInstance().getMyInvites();
    call.enqueue(mCallback);
  }

  public void getInvites() {
    this.isRefreshTrackings = false;

    Call<InvitesResponse> call = App.getApiInstance().getMyInvites();
    call.enqueue(mCallback);
  }
}
