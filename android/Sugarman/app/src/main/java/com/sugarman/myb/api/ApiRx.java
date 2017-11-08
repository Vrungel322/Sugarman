package com.sugarman.myb.api;

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
import com.sugarman.myb.api.models.responses.users.UsersResponse;
import com.sugarman.myb.models.mentor.MentorStupidAbstraction;
import com.sugarman.myb.models.mentor.MentorsCommentsStupidAbstraction;
import com.sugarman.myb.models.mentor.comments.CommentEntity;
import java.util.List;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
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
      @Part("members[][phone_name]") List<RequestBody> phoneNames,
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

  @GET("/v1/get_comments/{mentorId}") Observable<MentorsCommentsStupidAbstraction> fetchComments(
      @Path("mentorId") String mentorId);

  @POST("/v1/me/trackings/{tracking_id}/delete_user") Observable<Response<Void>> deleteUser(
      @Path("tracking_id") String trackingId, @Body DeleteUserRequest userId);

  @POST("/v1/add_comment/{id_mentor}") Observable<Response<Void>> sendComment(
      @Path("id_mentor") String mentorId, @Body CommentEntity commentEntity);
}