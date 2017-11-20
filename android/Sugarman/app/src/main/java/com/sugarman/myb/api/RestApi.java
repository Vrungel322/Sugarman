package com.sugarman.myb.api;

import android.util.Log;
import com.sugarman.myb.api.models.levelSystem.TaskEntity;
import com.sugarman.myb.api.models.requests.CheckPhoneRequest;
import com.sugarman.myb.api.models.requests.CheckVkRequest;
import com.sugarman.myb.api.models.requests.DeleteUserRequest;
import com.sugarman.myb.api.models.requests.PurchaseDataRequest;
import com.sugarman.myb.api.models.requests.RefreshUserDataRequest;
import com.sugarman.myb.api.models.responses.CheckPhoneResponse;
import com.sugarman.myb.api.models.responses.CheckVkResponse;
import com.sugarman.myb.api.models.responses.CountInvitesResponse;
import com.sugarman.myb.api.models.responses.InvitersImgUrls;
import com.sugarman.myb.api.models.responses.ShopProductEntity;
import com.sugarman.myb.api.models.responses.facebook.FacebookFriend;
import com.sugarman.myb.api.models.responses.users.UsersResponse;
import com.sugarman.myb.constants.Constants;
import com.sugarman.myb.models.iab.NextFreeSkuEntity;
import com.sugarman.myb.models.iab.PurchaseForServer;
import com.sugarman.myb.models.mentor.MentorStupidAbstraction;
import com.sugarman.myb.models.mentor.MentorsCommentsStupidAbstraction;
import com.sugarman.myb.models.mentor.comments.CommentEntity;
import com.sugarman.myb.utils.SharedPreferenceHelper;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Response;
import rx.Observable;
import timber.log.Timber;

/**
 * Created by Vrungel on 26.01.2017.
 */

public class RestApi {
  private final ApiRx api;

  public RestApi(ApiRx api) {
    this.api = api;
  }

  public Observable<UsersResponse> refreshRxUserData(RefreshUserDataRequest request) {
    return api.refreshRxUserData(request);
  }

  public Observable<Response<Void>> sendPurchaseData(PurchaseDataRequest purchaseDataRequest) {
    return api.sendPurchaseData(purchaseDataRequest);
  }

