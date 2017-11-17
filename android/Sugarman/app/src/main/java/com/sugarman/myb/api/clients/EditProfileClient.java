package com.sugarman.myb.api.clients;

import android.util.Log;
import com.sugarman.myb.App;
import com.sugarman.myb.api.models.requests.EditUserRequest;
import com.sugarman.myb.api.models.responses.users.UsersResponse;
import com.sugarman.myb.constants.Constants;
import com.sugarman.myb.listeners.ApiBaseListener;
import com.sugarman.myb.listeners.ApiRefreshUserDataListener;
import com.sugarman.myb.utils.SharedPreferenceHelper;
import java.io.File;
import java.lang.ref.WeakReference;
import java.util.TimeZone;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfileClient extends BaseApiClient {

  private static final String TAG = EditProfileClient.class.getName();

  private final Callback<UsersResponse> mCallback = new Callback<UsersResponse>() {

    @Override public void onResponse(Call<UsersResponse> call, Response<UsersResponse> response) {
      UsersResponse dataResponse = response.body();
      ResponseBody errorBody = response.errorBody();

      Log.e("Token", "ATLICHNA");

      if (clientListener.get() != null) {
        if (dataResponse != null) {
          Log.e("Token", "ATLICHNA 2");
          if (dataResponse.getResult() != null) {
            SharedPreferenceHelper.setOTPStatus(dataResponse.getUser().getNeedOTP());
          }

          ((ApiRefreshUserDataListener) clientListener.get()).onApiRefreshUserDataSuccess(
              dataResponse);
        } else if (errorBody != null) {
          String errorMessage = parseErrorBody(errorBody);
          responseFailure(TAG, errorMessage);
          if (response.code() == 401) {
            clientListener.get().onApiUnauthorized();
          } else if (response.code() == OLD_VERSION_CODE) {
            clientListener.get().onUpdateOldVersion();
          } else {
            Log.e("Token", "NE ATLICHNA NULL");
            Log.e("Token", "HUETA" + errorMessage);
            ((ApiRefreshUserDataListener) clientListener.get()).onApiRefreshUserDataFailure(
                errorMessage);
          }
        } else {
          responseIsNull(TAG);
          ((ApiRefreshUserDataListener) clientListener.get()).onApiRefreshUserDataFailure(
              RESPONSE_IS_NULL);
        }
      } else {
        listenerNotRegistered(TAG);
      }
    }

    @Override public void onFailure(Call<UsersResponse> call, Throwable t) {
      Log.e("Token", "OCHKO" + t.getLocalizedMessage());
      if (clientListener.get() != null) {
        String message = requestFailure(TAG, t);
        ((ApiRefreshUserDataListener) clientListener.get()).onApiRefreshUserDataFailure(message);
      } else {
        listenerNotRegistered(TAG);
      }
    }
  };

  @Override public void registerListener(ApiBaseListener listener) {
    clientListener = new WeakReference<>(listener);
  }

  public void editUser(String phoneNumber, String email, String name, String fbId, String vkId,
      String pictureUrl, File avatar) {

    MultipartBody.Part filePart = null;

    if (avatar != null && avatar.exists() && avatar.isFile()) {
      RequestBody requestFile =
          RequestBody.create(MediaType.parse(Constants.IMAGE_JPEG_TYPE), avatar);
      filePart =
          MultipartBody.Part.createFormData(Constants.PICTURE, avatar.getName(), requestFile);
      Log.e("FILE NAME", avatar.getName());
    }

    EditUserRequest request = new EditUserRequest();
    request.setUserId(SharedPreferenceHelper.getUserId());
    request.setFbId(fbId);
    request.setVkId(vkId);
    //googleid
    request.setPhoneNumber(phoneNumber);
    request.setEmail(email);
    //link
    request.setName(name);
    request.setPictureUrl(pictureUrl);
    //vklink
    request.setTimezone(TimeZone.getDefault().getID());
    Log.e("Request", request.toString());
    Log.e("token", "editProfileClient rabotaem");

    RequestBody userIdReq = RequestBody.create(MediaType.parse(Constants.TEXT_PLAIN_TYPE),
        SharedPreferenceHelper.getUserId());
    RequestBody fbIdReq = RequestBody.create(MediaType.parse(Constants.TEXT_PLAIN_TYPE), fbId);
    RequestBody vkIdReq = RequestBody.create(MediaType.parse(Constants.TEXT_PLAIN_TYPE), vkId);
    RequestBody fbToken = RequestBody.create(MediaType.parse(Constants.TEXT_PLAIN_TYPE),
        SharedPreferenceHelper.getFBAccessToken());
    //RequestBody googleIdReq = RequestBody.create(MediaType.parse(Constants.TEXT_PLAIN_TYPE), googleId);
    RequestBody phoneNumReq =
        RequestBody.create(MediaType.parse(Constants.TEXT_PLAIN_TYPE), phoneNumber);
    RequestBody emailReq = RequestBody.create(MediaType.parse(Constants.TEXT_PLAIN_TYPE), email);
    RequestBody nameReq = RequestBody.create(MediaType.parse(Constants.TEXT_PLAIN_TYPE), name);
    RequestBody pictureReq =
        RequestBody.create(MediaType.parse(Constants.TEXT_PLAIN_TYPE), pictureUrl);
    RequestBody vkReq = RequestBody.create(MediaType.parse(Constants.TEXT_PLAIN_TYPE),
        SharedPreferenceHelper.getVkToken());
    RequestBody gReq = RequestBody.create(MediaType.parse(Constants.TEXT_PLAIN_TYPE), "none");

    Call<UsersResponse> call = App.getApiInstance()
        .editUser(filePart, userIdReq, fbIdReq, vkIdReq, phoneNumReq, emailReq, pictureReq, nameReq,
            fbToken, vkReq, gReq);
    call.enqueue(mCallback);
  }
}
