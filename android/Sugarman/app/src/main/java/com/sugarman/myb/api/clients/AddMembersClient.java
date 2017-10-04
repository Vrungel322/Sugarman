package com.sugarman.myb.api.clients;

import com.sugarman.myb.App;
import com.sugarman.myb.api.models.requests.AddMembersRequest;
import com.sugarman.myb.api.models.requests.Member;
import com.sugarman.myb.api.models.responses.facebook.FacebookFriend;
import com.sugarman.myb.listeners.ApiAddMembersListener;
import com.sugarman.myb.listeners.ApiBaseListener;
import java.lang.ref.WeakReference;
import java.util.List;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class AddMembersClient extends BaseApiClient {

  private static final String TAG = AddMembersClient.class.getName();

  private final Callback<Object> mCallback = new Callback<Object>() {

    @Override public void onResponse(Call<Object> call, Response<Object> response) {
      Timber.e("onResponse");
      Object dataResponse = response.body();
      ResponseBody errorBody = response.errorBody();

      if (clientListener.get() != null) {
        if (dataResponse != null) {
          //Invite[] result = dataResponse.getResult();
          ((ApiAddMembersListener) clientListener.get()).onApiAddMembersSuccess();
        } else if (errorBody != null) {
          String errorMessage = parseErrorBody(errorBody);
          responseFailure(TAG, errorMessage);
          if (response.code() == 401) {
            clientListener.get().onApiUnauthorized();
          } else if (response.code() == OLD_VERSION_CODE) {
            clientListener.get().onUpdateOldVersion();
          } else {
            ((ApiAddMembersListener) clientListener.get()).onApiAddMembersFailure(errorMessage);
          }
        } else {
          responseIsNull(TAG);
          ((ApiAddMembersListener) clientListener.get()).onApiAddMembersFailure(RESPONSE_IS_NULL);
        }
      } else {
        listenerNotRegistered(TAG);
      }
    }

    @Override public void onFailure(Call<Object> call, Throwable t) {
      if (clientListener.get() != null) {
        String message = requestFailure(TAG, t);
        ((ApiAddMembersListener) clientListener.get()).onApiAddMembersFailure(message);
      } else {
        listenerNotRegistered(TAG);
      }
    }
  };

  @Override public void registerListener(ApiBaseListener listener) {
    clientListener = new WeakReference<>(listener);
  }

  public void addMembers(String trackingId, List<FacebookFriend> friends) {
    AddMembersRequest request = new AddMembersRequest();
    int friendsCount = friends.size();
    Member[] members = new Member[friendsCount];

    for (int i = 0; i < friendsCount; i++) {
      Member member = new Member();
      FacebookFriend friend = friends.get(i);
      if (friend.getSocialNetwork().equals("fb")) {
        member.setFbid(friend.getId());
      } else {
        if (friend.getSocialNetwork().equals("vk")) {
          member.setVkid(friend.getId());
        }
      }
      member.setName(friend.getName());
      member.setPictureUrl(friend.getPicture());

      members[i] = member;
    }

    request.setMembers(members);

    Call<Object> call = App.getApiInstance().addMembers(trackingId, request);
    Timber.e("Called addMembers");
    call.enqueue(mCallback);
  }
}