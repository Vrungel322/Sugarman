package com.sugarman.myb.data;

import android.content.ContentResolver;
import com.sugarman.myb.R;
import com.sugarman.myb.api.RestApi;
import com.sugarman.myb.api.models.levelSystem.TaskEntity;
import com.sugarman.myb.api.models.requests.CheckPhoneRequest;
import com.sugarman.myb.api.models.requests.CheckVkRequest;
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
import com.sugarman.myb.data.db.DbHelper;
import com.sugarman.myb.data.local.PreferencesHelper;
import com.sugarman.myb.models.mentor.MemberOfMentorsGroup;
import com.sugarman.myb.models.mentor.MentorEntity;
import com.sugarman.myb.models.mentor.MentorsSkills;
import com.sugarman.myb.utils.ContactsHelper;
import com.sugarman.myb.utils.SharedPreferenceHelper;
import java.io.File;
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

  ///////////////////////////////////////////////////////////////////////////
  // REST
  ///////////////////////////////////////////////////////////////////////////
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
    entities.add(new ShopProductEntity("0", productName.get(0), "", "0.99",
        Arrays.asList(String.valueOf(R.drawable.cap1), String.valueOf(R.drawable.cap2),
            String.valueOf(R.drawable.cap3))));
    entities.add(new ShopProductEntity("1", productName.get(1), "", "1.99",
        Arrays.asList(String.valueOf(R.drawable.belt1), String.valueOf(R.drawable.belt2),
            String.valueOf(R.drawable.belt3))));
    entities.add(new ShopProductEntity("2", productName.get(2), "", "2.99",
        Arrays.asList(String.valueOf(R.drawable.com1), String.valueOf(R.drawable.com2),
            String.valueOf(R.drawable.com3), String.valueOf(R.drawable.com4))));
    return Observable.just(entities);
    //return mRestApi.fetchProducts();
  }

  public Observable<UsersResponse> sendUserDataToServer(String phone, String email, String name,
      String fbId, String vkId, String avatar, File selectedFile) {
    return mRestApi.sendUserDataToServer(phone, email, name, fbId, vkId, avatar, selectedFile,
        Constants.BEARER + SharedPreferenceHelper.getAccessToken());
  }

  public Observable<List<MentorEntity>> fetchMentors() {
    List<MentorsSkills> mentorsSkillses = new ArrayList<>();
    List<MemberOfMentorsGroup> membersOfMentorsGroup = new ArrayList<>();
    List<MentorEntity> mentorEntities = new ArrayList<>();
    for (int i = 0; i < 7; i++) {
      mentorsSkillses.add(
          new MentorsSkills("Title " + i, Arrays.asList("Skill 1", "Skill 2", "Skill 3", "Skill 4"),
              "https://pi.tedcdn.com/r/pe.tedcdn.com/images/ted/0ef62e4df27b4ba7294de889fdbc33e476a08ec9_254x191.jpg?",
              "https://pi.tedcdn.com/r/pe.tedcdn.com/images/ted/0ef62e4df27b4ba7294de889fdbc33e476a08ec9_254x191.jpg?"));
    }

    for (int i = 0; i < 33; i++) {
      membersOfMentorsGroup.add(new MemberOfMentorsGroup("John" + i,
          "https://pi.tedcdn.com/r/pe.tedcdn.com/images/ted/0ef62e4df27b4ba7294de889fdbc33e476a08ec9_254x191.jpg?"));
    }

    for (int i = 0; i < 9; i++) {
      mentorEntities.add(new MentorEntity(String.valueOf(i),
          "https://pi.tedcdn.com/r/pe.tedcdn.com/images/ted/0ef62e4df27b4ba7294de889fdbc33e476a08ec9_254x191.jpg?",
          "Name " + i, "3", String.valueOf(i), " Description " + i, mentorsSkillses,
          membersOfMentorsGroup));
    }
    return Observable.just(mentorEntities);

    //return mRestApi.fetchMentors();
  }

  ///////////////////////////////////////////////////////////////////////////
  // DB
  ///////////////////////////////////////////////////////////////////////////
  public void cacheFriends(List<FacebookFriend> friends) {
    mDbHelper.dropRealmTable(FacebookFriend.class);
    for (int i = 0; i < friends.size(); i++) {

      if (friends.get(i).getPhotoUrl() == null) {
        friends.get(i).setPhotoUrl("https://sugarman-myb.s3.amazonaws.com/Group_New.png");
      }
      mDbHelper.save(friends.get(i));
    }
  }

  public Observable<List<FacebookFriend>> getCachedFriends() {

    return Observable.just(mDbHelper.getAll(FacebookFriend.class));
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
      }
      subscriber.onNext(allFriends);
      subscriber.onCompleted();
    });
  }

  public Observable<TaskEntity> fetchTasks() {

    return mRestApi.fetchTasks();
  }

  public Observable<List<String>> fetchCompletedTasks() {
    return mRestApi.fetchCompletedTasks(Constants.BEARER + SharedPreferenceHelper.getAccessToken());
  }

  public Observable<CheckPhoneResponse> checkPhone(List<String> phones) {
    return mRestApi.checkPhone(Constants.BEARER + SharedPreferenceHelper.getAccessToken(),
        new CheckPhoneRequest(phones));
  }

  public Observable<CheckVkResponse> checkVk(List<String> vkIds) {
    return mRestApi.checkVk(Constants.BEARER + SharedPreferenceHelper.getAccessToken(),
        new CheckVkRequest(vkIds));
  }
}
