package com.sugarman.myb.api;

import com.sugarman.myb.api.models.requests.PurchaseDataRequest;
import com.sugarman.myb.api.models.requests.RefreshUserDataRequest;
import com.sugarman.myb.api.models.responses.CountInvitesResponse;
import com.sugarman.myb.api.models.responses.InvitersImgUrls;
import com.sugarman.myb.api.models.responses.ShopProductEntity;
import com.sugarman.myb.api.models.responses.users.UsersResponse;
import java.util.List;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import rx.Observable;

/**
 * Created by nikita on 19.09.17.
 */

public interface ApiRx {
  @POST("v2/users") Observable<UsersResponse> refreshRxUserData(
      @Body RefreshUserDataRequest request);

  @POST("v2/checkouts") Observable<Response<Void>> sendPurchaseData(
      @Body PurchaseDataRequest purchaseDataRequest);

  //@POST("v2/shop_invite") @FormUrlEncoded Observable<Response<Void>> addFriendsToShopGroup(
  //    @Field("user_id") String userId, @Body List<FacebookFriend> selectedMembers);

  @Multipart @POST("v2/shop_invite") Observable<Response<Void>> addFriendsToShopGroup(
      @Part("user_id") RequestBody userId, @Part("members[][fbid]") List<RequestBody> ids,
      @Part("members[][vkid]") List<RequestBody> vkids,
      @Part("members[][phonenumber]") List<RequestBody> phoneNumbers,
      @Part("members[][name]") List<RequestBody> names,
      @Part("members[][vkname]") List<RequestBody> vknames,
      @Part("members[][phonename]") List<RequestBody> phoneNames,
      @Part("members[][picture_url]") List<RequestBody> pictures,
      @Part("members[][picture_url_vk]") List<RequestBody> picturesVK,
      @Part("members[][picture_url_phone]") List<RequestBody> picturesPhone);

  @GET("v2/get_inviter_picture") Observable<InvitersImgUrls> loadInvitersImgUrls(
      @Header("Authorization") String accessToken);

  @GET("v2/count_inviters") Observable<CountInvitesResponse> countInvites(
      @Header("Authorization") String accessToken);

  @GET("v3/get_products") Observable<List<ShopProductEntity>> fetchProducts();

  @Multipart @POST("/v2/editusers") Observable<UsersResponse> editUser(
      @Part MultipartBody.Part filePart, @Part("userId") RequestBody userId,
      @Part("fbid") RequestBody fbId, @Part("vkid") RequestBody vkId,
      @Part("phone_number") RequestBody phoneNumber, @Part("email") RequestBody email,
      @Part("picture_url") RequestBody pictureUrl, @Part("name") RequestBody name,
      @Part("token") RequestBody fbToken, @Part("vk_token") RequestBody vkToken,
      @Part("g_token") RequestBody gToken, @Header("Authorization") String accessToken);
}