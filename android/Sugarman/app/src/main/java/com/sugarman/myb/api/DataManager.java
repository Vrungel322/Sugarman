package com.sugarman.myb.api;

import android.content.ContentResolver;
import com.sugarman.myb.api.models.requests.PurchaseDataRequest;
import com.sugarman.myb.api.models.requests.RefreshUserDataRequest;
import com.sugarman.myb.api.models.responses.CountInvitesResponse;
import com.sugarman.myb.api.models.responses.InvitersImgUrls;
import com.sugarman.myb.api.models.levelSystem.TaskEntity;
import com.sugarman.myb.api.models.responses.facebook.FacebookFriend;
import com.sugarman.myb.api.models.responses.users.UsersResponse;
import com.sugarman.myb.constants.Constants;
import com.sugarman.myb.data.local.PreferencesHelper;
import com.sugarman.myb.utils.ContactsHelper;
import com.sugarman.myb.utils.SharedPreferenceHelper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import retrofit2.Response;
import rx.Observable;
import timber.log.Timber;

/**
 * Created by Vrungel on 26.01.2017.
 */

public class DataManager {

  private RestApi mRestApi;
  private PreferencesHelper mPref;
  private ContentResolver mContentResolver;

  public DataManager(RestApi restApi, PreferencesHelper pref, ContentResolver contentResolver) {
    mRestApi = restApi;
    mPref = pref;
    mContentResolver = contentResolver;
  }

  public Observable<UsersResponse> refreshRxUserData(RefreshUserDataRequest request) {
    return mRestApi.refreshRxUserData(request);
  }

  public Observable<Response<Void>> sendPurchaseData(String countryName, String cityName,
      String streetName, String zipCode, String fullName, String phoneNumber, String amountPrice,
      String num, String productName) {

    PurchaseDataRequest purchaseDataRequest =
        new PurchaseDataRequest(SharedPreferenceHelper.getUserId(), countryName, cityName,
            streetName, fullName, phoneNumber, amountPrice, num, productName, zipCode);
    return mRestApi.sendPurchaseData(purchaseDataRequest);
  }

  public Observable<Response<Void>> addFriendsToShopGroup(
      ArrayList<FacebookFriend> selectedMembers) {
    return mRestApi.addFriendsToShopGroup(SharedPreferenceHelper.getUserId(), selectedMembers);
  }

  public Observable<List<FacebookFriend>> loadContactsFromContactBook() {
    return Observable.create(subscriber -> {
      List<FacebookFriend> allFriends = new ArrayList<>();

      HashMap<String, String> contactList = ContactsHelper.getContactList(mContentResolver);
      for (String key : contactList.keySet()) {
        FacebookFriend friend = new FacebookFriend(contactList.get(key), key,
            "https://sugarman-myb.s3.amazonaws.com/Group_New.png", FacebookFriend.CODE_INVITABLE,
            "ph");
        allFriends.add(friend);
        subscriber.onNext(allFriends);
        subscriber.onCompleted();
      }
    });
  }

  public Observable<InvitersImgUrls> loadInvitersImgUrls() {
    Timber.e(SharedPreferenceHelper.getAccessToken());
    return mRestApi.loadInvitersImgUrls(Constants.BEARER + SharedPreferenceHelper.getAccessToken());
  }

  public Observable<CountInvitesResponse> countInvites() {
    Timber.e(SharedPreferenceHelper.getAccessToken());
    return mRestApi.countInvites(Constants.BEARER + SharedPreferenceHelper.getAccessToken());
  }

  public Observable<TaskEntity> fetchTasks() {

    return mRestApi.fetchTasks();
  }

  public Observable<TaskEntity> fetchCompletedTasks()
  {
    Timber.e("Called fetchCompletedTasks");
    return mRestApi.fetchCompletedTasks(SharedPreferenceHelper.getAccessToken());
  }
}