  public Observable<Response<Void>> addFriendsToShopGroup(String userId,
      ArrayList<FacebookFriend> selectedMembers) {
    RequestBody uId = RequestBody.create(MediaType.parse(Constants.TEXT_PLAIN_TYPE), userId);

    List<RequestBody> ids = new ArrayList<>();
    List<RequestBody> vkids = new ArrayList<>();
    List<RequestBody> phoneNumbers = new ArrayList<>();
    List<RequestBody> names = new ArrayList<>();
    List<RequestBody> vkNames = new ArrayList<>();
    List<RequestBody> phoneNames = new ArrayList<>();
    List<RequestBody> pictures = new ArrayList<>();
    List<RequestBody> vkpictures = new ArrayList<>();
    List<RequestBody> phonePictures = new ArrayList<>();

    for (FacebookFriend friend : selectedMembers) {
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
        Timber.e("PH FRIEND = " + friend.getName() + " " + friend.getId());
        phoneNumbers.add(
            RequestBody.create(MediaType.parse(Constants.TEXT_PLAIN_TYPE), friend.getId()));
        phoneNames.add(
            RequestBody.create(MediaType.parse(Constants.TEXT_PLAIN_TYPE), friend.getName()));
        phonePictures.add(RequestBody.create(MediaType.parse(Constants.TEXT_PLAIN_TYPE),
            "https://sugarman-myb.s3.amazonaws.com/Group_New.png"));
      }
    }
    return api.addFriendsToShopGroup(uId, ids, vkids, phoneNumbers, names, vkNames, phoneNames,
        pictures, vkpictures, phonePictures);
  }

  public Observable<InvitersImgUrls> loadInvitersImgUrls(String accessToken) {
    return api.loadInvitersImgUrls();
  }

  public Observable<CountInvitesResponse> countInvites(String accessToken) {
    return api.countInvites();
  }

  public Observable<List<ShopProductEntity>> fetchProducts() {
    return api.fetchProducts();
  }

  public Observable<UsersResponse> sendUserDataToServer(String phone, String email, String name,
      String fbId, String vkId, String pictureUrl, File selectedFile, String accessToken) {
    MultipartBody.Part filePart = null;

    if (selectedFile != null && selectedFile.exists() && selectedFile.isFile()) {
      RequestBody requestFile =
          RequestBody.create(MediaType.parse(Constants.IMAGE_JPEG_TYPE), pictureUrl);
      filePart =
          MultipartBody.Part.createFormData(Constants.PICTURE, selectedFile.getName(), requestFile);
      Log.e("FILE NAME", selectedFile.getName());
    }

    RequestBody userIdReq = RequestBody.create(MediaType.parse(Constants.TEXT_PLAIN_TYPE),
        SharedPreferenceHelper.getUserId());
    RequestBody fbIdReq = RequestBody.create(MediaType.parse(Constants.TEXT_PLAIN_TYPE), fbId);
    RequestBody vkIdReq = RequestBody.create(MediaType.parse(Constants.TEXT_PLAIN_TYPE), vkId);
    RequestBody fbToken = RequestBody.create(MediaType.parse(Constants.TEXT_PLAIN_TYPE),
        SharedPreferenceHelper.getFBAccessToken());
    //RequestBody googleIdReq = RequestBody.create(MediaType.parse(Constants.TEXT_PLAIN_TYPE), googleId);
    RequestBody phoneNumReq = RequestBody.create(MediaType.parse(Constants.TEXT_PLAIN_TYPE), phone);
    RequestBody emailReq = RequestBody.create(MediaType.parse(Constants.TEXT_PLAIN_TYPE), email);
    RequestBody nameReq = RequestBody.create(MediaType.parse(Constants.TEXT_PLAIN_TYPE), name);
    RequestBody pictureReq =
        RequestBody.create(MediaType.parse(Constants.TEXT_PLAIN_TYPE), pictureUrl);
    RequestBody vkReq = RequestBody.create(MediaType.parse(Constants.TEXT_PLAIN_TYPE),
        SharedPreferenceHelper.getVkToken());
    RequestBody gReq = RequestBody.create(MediaType.parse(Constants.TEXT_PLAIN_TYPE), "none");

    return api.editUser(filePart, userIdReq, fbIdReq, vkIdReq, phoneNumReq, emailReq, pictureReq,
        nameReq, fbToken, vkReq, gReq, accessToken);
  }

  public Observable<CheckPhoneResponse> checkPhone(String accessToken, CheckPhoneRequest phones) {
    return api.checkPhone(phones);
  }

  public Observable<CheckVkResponse> checkVk(String accessToken, CheckVkRequest vkRequest) {
    return api.checkVk(vkRequest);
  }

  public Observable<TaskEntity> fetchTasks() {
    return api.fetchTasks();
  }

  public Observable<List<String>> fetchCompletedTasks(String accessToken) {
    return api.fetchCompletedTasks(accessToken);
  }

  public Observable<MentorStupidAbstraction> fetchMentors() {
    return api.fetchMentors();
  }

  public Observable<MentorsCommentsStupidAbstraction> fetchComments(String mentorId) {
    return api.fetchComments(mentorId);
  }

  public Observable<Response<Void>> deleteUser(String trackingId, DeleteUserRequest userId) {
    return api.deleteUser(trackingId, userId);
  }

  public Observable<Response<Void>> sendComment(String mentorId, CommentEntity commentEntity) {
    return api.sendComment(mentorId, commentEntity);
  }

  public Observable<Response<Void>> checkInAppBilling(PurchaseForServer purchaseForServer) {
    return api.checkInAppBilling(purchaseForServer);
  }

  public Observable<Response<NextFreeSkuEntity>> getNextFreeSku() {
    return api.getNextFreeSku("Android");
  }
}
