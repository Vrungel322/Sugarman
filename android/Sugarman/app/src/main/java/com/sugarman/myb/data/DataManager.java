package com.sugarman.myb.data;

import android.content.ContentResolver;
import com.sugarman.myb.R;
import com.sugarman.myb.api.RestApi;
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
import com.sugarman.myb.data.db.DbHelper;
import com.sugarman.myb.data.local.PreferencesHelper;
import com.sugarman.myb.models.ContactForServer;
import com.sugarman.myb.models.ContactListForServer;
import com.sugarman.myb.models.iab.NextFreeSkuEntity;
import com.sugarman.myb.models.iab.PurchaseForServer;
import com.sugarman.myb.models.mentor.MentorStupidAbstraction;
import com.sugarman.myb.models.mentor.MentorsCommentsStupidAbstraction;
import com.sugarman.myb.models.mentor.comments.CommentEntity;
import com.sugarman.myb.models.mentor.comments.MentorsCommentsEntity;
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
      String num, String productName, String paymentType, String productPrice) {

    PurchaseDataRequest purchaseDataRequest =
        new PurchaseDataRequest(countryName, cityName, streetName, zipCode, fullName, amountPrice,
            productName, num, productPrice, paymentType, phoneNumber);
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
        Arrays.asList(String.valueOf(R.drawable.com_1), String.valueOf(R.drawable.com_2),
            String.valueOf(R.drawable.com_3), String.valueOf(R.drawable.com_4),
            String.valueOf(R.drawable.com_5))));
    entities.add(new ShopProductEntity("3", productName.get(3), "", "2.99",
        Arrays.asList(String.valueOf(R.drawable.mentor_shop))));
    return Observable.just(entities);
    //return mRestApi.fetchProducts();
  }

  public Observable<UsersResponse> sendUserDataToServer(String phone, String email, String name,
      String fbId, String vkId, String avatar, File selectedFile) {
    return mRestApi.sendUserDataToServer(phone, email, name, fbId, vkId, avatar, selectedFile,
        Constants.BEARER + SharedPreferenceHelper.getAccessToken());
  }

  public Observable<MentorStupidAbstraction> fetchMentors() {
    //  List<MentorsSkills> mentorsSkillses = new ArrayList<>();
    //  List<MemberOfMentorsGroup> membersOfMentorsGroup = new ArrayList<>();
    //  List<MentorEntity> mentorEntities = new ArrayList<>();
    //  for (int i = 0; i < 7; i++) {
    //    mentorsSkillses.add(
    //        new MentorsSkills("Title " + i, Arrays.asList("Skill 1", "Skill 2", "Skill 3", "Skill 4"),
    //            "https://pi.tedcdn.com/r/pe.tedcdn.com/images/ted/0ef62e4df27b4ba7294de889fdbc33e476a08ec9_254x191.jpg?",
    //            "https://pi.tedcdn.com/r/pe.tedcdn.com/images/ted/0ef62e4df27b4ba7294de889fdbc33e476a08ec9_254x191.jpg?"));
    //  }
    //
    //  for (int i = 0; i < 33; i++) {
    //    membersOfMentorsGroup.add(new MemberOfMentorsGroup("John" + i,
    //        "https://pi.tedcdn.com/r/pe.tedcdn.com/images/ted/0ef62e4df27b4ba7294de889fdbc33e476a08ec9_254x191.jpg?",
    //        i + ""));
    //  }
    //
    //  for (int i = 0; i < 9; i++) {
    //    mentorEntities.add(new MentorEntity(String.valueOf(i),
    //        "https://pi.tedcdn.com/r/pe.tedcdn.com/images/ted/0ef62e4df27b4ba7294de889fdbc33e476a08ec9_254x191.jpg?",
    //        "Name " + i, "2.8", String.valueOf(i), " Description " + i, mentorsSkillses,
    //        membersOfMentorsGroup));
    //
    //    mentorEntities.add(new MentorEntity(String.valueOf(i),
    //        "https://pi.tedcdn.com/r/pe.tedcdn.com/images/ted/0ef62e4df27b4ba7294de889fdbc33e476a08ec9_254x191.jpg?",
    //        "Name " + i, "2.2", String.valueOf(i), " Description " + i, mentorsSkillses,
    //        membersOfMentorsGroup));
    //  }
    //return Observable.just(mentorEntities);

    return mRestApi.fetchMentors();
  }

  public Observable<MentorsCommentsStupidAbstraction> fetchComments(String mentorId) {
    List<MentorsCommentsEntity> mentorsCommentsEntities = new ArrayList<>();
    for (int i = 0; i < 33; i++) {
      mentorsCommentsEntities.add(new MentorsCommentsEntity("Name Name " + i,
          "https://pi.tedcdn.com/r/pe.tedcdn.com/images/ted/0ef62e4df27b4ba7294de889fdbc33e476a08ec9_254x191.jpg?",
          "3.4", "A lot Of stupid text"));
    }
    //return Observable.just(mentorsCommentsEntities);
    return mRestApi.fetchComments(mentorId);
  }

  public Observable<Response<Void>> checkInAppBilling(PurchaseForServer purchaseForServer) {
    return mRestApi.checkInAppBilling(purchaseForServer);
  }

  public Observable<Response<Void>> sendContacts(ContactListForServer contactsForServer) {
    return mRestApi.sendContacts(contactsForServer);
  }

  public Observable<Response<Void>> closeSubscription(PurchaseForServer purchaseForServer) {
    return mRestApi.closeSubscription(purchaseForServer);
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

  public Observable<Response<Void>> sendComment(String mentorsId, String rating,
      String commentBody) {
    return mRestApi.sendComment(mentorsId, new CommentEntity(rating, commentBody));
  }

  public Observable<Response<Void>> deleteUser(String trackingId, String userId) {
    DeleteUserRequest request = new DeleteUserRequest(userId);
    return mRestApi.deleteUser(trackingId, request);
  }

  public Observable<Response<NextFreeSkuEntity>> getNextFreeSku() {
    return mRestApi.getNextFreeSku();
  }
}
