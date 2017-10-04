package com.sugarman.myb.api.clients;

import com.sugarman.myb.App;
import com.sugarman.myb.api.models.responses.trackings.TrackingInfoResponse;
import com.sugarman.myb.listeners.ApiBaseListener;
import com.sugarman.myb.listeners.ApiGetTrackingInfoListener;
import java.lang.ref.WeakReference;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GetTrackingInfoClient extends BaseApiClient {

  private static final String TAG = GetTrackingInfoClient.class.getName();

  private final Callback<TrackingInfoResponse> mCallback = new Callback<TrackingInfoResponse>() {

    @Override public void onResponse(Call<TrackingInfoResponse> call,
        Response<TrackingInfoResponse> response) {
      TrackingInfoResponse dataResponse = response.body();
      ResponseBody errorBody = response.errorBody();

      if (clientListener.get() != null) {
        if (dataResponse != null && dataResponse.getResult() != null) {
          ((ApiGetTrackingInfoListener) clientListener.get()).onApiGetTrackingInfoSuccess(
              dataResponse.getResult());
        } else if (errorBody != null) {
          String errorMessage = parseErrorBody(errorBody);
          responseFailure(TAG, errorMessage);
          if (response.code() == 401) {
            clientListener.get().onApiUnauthorized();
          } else if (response.code() == OLD_VERSION_CODE) {
            clientListener.get().onUpdateOldVersion();
          } else {
            ((ApiGetTrackingInfoListener) clientListener.get()).onApiGetTrackingInfoFailure(
                errorMessage);
          }
        } else {
          responseIsNull(TAG);
          ((ApiGetTrackingInfoListener) clientListener.get()).onApiGetTrackingInfoFailure(
              RESPONSE_IS_NULL);
        }
      } else {
        listenerNotRegistered(TAG);
      }
    }

    @Override public void onFailure(Call<TrackingInfoResponse> call, Throwable t) {
      if (clientListener.get() != null) {
        String message = requestFailure(TAG, t);
        ((ApiGetTrackingInfoListener) clientListener.get()).onApiGetTrackingInfoFailure(message);
      } else {
        listenerNotRegistered(TAG);
      }
    }
  };

  @Override public void registerListener(ApiBaseListener listener) {
    clientListener = new WeakReference<>(listener);
  }

  public void getTrackingInfo(String trackingId) {
    Call<TrackingInfoResponse> call = App.getApiInstance().getTrackingInfo(trackingId);
    call.enqueue(mCallback);
  }
}