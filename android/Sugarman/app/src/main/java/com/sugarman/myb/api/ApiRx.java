package com.sugarman.myb.api;

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
import com.sugarman.myb.api.models.responses.me.invites.InvitesResponse;
import com.sugarman.myb.api.models.responses.me.requests.RequestsResponse;
import com.sugarman.myb.api.models.responses.me.stats.Stats;
import com.sugarman.myb.api.models.responses.me.stats.StatsResponse;
import com.sugarman.myb.api.models.responses.trackings.TrackingInfoResponse;
import com.sugarman.myb.api.models.responses.trackings.TrackingStatsResponse;
import com.sugarman.myb.api.models.responses.trackings.TrackingsResponse;
import com.sugarman.myb.api.models.responses.users.UsersResponse;
import com.sugarman.myb.models.ContactListForServer;
import com.sugarman.myb.models.ab_testing.ABTesting;
import com.sugarman.myb.models.custom_events.RuleSet;
import com.sugarman.myb.models.iab.InAppSinglePurchase;
import com.sugarman.myb.models.iab.NextFreeSkuEntity;
import com.sugarman.myb.models.iab.PurchaseForServer;
import com.sugarman.myb.models.iab.Subscriptions;
import com.sugarman.myb.models.mentor.MentorFreeResponce;
import com.sugarman.myb.models.mentor.MentorStupidAbstraction;
import com.sugarman.myb.models.mentor.MentorsCommentsStupidAbstraction;
import com.sugarman.myb.models.mentor.StringAbstraction;
import com.sugarman.myb.models.mentor.comments.CommentEntity;
import com.sugarman.myb.utils.purchase.PurchaseTransaction;
import java.util.List;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by nikita on 19.09.17.
 */

public interface ApiRx {
  @POST("v2/users") Observable<UsersResponse> refreshRxUserData(
      @Body RefreshUserDataRequest request);

  @POST("v3/checkouts") Observable<Response<Void>> sendPurchaseData(
      @Body PurchaseDataRequest purchaseDataRequest);

  //@POST("v2/shop_invite") @FormUrlEncoded Observable<Response<Void>> addFriendsToShopGroup(
  //    @Field("user_id") String userId, @Body List<FacebookFriend> selectedMembers);

  @Multipart @POST("v2/shop_invite") Observable<Response<Void>> addFriendsToShopGroup(
      @Part("user_id") RequestBody userId, @Part("members[][fbid]") List<RequestBody> ids,
      @Part("members[][vkid]") List<RequestBody> vkids,
      @Part("members[][phone_number]") List<RequestBody> phoneNumbers,
      @Part("members[][name]") List<RequestBody> names,
      @Part("members[][vkname]") List<RequestBody> vknames,
      @Part("members[][name_phone]") List<RequestBody> phoneNames,
      @Part("members[][picture_url]") List<RequestBody> pictures,
      @Part("members[][picture_url_vk]") List<RequestBody> picturesVK,
      @Part("members[][picture_url_phone]") List<RequestBody> picturesPhone);

  @GET("v2/get_inviter_picture") Observable<InvitersImgUrls> loadInvitersImgUrls();

  @GET("v2/count_inviters") Observable<CountInvitesResponse> countInvites();

  @GET("v3/get_all_tasks") Observable<TaskEntity> fetchTasks();

  @Multipart @POST("/v2/editusers") Observable<UsersResponse> editUser(
      @Part MultipartBody.Part filePart, @Part("userId") RequestBody userId,
      @Part("fbid") RequestBody fbId, @Part("vkid") RequestBody vkId,
      @Part("phone_number") RequestBody phoneNumber, @Part("email") RequestBody email,
      @Part("picture_url") RequestBody pictureUrl, @Part("name") RequestBody name,
      @Part("token") RequestBody fbToken, @Part("vk_token") RequestBody vkToken,
      @Part("g_token") RequestBody gToken, @Header("Authorization") String accessToken);

  @GET("v3/get_products") Observable<List<ShopProductEntity>> fetchProducts();

  @GET("v3/get_completed_tasks") Observable<List<String>> fetchCompletedTasks(
      @Header("Authorization") String accessToken);

  @POST("v3/check_phone") Observable<CheckPhoneResponse> checkPhone(@Body CheckPhoneRequest phones);

  @POST("v3/check_vk") Observable<CheckVkResponse> checkVk(@Body CheckVkRequest vkRequest);

  @GET("v1/get_mentors") Observable<MentorStupidAbstraction> fetchMentors();

  @GET("v1/get_comments/{mentorId}") Observable<MentorsCommentsStupidAbstraction> fetchComments(
      @Path("mentorId") String mentorId);

  @POST("v1/me/trackings/{tracking_id}/delete_user") Observable<Response<Void>> deleteUser(
      @Path("tracking_id") String trackingId, @Body DeleteUserRequest userId);

  @POST("/v1/add_comment/{id_mentor}") Observable<Response<Void>> sendComment(
      @Path("id_mentor") String mentorId, @Body CommentEntity commentEntity);

