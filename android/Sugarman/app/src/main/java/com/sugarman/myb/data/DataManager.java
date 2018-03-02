package com.sugarman.myb.data;

import android.content.ContentResolver;
import android.util.Log;
import com.clover_studio.spikachatmodule.models.Login;
import com.clover_studio.spikachatmodule.models.User;
import com.google.gson.Gson;
import com.sugarman.myb.R;
import com.sugarman.myb.api.RestApi;
import com.sugarman.myb.api.RestApiSpika;
import com.sugarman.myb.api.models.levelSystem.TaskEntity;
import com.sugarman.myb.api.models.requests.ApproveOtpRequest;
import com.sugarman.myb.api.models.requests.CheckPhoneRequest;
import com.sugarman.myb.api.models.requests.CheckVkRequest;
import com.sugarman.myb.api.models.requests.DeleteUserRequest;
import com.sugarman.myb.api.models.requests.PokeRequest;
import com.sugarman.myb.api.models.requests.PurchaseDataRequest;
import com.sugarman.myb.api.models.requests.RefreshUserDataRequest;
import com.sugarman.myb.api.models.responses.ApproveOtpResponse;
import com.sugarman.myb.api.models.responses.CheckPhoneResponse;
import com.sugarman.myb.api.models.responses.CheckVkResponse;
import com.sugarman.myb.api.models.responses.CountInvitesResponse;
import com.sugarman.myb.api.models.responses.InvitersImgUrls;
import com.sugarman.myb.api.models.responses.MentorsVendor;
import com.sugarman.myb.api.models.responses.RescueFriendResponse;
import com.sugarman.myb.api.models.responses.ShopProductEntity;
import com.sugarman.myb.api.models.responses.animation.GetAnimationResponse;
import com.sugarman.myb.api.models.responses.facebook.FacebookFriend;
import com.sugarman.myb.api.models.responses.me.stats.Stats;
import com.sugarman.myb.api.models.responses.me.stats.StatsResponse;
import com.sugarman.myb.api.models.responses.trackings.TrackingStatsResponse;
import com.sugarman.myb.api.models.responses.users.UsersResponse;
import com.sugarman.myb.constants.Constants;
import com.sugarman.myb.data.db.DbHelper;
import com.sugarman.myb.data.local.PreferencesHelper;
import com.sugarman.myb.models.ContactListForServer;
import com.sugarman.myb.models.ab_testing.ABTesting;
import com.sugarman.myb.models.animation.ImageModel;
import com.sugarman.myb.models.chat_refactor.GetMessagesModelRefactored;
import com.sugarman.myb.models.custom_events.Rule;
import com.sugarman.myb.models.custom_events.RuleSet;
import com.sugarman.myb.models.iab.InAppSinglePurchase;
import com.sugarman.myb.models.iab.NextFreeSkuEntity;
import com.sugarman.myb.models.iab.PurchaseForServer;
import com.sugarman.myb.models.iab.Subscriptions;
import com.sugarman.myb.models.mentor.MentorEntity;
import com.sugarman.myb.models.mentor.MentorFreeSomeLayer;
import com.sugarman.myb.models.mentor.MentorStupidAbstraction;
import com.sugarman.myb.models.mentor.MentorsCommentsStupidAbstraction;
import com.sugarman.myb.models.mentor.comments.CommentEntity;
import com.sugarman.myb.models.mentor.comments.MentorsCommentsEntity;
import com.sugarman.myb.utils.ContactsHelper;
import com.sugarman.myb.utils.SharedPreferenceHelper;
import com.sugarman.myb.utils.apps_Fly.AppsFlyRemoteLogger;
import com.sugarman.myb.utils.purchase.ProviderManager;
import com.sugarman.myb.utils.purchase.PurchaseTransaction;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import okhttp3.MultipartBody;
import retrofit2.Response;
import rx.Observable;
import timber.log.Timber;

/**
 * Created by Vrungel on 26.01.2017.
 */

public class DataManager {

  private final ProviderManager mProviderManager;
  private RestApi mRestApi;
  private RestApiSpika mRestApiSpika;
  private DbHelper mDbHelper;
  private PreferencesHelper mPref;
  private ContentResolver mContentResolver;
  private AppsFlyRemoteLogger mAppsFlyRemoteLogger;

