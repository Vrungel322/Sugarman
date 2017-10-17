package com.sugarman.myb.api.clients;

import com.sugarman.myb.api.models.responses.me.user.MyUserResponse;
import com.sugarman.myb.api.models.responses.users.User;
import com.sugarman.myb.listeners.ApiBaseListener;
import com.sugarman.myb.listeners.ApiGetMyUserListener;
import java.lang.ref.WeakReference;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GetMyUserClient extends BaseApiClient {

  private static final String TAG = GetMyUserClient.class.getName();

  private final Callback<MyUserResponse> mCallback = new Callback<MyUserResponse>() {

    @Override public void onResponse(Call<MyUserResponse> call, Response<MyUserResponse> response) {
      MyUserResponse dataResponse = response.body();
      ResponseBody errorBody = response.errorBody();

      if (clientListener.get() != null) {
        if (dataResponse != null) {
          User result = dataResponse.getResult();
          ((ApiGetMyUserListener) clientListener.get()).onApiGetMyUserSuccess(result);
        } else if (errorBody != null) {
          String errorMessage = parseErrorBody(errorBody);
          responseFailure(TAG, errorMessage);
          if (response.code() == 401) {
            clientListener.get().onApiUnauthorized();
          } else if (response.code() == OLD_VERSION_CODE) {
            clientListener.get().onUpdateOldVersion();
          } else {
            ((ApiGetMyUserListener) clientListener.get()).onApiGetMyUserFailure(errorMessage);
          }
        } else {
          responseIsNull(TAG);
          ((ApiGetMyUserListener) clientListener.get()).onApiGetMyUserFailure(RESPONSE_IS_NULL);
        }
      } else {
        listenerNotRegistered(TAG);
      }
    }

    @Override public void onFailure(Call<MyUserResponse> call, Throwable t) {
      if (clientListener.get() != null) {
        String message = requestFailure(TAG, t);
        ((ApiGetMyUserListener) clientListener.get()).onApiGetMyUserFailure(message);
      } else {
        listenerNotRegistered(TAG);
      }
    }
  };

  @Override public void registerListener(ApiBaseListener listener) {
    clientListener = new WeakReference<>(listener);
  }

  //public void getUser() {
  //  Call<MyUserResponse> call = App.getApiInstance().getMyUser();
  //  call.enqueue(mCallback);
  //}
}
