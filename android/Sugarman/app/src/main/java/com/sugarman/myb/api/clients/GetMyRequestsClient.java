package com.sugarman.myb.api.clients;

import com.sugarman.myb.App;
import com.sugarman.myb.api.models.responses.me.requests.Request;
import com.sugarman.myb.api.models.responses.me.requests.RequestsResponse;
import com.sugarman.myb.listeners.ApiBaseListener;
import com.sugarman.myb.listeners.ApiGetMyRequestListener;
import java.lang.ref.WeakReference;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GetMyRequestsClient extends BaseApiClient {

  private static final String TAG = GetMyRequestsClient.class.getName();

  private boolean isRefreshTrackings;

  private final Callback<RequestsResponse> mCallback = new Callback<RequestsResponse>() {

    @Override
    public void onResponse(Call<RequestsResponse> call, Response<RequestsResponse> response) {
      RequestsResponse dataResponse = response.body();

      ResponseBody errorBody = response.errorBody();

      if (clientListener.get() != null) {
        if (dataResponse != null) {
          Request[] result = dataResponse.getResult();
          ((ApiGetMyRequestListener) clientListener.get()).onApiGetMyRequestsSuccess(result,
              isRefreshTrackings);
        } else if (errorBody != null) {
          String errorMessage = parseErrorBody(errorBody);
          responseFailure(TAG, errorMessage);
          if (response.code() == 401) {
            clientListener.get().onApiUnauthorized();
          } else if (response.code() == OLD_VERSION_CODE) {
            clientListener.get().onUpdateOldVersion();
          } else {
            ((ApiGetMyRequestListener) clientListener.get()).onApiGetMyRequestsFailure(
                errorMessage);
          }
        } else {
          responseIsNull(TAG);
          ((ApiGetMyRequestListener) clientListener.get()).onApiGetMyRequestsFailure(
              RESPONSE_IS_NULL);
        }
      } else {
        listenerNotRegistered(TAG);
      }
    }

    @Override public void onFailure(Call<RequestsResponse> call, Throwable t) {
      if (clientListener.get() != null) {
        String message = requestFailure(TAG, t);
        ((ApiGetMyRequestListener) clientListener.get()).onApiGetMyRequestsFailure(message);
      } else {
        listenerNotRegistered(TAG);
      }
    }
  };

  @Override public void registerListener(ApiBaseListener listener) {
    clientListener = new WeakReference<>(listener);
  }

  public void getRequests(boolean isRefreshTrackings) {
    this.isRefreshTrackings = isRefreshTrackings;
    Call<RequestsResponse> call = App.getApiInstance().getMyRequests();
    call.enqueue(mCallback);
  }

  public void getRequests() {
    this.isRefreshTrackings = false;
    Call<RequestsResponse> call = App.getApiInstance().getMyRequests();
    call.enqueue(mCallback);
  }
}
