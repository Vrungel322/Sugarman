package com.sugarman.myb.api.clients;

import com.sugarman.myb.App;
import com.sugarman.myb.api.models.responses.me.score.HighScoreResponse;
import com.sugarman.myb.api.models.responses.me.score.HighScores;
import com.sugarman.myb.listeners.ApiBaseListener;
import com.sugarman.myb.listeners.ApiGetHighScoresListener;
import java.lang.ref.WeakReference;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GetHighScoreClient extends BaseApiClient {

  private static final String TAG = GetHighScoreClient.class.getName();

  private final Callback<HighScoreResponse> mCallback = new Callback<HighScoreResponse>() {

    @Override
    public void onResponse(Call<HighScoreResponse> call, Response<HighScoreResponse> response) {
      HighScoreResponse dataResponse = response.body();
      ResponseBody errorBody = response.errorBody();

      if (clientListener.get() != null) {
        if (dataResponse != null) {
          HighScores result = dataResponse.getResult();
          ((ApiGetHighScoresListener) clientListener.get()).onApiGetHighScoresSuccess(result);
        } else if (errorBody != null) {
          String errorMessage = parseErrorBody(errorBody);
          responseFailure(TAG, errorMessage);
          if (response.code() == 401) {
            clientListener.get().onApiUnauthorized();
          } else if (response.code() == OLD_VERSION_CODE) {
            clientListener.get().onUpdateOldVersion();
          } else {
            ((ApiGetHighScoresListener) clientListener.get()).onApiGetHighScoresFailure(
                errorMessage);
          }
        } else {
          responseIsNull(TAG);
          ((ApiGetHighScoresListener) clientListener.get()).onApiGetHighScoresFailure(
              RESPONSE_IS_NULL);
        }
      } else {
        listenerNotRegistered(TAG);
      }
    }

    @Override public void onFailure(Call<HighScoreResponse> call, Throwable t) {
      if (clientListener.get() != null) {
        String message = requestFailure(TAG, t);
        ((ApiGetHighScoresListener) clientListener.get()).onApiGetHighScoresFailure(message);
      } else {
        listenerNotRegistered(TAG);
      }
    }
  };

  @Override public void registerListener(ApiBaseListener listener) {
    clientListener = new WeakReference<>(listener);
  }

  public void getHighScores() {
    Call<HighScoreResponse> call = App.getApiInstance().getHighScore();
    call.enqueue(mCallback);
  }
}