  public DataManager(RestApiSpika restApiSpika, RestApi restApi, PreferencesHelper pref,
      ContentResolver contentResolver, DbHelper dbHelper, AppsFlyRemoteLogger appsFlyRemoteLogger,
      ProviderManager providerManager) {
    mRestApi = restApi;
    mRestApiSpika = restApiSpika;
    mPref = pref;
    mContentResolver = contentResolver;
    mDbHelper = dbHelper;
    mAppsFlyRemoteLogger = appsFlyRemoteLogger;
    mProviderManager = providerManager;
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
    entities.add(new ShopProductEntity("0", productName.get(0), "", "10",
        Arrays.asList(String.valueOf(R.drawable.cap1), String.valueOf(R.drawable.cap2),
            String.valueOf(R.drawable.cap3))));
    entities.add(new ShopProductEntity("1", productName.get(1), "", "10",
        Arrays.asList(String.valueOf(R.drawable.belt1), String.valueOf(R.drawable.belt2),
            String.valueOf(R.drawable.belt3))));
    entities.add(new ShopProductEntity("2", productName.get(2), "", "10",
        Arrays.asList(String.valueOf(R.drawable.com_5), String.valueOf(R.drawable.com_2),
            String.valueOf(R.drawable.com_3), String.valueOf(R.drawable.com_4),
            String.valueOf(R.drawable.com_1))));
    entities.add(new ShopProductEntity("3", productName.get(3), "", "1",
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

  public Observable<Response<Subscriptions>> checkInAppBilling(
      PurchaseForServer purchaseForServer) {
    return mRestApi.checkInAppBilling(purchaseForServer);
  }

  public Observable<Response<Subscriptions>> checkInAppBilling(
      InAppSinglePurchase purchaseForServer) {
    return mRestApi.checkInAppBilling(purchaseForServer);
  }

  public Observable<Response<Void>> sendContacts(ContactListForServer contactsForServer) {
    return mRestApi.sendContacts(contactsForServer);
  }

  @Deprecated public Observable<Response<Subscriptions>> closeSubscription(
      PurchaseForServer purchaseForServer) {
    return mRestApi.closeSubscription(purchaseForServer);
  }

  public Observable<Response<GetAnimationResponse>> getAnimations() {
    return mRestApi.getAnimations();
  }

  public Observable<Response<GetAnimationResponse>> getAnimationsByName(String name) {
    return mRestApi.getAnimationsByName(name);
  }

  public Observable<Response<Object>> poke(String memberId, String trakingId) {
    Observable<Response<Object>> responseObservable =
        mRestApi.poke(new PokeRequest(memberId, trakingId));
    Timber.e("poke memberId " + memberId + " trakingId " + trakingId);
    return responseObservable;
  }

  ///////////////////////////////////////////////////////////////////////////
  // DB
  ///////////////////////////////////////////////////////////////////////////
  public void cacheFriends(List<FacebookFriend> friends) {
    SharedPreferenceHelper.cacheFriends(friends);
  }

  public Observable<List<FacebookFriend>> getCachedFriends() {

    return Observable.just(SharedPreferenceHelper.getCachedFriends());
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

  public Observable<Response<RuleSet>> fetchRules() {
    return mRestApi.fetchRules();
  }

  public void saveRules(RuleSet ruleSet) {
    mDbHelper.dropRealmTable(RuleSet.class);
    mDbHelper.save(ruleSet);
  }

  public List<RuleSet> getAllRules() {
    return mDbHelper.getAll(RuleSet.class);
  }

  public List<Rule> getRuleByName(String name) {
    return mDbHelper.getElementsFromDBByQuery(Rule.class, "name", name);
  }

  public Observable<Response<ApproveOtpResponse>> approveOtp(String userId, String phoneNumberStr,
      String otp) {
    MultipartBody.Part filePart = null;

    ApproveOtpRequest request = new ApproveOtpRequest();
    request.setUserId(userId);
    request.setPhoneNumber(phoneNumberStr);
    request.setPhoneOtp(otp);
    Log.e("Request", request.toString());
    Log.e("token", "editProfileClient rabotaem");
    return mRestApi.approveOtp(request);
  }

  public void saveAnimation(GetAnimationResponse getAnimationResponseResponse) {
    Timber.e("saveAnimation");

    mDbHelper.dropRealmTable(GetAnimationResponse.class);

    mDbHelper.save(getAnimationResponseResponse);
  }

  public GetAnimationResponse getAnimationFromBd() {
    Timber.e("getAnimationFromBd");
    List<GetAnimationResponse> gar = mDbHelper.getAll(GetAnimationResponse.class);
    if (gar != null && !gar.isEmpty()) {
      return gar.get(0);
    } else {
      return null;
    }
  }

  public ImageModel getAnimationByNameFromRealm(String name) {
    for (ImageModel im : mDbHelper.getElementsFromDBByQuery(ImageModel.class, "name", name)) {
      Timber.e("imageModel name" + im.getName());
      Timber.e("imageModel id" + im.getId());
    }
    List<ImageModel> imageModels =
        mDbHelper.getElementsFromDBByQuery(ImageModel.class, "name", name);
    if (!imageModels.isEmpty()) {
      return imageModels.get(0);
    } else {
      return null;
    }
  }

  public void clearRuleDailyData() {
    List<Rule> rules = mDbHelper.getAll(Rule.class);
    for (Rule r : rules) {
      SharedPreferenceHelper.disableEventXStepsDone(r.getCount());
    }
  }

  public Observable<Response<Void>> checkInAppBillingOneDollar(String trackingId,
      InAppSinglePurchase inAppSinglePurchase) {
    return mRestApi.checkInAppBillingOneDollar(trackingId, inAppSinglePurchase);
  }

  public Observable<Response<RescueFriendResponse>> sendInvitersForRescue(
      List<FacebookFriend> members, String trackingId) {
    return mRestApi.sendInvitersForRescue(SharedPreferenceHelper.getUserId(), members, trackingId);
  }

  public Observable<Response<ABTesting>> fetchAorBtesting() {
    return mRestApi.fetchAorBtesting();
  }

  public void cacheMentors(List<MentorEntity> mentorEntities) {
    SharedPreferenceHelper.putMentorEntity(new Gson().toJson(mentorEntities));
  }

  public List<MentorEntity> getCachedMentors() {
    return SharedPreferenceHelper.getCachedMentors();
  }

  public Observable<GetMessagesModelRefactored> fetchMessagesSpika(String roomId,
      String lastMessageId, String token) {
    return mRestApiSpika.fetchMessagesSpika(roomId, lastMessageId, token);
  }

  public Observable<Login> loginSpika(User user) {
    return mRestApiSpika.loginSpika(user);
  }

  public Observable<Response<CheckVkResponse>> checkVks(List<String> vkToCheck) {
    return mRestApi.checkVks(vkToCheck);
  }

  //public Observable<Response<Subscriptions>> purchaseMentorForFree(String mentorId) {
  //  return mRestApi.purchaseMentorForFree(mentorId);
  //}

  public Observable<Response<MentorsVendor>> getMentorsVendor(String mentorId) {
    return mRestApi.getMentorsVendor(mentorId);
  }

  public Observable<Response<Subscriptions>> checkPurchaseTransaction(
      MentorFreeSomeLayer mentorFreeResponce) {
    return mRestApi.
        checkPurchaseTransaction(mentorFreeResponce);
  }

  public Observable<Response<Subscriptions>> checkPurchaseTransaction(
      PurchaseTransaction purchaseTransaction) {
    return mRestApi.
        checkPurchaseTransaction(purchaseTransaction);
  }

  public Observable<Response<Void>> closeSubscription(String mentorId, String token) {
    return mRestApi.closeSubscription(mentorId, token);
  }

  public Observable<Response<StatsResponse>> fetchStats() {
    return mRestApi.fetchStats();
  }

  public Observable<Response<TrackingStatsResponse>> fetchTrackingStats(String trackingId) {
    return mRestApi.fetchTrackingStats(trackingId);
  }

  public Observable<Response<Stats>> fetchStatByDate(String date) {
    return mRestApi.fetchStatByDate(date);
  }
}
