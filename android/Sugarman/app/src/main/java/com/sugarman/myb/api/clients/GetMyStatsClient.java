package com.sugarman.myb.api.clients;

import com.sugarman.myb.App;
import com.sugarman.myb.api.models.responses.me.stats.Stats;
import com.sugarman.myb.api.models.responses.me.stats.StatsResponse;
import com.sugarman.myb.listeners.ApiBaseListener;
import com.sugarman.myb.listeners.ApiGetMyStatsListener;
import java.lang.ref.WeakReference;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GetMyStatsClient extends BaseApiClient {

  private static final String TAG = GetMyStatsClient.class.getName();

  private final Callback<StatsResponse> mCallback = new Callback<StatsResponse>() {

    @Override public void onResponse(Call<StatsResponse> call, Response<StatsResponse> response) {
      StatsResponse dataResponse = response.body();
      ResponseBody errorBody = response.errorBody();

      if (clientListener.get() != null) {
        if (dataResponse != null) {
          Stats[] result = dataResponse.getResult();
          ((ApiGetMyStatsListener) clientListener.get()).onApiGetMyStatsSuccess(result);
        } else if (errorBody != null) {
          String errorMessage = parseErrorBody(errorBody);
          responseFailure(TAG, errorMessage);
          if (response.code() == 401) {
            clientListener.get().onApiUnauthorized();
          } else if (response.code() == OLD_VERSION_CODE) {
            clientListener.get().onUpdateOldVersion();
          } else {
            ((ApiGetMyStatsListener) clientListener.get()).onApiGetMyStatsFailure(errorMessage);
          }
        } else {
          responseIsNull(TAG);
          ((ApiGetMyStatsListener) clientListener.get()).onApiGetMyStatsFailure(RESPONSE_IS_NULL);
        }
      } else {
        listenerNotRegistered(TAG);
      }
    }

    @Override public void onFailure(Call<StatsResponse> call, Throwable t) {
      if (clientListener.get() != null) {
        String message = requestFailure(TAG, t);
        ((ApiGetMyStatsListener) clientListener.get()).onApiGetMyStatsFailure(message);
      } else {
        listenerNotRegistered(TAG);
      }
    }
  };

  @Override public void registerListener(ApiBaseListener listener) {
    clientListener = new WeakReference<>(listener);
  }

  public void getStats() {
    Call<StatsResponse> call = App.getApiInstance().getMyStats();
    call.enqueue(mCallback);
  }
}