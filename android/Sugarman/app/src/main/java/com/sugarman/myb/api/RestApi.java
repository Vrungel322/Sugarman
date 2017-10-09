package com.sugarman.myb.api;

import com.sugarman.myb.api.models.requests.PurchaseDataRequest;
import com.sugarman.myb.api.models.requests.RefreshUserDataRequest;
import com.sugarman.myb.api.models.responses.CountInvitesResponse;
import com.sugarman.myb.api.models.responses.InvitersImgUrls;
import com.sugarman.myb.api.models.levelSystem.TaskEntity;
import com.sugarman.myb.api.models.responses.facebook.FacebookFriend;
import com.sugarman.myb.api.models.responses.users.UsersResponse;
import com.sugarman.myb.constants.Constants;
import java.util.ArrayList;
import java.util.List;
import okhttp3.MediaType;
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
      friend.setSocialNetwork("fb");
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
    return api.loadInvitersImgUrls(accessToken);
  }

  public Observable<CountInvitesResponse> countInvites(String accessToken) {
    return api.countInvites(accessToken);
  }

  public Observable<TaskEntity> fetchTasks() {
    return api.fetchTasks();
  }
}
