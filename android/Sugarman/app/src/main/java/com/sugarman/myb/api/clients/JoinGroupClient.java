package com.sugarman.myb.api.clients;

import com.sugarman.myb.App;
import com.sugarman.myb.api.models.requests.JoinGroupRequest;
import com.sugarman.myb.api.models.responses.Tracking;
import com.sugarman.myb.api.models.responses.me.join.JoinGroupResponse;
import com.sugarman.myb.listeners.ApiBaseListener;
import com.sugarman.myb.listeners.ApiJoinGroupListener;
import java.lang.ref.WeakReference;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class JoinGroupClient extends BaseApiClient {

  private static final String TAG = JoinGroupClient.class.getName();

  private final Callback<JoinGroupResponse> mCallback = new Callback<JoinGroupResponse>() {

    @Override
    public void onResponse(Call<JoinGroupResponse> call, Response<JoinGroupResponse> response) {
      JoinGroupResponse dataResponse = response.body();
      ResponseBody errorBody = response.errorBody();

      if (clientListener.get() != null) {
        if (dataResponse != null) {
          Tracking result = dataResponse.getResult();
          ((ApiJoinGroupListener) clientListener.get()).onApiJoinGroupSuccess(result);
        } else if (errorBody != null) {
          String errorMessage = parseErrorBody(errorBody);
          responseFailure(TAG, errorMessage);
          if (response.code() == 401) {
            clientListener.get().onApiUnauthorized();
          } else if (response.code() == OLD_VERSION_CODE) {
            clientListener.get().onUpdateOldVersion();
          } else {
            ((ApiJoinGroupListener) clientListener.get()).onApiJoinGroupFailure(errorMessage);
          }
        } else {
          responseIsNull(TAG);
          ((ApiJoinGroupListener) clientListener.get()).onApiJoinGroupFailure(RESPONSE_IS_NULL);
        }
      } else {
        listenerNotRegistered(TAG);
      }
    }

    @Override public void onFailure(Call<JoinGroupResponse> call, Throwable t) {
      if (clientListener.get() != null) {
        String message = requestFailure(TAG, t);
        ((ApiJoinGroupListener) clientListener.get()).onApiJoinGroupFailure(message);
      } else {
        listenerNotRegistered(TAG);
      }
    }
  };

  @Override public void registerListener(ApiBaseListener listener) {
    clientListener = new WeakReference<>(listener);
  }

  public void joinGroup(String trackingId, String groupId) {
    JoinGroupRequest request = new JoinGroupRequest();
    request.setGroupId(groupId);

    Call<JoinGroupResponse> call = App.getApiInstance().joinGroup(trackingId, request);
    call.enqueue(mCallback);
  }

  public void joinGroup(String groupId) {
    JoinGroupRequest request = new JoinGroupRequest();
    request.setGroupId(groupId);

    Call<JoinGroupResponse> call = App.getApiInstance().joinGroup(request);
    call.enqueue(mCallback);
  }
}