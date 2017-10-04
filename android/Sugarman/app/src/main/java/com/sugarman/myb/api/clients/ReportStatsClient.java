package com.sugarman.myb.api.clients;

import com.sugarman.myb.App;
import com.sugarman.myb.api.models.requests.StatsRequest;
import com.sugarman.myb.listeners.ApiBaseListener;
import com.sugarman.myb.listeners.ApiReportStatsListener;
import java.lang.ref.WeakReference;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReportStatsClient extends BaseApiClient {

  private static final String TAG = ReportStatsClient.class.getName();

  private final Callback<Void> mCallback = new Callback<Void>() {

    @Override public void onResponse(Call<Void> call, Response<Void> response) {
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
            ((ApiReportStatsListener) clientListener.get()).onApiReportStatsFailure(errorMessage);
          }
        } else {
          ((ApiReportStatsListener) clientListener.get()).onApiReportStatsSuccess();
        }
      } else {
        listenerNotRegistered(TAG);
      }
    }

    @Override public void onFailure(Call<Void> call, Throwable t) {
      if (clientListener.get() != null) {
        String message = requestFailure(TAG, t);
        ((ApiReportStatsListener) clientListener.get()).onApiReportStatsFailure(message);
      } else {
        listenerNotRegistered(TAG);
      }
    }
  };

  @Override public void registerListener(ApiBaseListener listener) {
    clientListener = new WeakReference<>(listener);
  }

  public void sendStats(StatsRequest request) {
    Call<Void> call = App.getApiInstance().reportStats(request);

    call.enqueue(mCallback);
  }
}
