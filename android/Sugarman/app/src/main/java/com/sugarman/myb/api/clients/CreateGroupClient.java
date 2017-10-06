package com.sugarman.myb.api.clients;

import android.content.Context;
import android.util.Log;
import com.sugarman.myb.App;
import com.sugarman.myb.api.models.responses.facebook.FacebookFriend;
import com.sugarman.myb.api.models.responses.me.groups.CreateGroupResponse;
import com.sugarman.myb.constants.Constants;
import com.sugarman.myb.listeners.ApiBaseListener;
import com.sugarman.myb.listeners.ApiCreateGroupListener;
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

public class CreateGroupClient extends BaseApiClient {

  private static final String TAG = CreateGroupClient.class.getName();

  private final Callback<CreateGroupResponse> mCallback = new Callback<CreateGroupResponse>() {

    @Override
    public void onResponse(Call<CreateGroupResponse> call, Response<CreateGroupResponse> response) {
      CreateGroupResponse dataResponse = response.body();
      ResponseBody errorBody = response.errorBody();

      if (clientListener.get() != null) {
        if (dataResponse != null) {
          ((ApiCreateGroupListener) clientListener.get()).onApiCreateGroupSuccess(
              dataResponse.getResult());
        } else if (errorBody != null) {
          String errorMessage = parseErrorBody(errorBody);
          responseFailure(TAG, errorMessage);
          if (response.code() == 401) {
            clientListener.get().onApiUnauthorized();
          } else if (response.code() == OLD_VERSION_CODE) {
            clientListener.get().onUpdateOldVersion();
          } else {
            ((ApiCreateGroupListener) clientListener.get()).onApiCreateGroupFailure(errorMessage);
          }
        } else {
          responseIsNull(TAG);
          ((ApiCreateGroupListener) clientListener.get()).onApiCreateGroupFailure(RESPONSE_IS_NULL);
        }
      } else {
        listenerNotRegistered(TAG);
      }
    }

    @Override public void onFailure(Call<CreateGroupResponse> call, Throwable t) {
      if (clientListener.get() != null) {
        String message = requestFailure(TAG, t);
        ((ApiCreateGroupListener) clientListener.get()).onApiCreateGroupFailure(message);
      } else {
        listenerNotRegistered(TAG);
      }
    }
  };

  @Override public void registerListener(ApiBaseListener listener) {
    clientListener = new WeakReference<>(listener);
  }

  public void createGroup(List<FacebookFriend> members, String groupName, File avatar, Context c) {
    MultipartBody.Part filePart = null;

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
      Log.e("FILE NAME", avatar.getName());
    }

    RequestBody name = RequestBody.create(MediaType.parse(Constants.TEXT_PLAIN_TYPE), groupName);
    RequestBody fbToken = RequestBody.create(MediaType.parse(Constants.TEXT_PLAIN_TYPE),
        SharedPreferenceHelper.getFBAccessToken());

    for (FacebookFriend friend : members) {
      //friend.setSocialNetwork("fb");
      if (friend.getSocialNetwork().equals("fb")) {
        Timber.e("FB FRIEND = " + friend.getId());
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

        Timber.e("Phone contact " + friend.getName());
        if (friend.getId().contains("+")) {
          phoneNumbers.add(
              RequestBody.create(MediaType.parse(Constants.TEXT_PLAIN_TYPE), friend.getId().replace(" ","")));
        } else {
          phoneNumbers.add(RequestBody.create(MediaType.parse(Constants.TEXT_PLAIN_TYPE),
              (CountryCodeHelper.getCountryZipCode() + friend.getId()).replace(" ", "")));
          Timber.e(CountryCodeHelper.getCountryZipCode() + friend.getId());
        }

        phoneNames.add(RequestBody.create(MediaType.parse(Constants.TEXT_PLAIN_TYPE),
            "Walker " + new Random().nextInt(9000)));
        phonePictures.add(RequestBody.create(MediaType.parse(Constants.TEXT_PLAIN_TYPE),
            "https://sugarman-myb.s3.amazonaws.com/Group_New.png"));
        Timber.e("pictures phone: " + phonePictures.size()+ " names phone: " + phoneNames.size() + " ids phone: " + phoneNumbers.size() );
      }
    }



    Call<CreateGroupResponse> call = App.getApiInstance()
        .createGroup(filePart, name, fbToken, ids, vkids, phoneNumbers, names, vkNames, phoneNames,
            pictures, vkpictures, phonePictures);
    call.enqueue(mCallback);
  }
}