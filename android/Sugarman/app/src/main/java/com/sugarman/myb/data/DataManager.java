package com.sugarman.myb.data;

import android.content.ContentResolver;
import com.sugarman.myb.R;
import com.sugarman.myb.api.RestApi;
import com.sugarman.myb.api.models.requests.PurchaseDataRequest;
import com.sugarman.myb.api.models.requests.RefreshUserDataRequest;
import com.sugarman.myb.api.models.responses.CountInvitesResponse;
import com.sugarman.myb.api.models.responses.InvitersImgUrls;
import com.sugarman.myb.api.models.responses.ShopProductEntity;
import com.sugarman.myb.api.models.responses.facebook.FacebookFriend;
import com.sugarman.myb.api.models.responses.users.UsersResponse;
import com.sugarman.myb.constants.Constants;
import com.sugarman.myb.data.db.DbHelper;
import com.sugarman.myb.data.local.PreferencesHelper;
import com.sugarman.myb.utils.ContactsHelper;
import com.sugarman.myb.utils.SharedPreferenceHelper;
import java.util.ArrayList;
import java.util.Arrays;
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
  private DbHelper mDbHelper;
  private PreferencesHelper mPref;
  private ContentResolver mContentResolver;

  public DataManager(RestApi restApi, PreferencesHelper pref, ContentResolver contentResolver,
      DbHelper dbHelper) {
    mRestApi = restApi;
    mPref = pref;
    mContentResolver = contentResolver;
    mDbHelper = dbHelper;
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

  public Observable<List<ShopProductEntity>> fetchProducts(List<String> productName) {
    ArrayList<ShopProductEntity> entities = new ArrayList<>();
    entities.add(new ShopProductEntity("0", productName.get(0), "", "",
        Arrays.asList(String.valueOf(R.drawable.cap1), String.valueOf(R.drawable.cap2),
            String.valueOf(R.drawable.cap3))));
    entities.add(new ShopProductEntity("1", productName.get(1), "", "",
        Arrays.asList(String.valueOf(R.drawable.belt1), String.valueOf(R.drawable.belt2),
            String.valueOf(R.drawable.belt3))));
    entities.add(new ShopProductEntity("2", productName.get(2), "", "",
        Arrays.asList(String.valueOf(R.drawable.com1), String.valueOf(R.drawable.com2),
            String.valueOf(R.drawable.com3), String.valueOf(R.drawable.com4))));
    return Observable.just(entities);
    //return mRestApi.fetchProducts();
  }

  public void cacheFriends(List<FacebookFriend> friends) {
    mDbHelper.dropRealmTable(FacebookFriend.class);
    for (int i = 0; i < friends.size(); i++) {
      mDbHelper.save(friends.get(i));
    }

    //for (int i = 0; i < mDbHelper.getAll(FacebookFriend.class).size(); i++) {
    //  Timber.e("Cached item save "
    //      + mDbHelper.getAll(FacebookFriend.class).get(i).getName()
    //      + " id : "
    //      + mDbHelper.getAll(FacebookFriend.class).get(i).getId()
    //      + " photo : "
    //      + mDbHelper.getAll(FacebookFriend.class).get(i).getPhotoUrl());
    //}
    Timber.e(
        "_______________________________________________________________________________Total : "
            + mDbHelper.getAll(FacebookFriend.class).size());
  }

  public Observable<List<FacebookFriend>> getCachedFriends() {
    //Timber.e("Cached size get " + mDbHelper.getAll(FacebookFriend.class).size());

    return Observable.just(mDbHelper.getAll(FacebookFriend.class));
  }
}

