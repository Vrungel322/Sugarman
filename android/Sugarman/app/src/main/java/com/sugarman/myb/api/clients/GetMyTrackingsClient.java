package com.sugarman.myb.api.clients;

import com.sugarman.myb.App;
import com.sugarman.myb.api.models.responses.Tracking;
import com.sugarman.myb.api.models.responses.me.trackings.MyTrackingsResponse;
import com.sugarman.myb.listeners.ApiBaseListener;
import com.sugarman.myb.listeners.ApiGetMyTrackingsListener;
import java.lang.ref.WeakReference;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GetMyTrackingsClient extends BaseApiClient {

  private static final String TAG = GetMyTrackingsClient.class.getName();

  private boolean isRefreshNotification;

  private final Callback<MyTrackingsResponse> mCallback = new Callback<MyTrackingsResponse>() {

    @Override
    public void onResponse(Call<MyTrackingsResponse> call, Response<MyTrackingsResponse> response) {

      MyTrackingsResponse dataResponse = response.body();
      ResponseBody errorBody = response.errorBody();

      if (clientListener.get() != null) {
        if (dataResponse != null) {
          Tracking[] result = dataResponse.getResult();
          ((ApiGetMyTrackingsListener) clientListener.get()).onApiGetMyTrackingSuccess(result,
              dataResponse.getMentorsGroup(), isRefreshNotification);
          //MainActivity.onApiTrackingSuccess();
        } else if (errorBody != null) {
          String errorMessage = parseErrorBody(errorBody);
          responseFailure(TAG, errorMessage);
          if (response.code() == 401) {
            clientListener.get().onApiUnauthorized();
          } else if (response.code() == OLD_VERSION_CODE) {
            clientListener.get().onUpdateOldVersion();
          } else {
            ((ApiGetMyTrackingsListener) clientListener.get()).onApiGetMyTrackingsFailure(
                errorMessage);
          }
        } else {
          responseIsNull(TAG);
          ((ApiGetMyTrackingsListener) clientListener.get()).onApiGetMyTrackingsFailure(
              RESPONSE_IS_NULL);
        }
      } else {
        listenerNotRegistered(TAG);
      }
    }

    @Override public void onFailure(Call<MyTrackingsResponse> call, Throwable t) {
      if (clientListener.get() != null) {
        String message = requestFailure(TAG, t);
        ((ApiGetMyTrackingsListener) clientListener.get()).onApiGetMyTrackingsFailure(message);
      } else {
        listenerNotRegistered(TAG);
      }
    }
  };

  @Override public void registerListener(ApiBaseListener listener) {
    clientListener = new WeakReference<>(listener);
  }

  public void getMyTrackings() {
    isRefreshNotification = false;
    Call<MyTrackingsResponse> call = App.getApiInstance().getMyTrackings();
    call.enqueue(mCallback);
  }

  public void getMyTrackings(boolean isRefreshNotification) {
    this.isRefreshNotification = isRefreshNotification;
    Call<MyTrackingsResponse> call = App.getApiInstance().getMyTrackings();
    call.enqueue(mCallback);
  }
}