  @POST("v1/in_app_purchases") Observable<Response<Subscriptions>> checkInAppBilling(
      @Body PurchaseForServer purchaseForServer);

  @POST("v1/in_app_purchases") Observable<Response<Subscriptions>> checkInAppBilling(
      @Body InAppSinglePurchase purchaseForServer);

  @GET("v1/check_slots") Observable<Response<NextFreeSkuEntity>> getNextFreeSku(
      @Query("device") String device);

  @Deprecated @POST("v1/in_app_purchases/cancel")
  Observable<Response<Subscriptions>> closeSubscription(@Body PurchaseForServer purchaseForServer);

  @POST("v1/contact_list") Observable<Response<Void>> sendContacts(
      @Body ContactListForServer contactsForServer);

  @GET("v1/get_rules") Observable<Response<RuleSet>> fetchRules();

  @GET("v1/get_animation") Observable<Response<GetAnimationResponse>> getAnimations();

  @POST("/v1/approveotp") Observable<Response<ApproveOtpResponse>> approveOtp(
      @Body ApproveOtpRequest request);

  @GET("v1/get_animation") Observable<Response<GetAnimationResponse>> getAnimationsByName(
      @Query("name") String name);

  @POST("v1/pokes") Observable<Response<Object>> poke(@Body PokeRequest request);

  @POST("v1/in_app_purchases") Observable<Response<Void>> checkInAppBillingOneDollar(
      @Header("tracking_id") String trackingId, @Body InAppSinglePurchase inAppSinglePurchase);

  @Multipart @POST("v1/invite_rescue")
  Observable<Response<RescueFriendResponse>> sendInvitersForRescue(
      @Part("user_id") RequestBody userId, @Part("members[][fbid]") List<RequestBody> ids,
      @Part("members[][vkid]") List<RequestBody> vkids,
      @Part("members[][phone_number]") List<RequestBody> phoneNumbers,
      @Part("members[][name]") List<RequestBody> names,
      @Part("members[][vkname]") List<RequestBody> vknames,
      @Part("members[][name_phone]") List<RequestBody> phoneNames,
      @Part("members[][picture_url]") List<RequestBody> pictures,
      @Part("members[][picture_url_vk]") List<RequestBody> picturesVK,
      @Part("members[][picture_url_phone]") List<RequestBody> picturesPhone,
      @Part("tracking_id") String trackingId);

  @GET("v1/ab_testing") Observable<Response<ABTesting>> fetchAorBtesting();

  @POST("v3/check_vk") Observable<Response<CheckVkResponse>> checkVks(@Body CheckVkRequest request);

  @POST("v1/get_free_subscription_data")
  Observable<Response<MentorFreeResponce>> purchaseMentorForFree(
      @Body PurchaseTransaction purchaseTransaction);

  @POST("v1/get_provider_data") @FormUrlEncoded
  Observable<Response<MentorsVendor>> getMentorsVendor(@Header("os") String os,
      @Field("id_mentor") String mentorId);

  @POST("v1/subscribe_for_mentor") Observable<Response<Subscriptions>> checkPurchaseTransaction(
      @Body StringAbstraction abstraction);

  @FormUrlEncoded @POST("v1/subscription/cancel") Observable<Response<Void>> closeSubscription(
      @Field("id_mentor") String mentorId, @Field("token") String token, @Header("os") String os);

  @GET("/v2/me/stats") Observable<Response<StatsResponse>> fetchStats();

  @GET("/v1/me/trackings/{tracking_id}/stats")
  Observable<Response<TrackingStatsResponse>> fetchTrackingStats(
      @Path("tracking_id") String trackingId);

  @GET("") Observable<Response<Stats>> fetchStatByDate(String date);

  @GET("/v1/me/trackings/{tracking_id}")
  Observable<Response<TrackingInfoResponse>> fetchCurrentTracking(
      @Path("tracking_id") String trackingId);

  @DELETE("/v1/me/invites/{invite_id}") Observable<Response<Object>> declineInvite(
      @Path("invite_id") String inviteId);

  @POST("/v1/me/invites/{invite_id}") Observable<Response<Object>> acceptInvite(
      @Path("invite_id") String inviteId);

  @POST("/v1/requests/{request_id}/decline") Observable<Response<Object>> declineRequest(
      @Path("request_id") String requestId);

  @POST("/v1/requests/{request_id}/accept") Observable<Response<Object>> acceptRequest(
      @Path("request_id") String requestId);

  @GET("/v1/me/invites") Observable<Response<InvitesResponse>> getMyInvites();

  @GET("/v1/me/requests") Observable<Response<RequestsResponse>> getMyRequests();

  @GET("/v1/trackings") Observable<Response<TrackingsResponse>> getTrackings(
      @Query("type") String type);

  @GET("/v1/trackings") Observable<Response<TrackingsResponse>> getTrackings(
      @Query("query") String query, @Query("type") String type);
}