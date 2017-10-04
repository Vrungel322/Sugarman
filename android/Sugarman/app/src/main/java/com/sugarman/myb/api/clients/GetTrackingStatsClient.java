package com.sugarman.myb.api.clients;

import com.sugarman.myb.App;
import com.sugarman.myb.api.models.responses.me.stats.Stats;
import com.sugarman.myb.api.models.responses.trackings.TrackingStatsResponse;
import com.sugarman.myb.constants.Config;
import com.sugarman.myb.listeners.ApiBaseListener;
import com.sugarman.myb.listeners.ApiGetTrackingStatsListener;
import java.lang.ref.WeakReference;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GetTrackingStatsClient extends BaseApiClient {

  private static final String TAG = GetTrackingStatsClient.class.getName();

  private final Callback<TrackingStatsResponse> mCallback = new Callback<TrackingStatsResponse>() {

    @Override public void onResponse(Call<TrackingStatsResponse> call,
        Response<TrackingStatsResponse> response) {
      TrackingStatsResponse dataResponse = response.body();
      ResponseBody errorBody = response.errorBody();

      if (clientListener.get() != null) {
        if (dataResponse != null) {
          Stats[] responseResult = dataResponse.getResult();
          Stats[] result;
          if (responseResult.length > Config.SHOWING_DAYS_STATS) {
            result = new Stats[Config.SHOWING_DAYS_STATS];
            System.arraycopy(responseResult, 0, result, 0, Config.SHOWING_DAYS_STATS);
          } else {
            result = responseResult;
          }
          ((ApiGetTrackingStatsListener) clientListener.get()).onApiGetTrackingStatsSuccess(result);
        } else if (errorBody != null) {
          String errorMessage = parseErrorBody(errorBody);
          responseFailure(TAG, errorMessage);
          if (response.code() == 401) {
            clientListener.get().onApiUnauthorized();
          } else if (response.code() == OLD_VERSION_CODE) {
            clientListener.get().onUpdateOldVersion();
          } else {
            ((ApiGetTrackingStatsListener) clientListener.get()).onApiGetTrackingStatsFailure(
                errorMessage);
          }
        } else {
          responseIsNull(TAG);
          ((ApiGetTrackingStatsListener) clientListener.get()).onApiGetTrackingStatsFailure(
              RESPONSE_IS_NULL);
        }
      } else {
        listenerNotRegistered(TAG);
      }
    }

    @Override public void onFailure(Call<TrackingStatsResponse> call, Throwable t) {
      if (clientListener.get() != null) {
        String message = requestFailure(TAG, t);
        ((ApiGetTrackingStatsListener) clientListener.get()).onApiGetTrackingStatsFailure(message);
      } else {
        listenerNotRegistered(TAG);
      }
    }
  };

  @Override public void registerListener(ApiBaseListener listener) {
    clientListener = new WeakReference<>(listener);
  }

  public void getTrackingStats(String trackingId) {
    Call<TrackingStatsResponse> call = App.getApiInstance().getTrackingStats(trackingId);
    call.enqueue(mCallback);
  }
}