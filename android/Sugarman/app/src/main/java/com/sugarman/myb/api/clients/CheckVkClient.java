package com.sugarman.myb.api.clients;

import com.sugarman.myb.App;
import com.sugarman.myb.api.models.requests.CheckPhoneRequest;
import com.sugarman.myb.api.models.requests.CheckVkRequest;
import com.sugarman.myb.api.models.responses.CheckPhoneResponse;
import com.sugarman.myb.api.models.responses.CheckVkResponse;
import com.sugarman.myb.listeners.ApiBaseListener;
import com.sugarman.myb.listeners.ApiCheckPhoneListener;
import com.sugarman.myb.listeners.ApiCheckVkListener;
import java.lang.ref.WeakReference;
import java.util.List;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

/**
 * Created by yegoryeriomin on 10/11/17.
 */

public class CheckVkClient extends BaseApiClient {
  private static final String TAG = CheckVkClient.class.getName();
  @Override public void registerListener(ApiBaseListener listener) {
    clientListener = new WeakReference<>(listener);
  }

  private final Callback<CheckVkResponse> mCallback = new Callback<CheckVkResponse>() {

    @Override public void onResponse(Call<CheckVkResponse> call, Response<CheckVkResponse> dataResponse) {
      ResponseBody errorBody = dataResponse.errorBody();
      if (dataResponse != null) {
        Timber.e("SET INVITABLE 0");
        ((ApiCheckVkListener) clientListener.get()).onApiCheckVkSuccess(dataResponse.body().getVks());
      } else if (errorBody != null) {
        String errorMessage = parseErrorBody(errorBody);
        responseFailure(TAG, errorMessage);
        if (dataResponse.code() == 401) {
          clientListener.get().onApiUnauthorized();
        } else if (dataResponse.code() == OLD_VERSION_CODE) {
          clientListener.get().onUpdateOldVersion();
        } else {
          ((ApiCheckVkListener) clientListener.get()).onApiCheckVkFailure(errorMessage);
        }
      } else {
        responseIsNull(TAG);
        ((ApiCheckVkListener) clientListener.get()).onApiCheckVkFailure(RESPONSE_IS_NULL);
      }
  }



    @Override public void onFailure(Call<CheckVkResponse> call, Throwable t) {

    }
  };

  public void checkVks(List<String> vks)
  {
    Timber.e("CHECK VK CALLED");

    CheckVkRequest request = new CheckVkRequest();
    request.setVks(vks);

    Call<CheckVkResponse> call = App.getApiInstance().checkVk(request);
    call.enqueue(mCallback);
}
}
