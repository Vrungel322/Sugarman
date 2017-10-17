package com.sugarman.myb.api;

import com.sugarman.myb.api.models.requests.AddMembersRequest;
import com.sugarman.myb.api.models.requests.ApproveOtpRequest;
import com.sugarman.myb.api.models.requests.CheckPhoneRequest;
import com.sugarman.myb.api.models.requests.CheckVkRequest;
import com.sugarman.myb.api.models.requests.CountInvitesRequest;
import com.sugarman.myb.api.models.requests.JoinGroupRequest;
import com.sugarman.myb.api.models.requests.PokeRequest;
import com.sugarman.myb.api.models.requests.RefreshUserDataRequest;
import com.sugarman.myb.api.models.requests.ResendMessageRequest;
import com.sugarman.myb.api.models.requests.SendFirebaseTokenRequest;
import com.sugarman.myb.api.models.requests.StatsRequest;
import com.sugarman.myb.api.models.responses.AllMyUserDataResponse;
import com.sugarman.myb.api.models.responses.ApproveOtpResponse;
import com.sugarman.myb.api.models.responses.CheckPhoneResponse;
import com.sugarman.myb.api.models.responses.CheckVkResponse;
import com.sugarman.myb.api.models.responses.CountInvitesResponse;
import com.sugarman.myb.api.models.responses.ResendMessageResponse;
import com.sugarman.myb.api.models.responses.devices.DevicesResponse;
import com.sugarman.myb.api.models.responses.me.EditGroupResponse;
import com.sugarman.myb.api.models.responses.me.groups.CreateGroupResponse;
import com.sugarman.myb.api.models.responses.me.invites.InvitesResponse;
import com.sugarman.myb.api.models.responses.me.join.JoinGroupResponse;
import com.sugarman.myb.api.models.responses.me.notifications.NotificationsResponse;
import com.sugarman.myb.api.models.responses.me.requests.RequestsResponse;
import com.sugarman.myb.api.models.responses.me.score.HighScoreResponse;
import com.sugarman.myb.api.models.responses.me.stats.StatsResponse;
import com.sugarman.myb.api.models.responses.me.trackings.MyTrackingsResponse;
import com.sugarman.myb.api.models.responses.trackings.TrackingInfoResponse;
import com.sugarman.myb.api.models.responses.trackings.TrackingStatsResponse;
import com.sugarman.myb.api.models.responses.trackings.TrackingsResponse;
import com.sugarman.myb.api.models.responses.users.UsersResponse;
import java.util.List;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface Api {

  @POST("/v2/users") Call<UsersResponse> refreshUserData(@Body RefreshUserDataRequest request);

  @POST("/v1/approveotp") Call<ApproveOtpResponse> approveOtp(@Body ApproveOtpRequest request);

  @POST("/v2/count_inviters") Call<CountInvitesResponse> countInvites(@Body
      CountInvitesRequest request);



  @Multipart @POST("/v2/editusers") Call<UsersResponse> editUser(@Part MultipartBody.Part filePart,
      @Part("userId") RequestBody userId, @Part("fbid") RequestBody fbId,
      @Part("vkid") RequestBody vkId, @Part("phone_number") RequestBody phoneNumber,
      @Part("email") RequestBody email, @Part("picture_url") RequestBody pictureUrl,
      @Part("name") RequestBody name, @Part("token") RequestBody fbToken,
      @Part("vk_token") RequestBody vkToken, @Part("g_token") RequestBody gToken);

  @Multipart @POST("/v1/me/groups") Call<CreateGroupResponse> createGroup(
      @Part MultipartBody.Part filePart, @Part("name") RequestBody name,
      @Part("facebook_token") RequestBody facebookToken,
      @Part("members[][fbid]") List<RequestBody> ids,
      @Part("members[][vkid]") List<RequestBody> vkids,
      @Part("members[][phonenumber]") List<RequestBody> phoneNumbers,
      @Part("members[][name]") List<RequestBody> names,
      @Part("members[][vkname]") List<RequestBody> vknames,
      @Part("members[][phonename]") List<RequestBody> phoneNames,
      @Part("members[][picture_url]") List<RequestBody> pictures,
      @Part("members[][picture_url_vk]") List<RequestBody> picturesVK,
      @Part("members[][picture_url_phone]") List<RequestBody> picturesPhone);

  @POST("/v2/resend_message") Call<ResendMessageResponse> resendMessage(@Body
      ResendMessageRequest request);

  @POST("/v1/devices") Call<DevicesResponse> sendFirebaseToken(
      @Body SendFirebaseTokenRequest request);

  @GET("/v1/me/trackings") Call<MyTrackingsResponse> getMyTrackings();

  //@GET("/v1/me") Call<MyUserResponse> getMyUser();

  @GET("/v1/me/invites") Call<InvitesResponse> getMyInvites();

  @GET("/v2/me/stats") Call<StatsResponse> getMyStats();

  @GET("/v1/me/trackings/{tracking_id}/requests") Call<RequestsResponse> getMyRequests(
      @Path("tracking_id") String trackingId);

  @GET("/v1/me/requests") Call<RequestsResponse> getMyRequests();

  @GET("/v1/me/notifications") Call<NotificationsResponse> getMyNotifications();

  @GET("/v2/me") Call<AllMyUserDataResponse> getMyAllUserData();

  @GET("/v1/trackings") Call<TrackingsResponse> getTrackings(@Query("type") String type);

  @GET("/v1/trackings") Call<TrackingsResponse> getTrackings(@Query("query") String query,
      @Query("type") String type);

  @POST("/v1/trackings/{tracking_id}/requests") Call<JoinGroupResponse> joinGroup(
      @Path("tracking_id") String trackingId, @Body JoinGroupRequest request);

  @POST("/v1/me/trackings") Call<JoinGroupResponse> joinGroup(@Body JoinGroupRequest request);

  @POST("/v1/requests/{request_id}/accept") Call<Object> acceptRequest(
      @Path("request_id") String requestId);

  @POST("/v1/requests/{request_id}/decline") Call<Object> declineRequest(
      @Path("request_id") String requestId);

  @POST("/v2/trackings/{tracking_id}/regroup") Call<Object> recreateGroup(
      @Path("tracking_id") String trackingId);

  @POST("/v1/me/invites/{invite_id}") Call<Object> acceptInvite(@Path("invite_id") String inviteId);

  @DELETE("/v1/me/invites/{invite_id}") Call<Object> declineInvite(
      @Path("invite_id") String inviteId);

  @GET("/v1/me/highscores") Call<HighScoreResponse> getHighScore();

  @Multipart @POST("/v2/trackings/{tracking_id}/members") Call<EditGroupResponse> editGroup(
      @Path("tracking_id") String groupId, @Part MultipartBody.Part filePart,
      @Part("tracking_name") RequestBody name, @Part("members[][fbid]") List<RequestBody> ids,
      @Part("members[][vkid]") List<RequestBody> vkids,
      @Part("members[][phonenumber]") List<RequestBody> phoneNumbers,
      @Part("members[][name]") List<RequestBody> names,
      @Part("members[][vkname]") List<RequestBody> vknames,
      @Part("members[][phonename]") List<RequestBody> phoneNames,
      @Part("members[][picture_url]") List<RequestBody> pictures,
      @Part("members[][picture_url_vk]") List<RequestBody> picturesVK,
      @Part("members[][picture_url_phone]") List<RequestBody> picturesPhone);

  @POST("/v1/pokes") Call<Object> poke(@Body PokeRequest request);

  @POST("/v1/trackings/{group_id}/members") Call<Object> addMembers(
      @Path("group_id") String groupId, @Body AddMembersRequest request);

  @DELETE("/v1/notifications/{notification_id}") Call<Object> markNotification(
      @Path("notification_id") String notificationId);

  @GET("/v1/me/trackings/{tracking_id}/stats") Call<TrackingStatsResponse> getTrackingStats(
      @Path("tracking_id") String trackingId);

  @GET("/v1/me/trackings/{tracking_id}") Call<TrackingInfoResponse> getTrackingInfo(
      @Path("tracking_id") String trackingId);

  @POST("/v1/stats/day") Call<Void> reportStats(@Body StatsRequest requst);

  @POST("/v3/check_phone") Call<CheckPhoneResponse> checkPhone(@Body CheckPhoneRequest phones);

  @POST("/v3/check_vk") Call<CheckVkResponse> checkVk(@Body CheckVkRequest phones);
}
