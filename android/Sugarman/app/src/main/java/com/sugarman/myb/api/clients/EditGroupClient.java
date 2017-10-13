package com.sugarman.myb.api.clients;

import com.sugarman.myb.App;
import com.sugarman.myb.api.models.responses.facebook.FacebookFriend;
import com.sugarman.myb.api.models.responses.me.EditGroupResponse;
import com.sugarman.myb.constants.Constants;
import com.sugarman.myb.listeners.ApiBaseListener;
import com.sugarman.myb.listeners.ApiEditGroupListener;
import com.sugarman.myb.utils.CountryCodeHelper;
import com.sugarman.myb.utils.SharedPreferenceHelper;
import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class EditGroupClient extends BaseApiClient {

  private static final String TAG = CreateGroupClient.class.getName();

  private final Callback<EditGroupResponse> mCallback = new Callback<EditGroupResponse>() {

    @Override
    public void onResponse(Call<EditGroupResponse> call, Response<EditGroupResponse> response) {
      EditGroupResponse dataResponse = response.body();
      ResponseBody errorBody = response.errorBody();

      if (clientListener.get() != null) {
        if (dataResponse != null) {
          ((ApiEditGroupListener) clientListener.get()).onApiEditGroupSuccess(
              dataResponse.getResult());
        } else if (errorBody != null) {
          String errorMessage = parseErrorBody(errorBody);
          responseFailure(TAG, errorMessage);
          if (response.code() == 401) {
            clientListener.get().onApiUnauthorized();
          } else if (response.code() == OLD_VERSION_CODE) {
            clientListener.get().onUpdateOldVersion();
          } else {
            ((ApiEditGroupListener) clientListener.get()).onApiEditGroupFailure(errorMessage);
          }
        } else {
          responseIsNull(TAG);
          ((ApiEditGroupListener) clientListener.get()).onApiEditGroupFailure(RESPONSE_IS_NULL);
        }
      } else {
        listenerNotRegistered(TAG);
      }
    }

    @Override public void onFailure(Call<EditGroupResponse> call, Throwable t) {
      if (clientListener.get() != null) {
        String message = requestFailure(TAG, t);
        ((ApiEditGroupListener) clientListener.get()).onApiEditGroupFailure(message);
      } else {
        listenerNotRegistered(TAG);
      }
    }
  };

  @Override public void registerListener(ApiBaseListener listener) {
    clientListener = new WeakReference<>(listener);
  }

  public void editGroup(String trackingId, List<FacebookFriend> members, String groupName,
      File avatar) {
    MultipartBody.Part filePart = null;
    Timber.e("Called editGroup");

    int totalMembers = members.size();
    List<RequestBody> ids = new ArrayList<>(totalMembers);
    List<RequestBody> vkids = new ArrayList<>(totalMembers);
    List<RequestBody> phoneNumbers = new ArrayList<>(totalMembers);
    List<RequestBody> names = new ArrayList<>(totalMembers);
    List<RequestBody> vkNames = new ArrayList<>(totalMembers);
    List<RequestBody> phoneNames = new ArrayList<>(totalMembers);
    List<RequestBody> pictures = new ArrayList<>(totalMembers);
    List<RequestBody> vkpictures = new ArrayList<>(totalMembers);
    List<RequestBody> phonePictures = new ArrayList<>(totalMembers);

    if (avatar != null && avatar.exists() && avatar.isFile()) {
      RequestBody requestFile =
          RequestBody.create(MediaType.parse(Constants.IMAGE_JPEG_TYPE), avatar);
      filePart =
          MultipartBody.Part.createFormData(Constants.PICTURE, avatar.getName(), requestFile);
    }

    RequestBody name = RequestBody.create(MediaType.parse(Constants.TEXT_PLAIN_TYPE), groupName);
    RequestBody fbToken = RequestBody.create(MediaType.parse(Constants.TEXT_PLAIN_TYPE),
        SharedPreferenceHelper.getFBAccessToken());

    for (FacebookFriend friend : members) {
      if (friend.getSocialNetwork().equals("fb")) {
        Timber.e("FB FRIEND = " + friend.getName());
        ids.add(RequestBody.create(MediaType.parse(Constants.TEXT_PLAIN_TYPE), friend.getId()));
        names.add(RequestBody.create(MediaType.parse(Constants.TEXT_PLAIN_TYPE), friend.getName()));
        pictures.add(
            RequestBody.create(MediaType.parse(Constants.TEXT_PLAIN_TYPE), friend.getPicture()));
      } else if (friend.getSocialNetwork().equals("vk")) {
        Timber.e("VK FRIEND = " + friend.getName());
        vkids.add(RequestBody.create(MediaType.parse(Constants.TEXT_PLAIN_TYPE), friend.getId()));
        vkNames.add(
            RequestBody.create(MediaType.parse(Constants.TEXT_PLAIN_TYPE), friend.getName()));
        vkpictures.add(
            RequestBody.create(MediaType.parse(Constants.TEXT_PLAIN_TYPE), friend.getPicture()));
      } else {

        phoneNumbers.add(RequestBody.create(MediaType.parse(Constants.TEXT_PLAIN_TYPE),
            friend.getId().replace(" ", "")));
        phoneNames.add(
            RequestBody.create(MediaType.parse(Constants.TEXT_PLAIN_TYPE), friend.getName()));
        phonePictures.add(RequestBody.create(MediaType.parse(Constants.TEXT_PLAIN_TYPE),
            "https://sugarman-myb.s3.amazonaws.com/Group_New.png"));
      }
    }

    Call<EditGroupResponse> call = App.getApiInstance()
        .editGroup(trackingId, filePart, name, ids, vkids, phoneNumbers, names, vkNames, phoneNames,
            pictures, vkpictures, phonePictures);
    Timber.e("Called editGroup");
    call.enqueue(mCallback);
  }
}