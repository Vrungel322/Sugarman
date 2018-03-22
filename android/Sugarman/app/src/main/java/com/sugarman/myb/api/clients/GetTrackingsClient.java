package com.sugarman.myb.api.clients;

import android.text.TextUtils;
import com.sugarman.myb.App;
import com.sugarman.myb.api.models.responses.trackings.TrackingsResponse;
import com.sugarman.myb.listeners.ApiBaseListener;
import com.sugarman.myb.listeners.ApiGetTrackingsListener;
import java.lang.ref.WeakReference;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GetTrackingsClient extends BaseApiClient {

  private static final String TAG = GetTrackingsClient.class.getName();

  public static final String AVAILABLE_TYPE = "available";
  public static final String UNAVAILABLE_TYPE = "unavailable";

  private String type;

  private Call<TrackingsResponse> call;

  private final Callback<TrackingsResponse> mCallback = new Callback<TrackingsResponse>() {

    @Override
    public void onResponse(Call<TrackingsResponse> call, Response<TrackingsResponse> response) {
      //TrackingsResponse dataResponse = response.body();
      //ResponseBody errorBody = response.errorBody();
      //
      //if (clientListener.get() != null) {
      //  if (dataResponse != null) {
      //    Tracking[] result = dataResponse.getResult();
      //    ((ApiGetTrackingsListener) clientListener.get()).onApiGetTrackingsSuccess(type, result);
      //  } else if (errorBody != null) {
      //    String errorMessage = parseErrorBody(errorBody);
      //    responseFailure(TAG, errorMessage);
      //    if (response.code() == 401) {
      //      clientListener.get().onApiUnauthorized();
      //    } else if (response.code() == OLD_VERSION_CODE) {
      //      clientListener.get().onUpdateOldVersion();
      //    } else {
      //      ((ApiGetTrackingsListener) clientListener.get()).onApiGetTrackingsFailure(type,
      //          errorMessage);
      //    }
      //  } else {
      //    responseIsNull(TAG);
      //    ((ApiGetTrackingsListener) clientListener.get()).onApiGetTrackingsFailure(type,
      //        RESPONSE_IS_NULL);
      //  }
      //} else {
      //  listenerNotRegistered(TAG);
      //}
    }

    @Override public void onFailure(Call<TrackingsResponse> call, Throwable t) {
      if (clientListener.get() != null) {
        String message = requestFailure(TAG, t);
        if (!call.isCanceled()) {
          ((ApiGetTrackingsListener) clientListener.get()).onApiGetTrackingsFailure(type, message);
        }
      } else {
        listenerNotRegistered(TAG);
      }
    }
  };

  @Override public void registerListener(ApiBaseListener listener) {
    clientListener = new WeakReference<>(listener);
  }

  public void getTrackings(String type, String query) {
    this.type = type;

    if (call != null) {
      call.cancel();
    }

    if (TextUtils.isEmpty(query)) {
      call = App.getApiInstance().getTrackings(type);
    } else {
      call = App.getApiInstance().getTrackings(query, type);
    }
    call.enqueue(mCallback);
  }
}