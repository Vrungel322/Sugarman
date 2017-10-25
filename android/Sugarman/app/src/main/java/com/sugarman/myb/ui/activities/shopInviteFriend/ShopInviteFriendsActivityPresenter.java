package com.sugarman.myb.ui.activities.shopInviteFriend;

import android.util.Log;
import com.arellomobile.mvp.InjectViewState;
import com.sugarman.myb.App;
import com.sugarman.myb.api.models.responses.facebook.FacebookFriend;
import com.sugarman.myb.base.BasicPresenter;
import com.sugarman.myb.constants.Constants;
import com.sugarman.myb.data.DataManager;
import com.sugarman.myb.utils.RxBus;
import com.sugarman.myb.utils.RxBusHelper;
import com.sugarman.myb.utils.ThreadSchedulers;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import rx.Observable;
import rx.Subscription;
import timber.log.Timber;

/**
 * Created by nikita on 26.09.17.
 */
@InjectViewState public class ShopInviteFriendsActivityPresenter
    extends BasicPresenter<IShopInviteFriendsActivityView> {
  @Inject RxBus mRxBus;
  @Inject DataManager mDataManager;

  @Override protected void inject() {
    App.getAppComponent().inject(this);
  }

  @Override protected void onFirstViewAttach() {
    super.onFirstViewAttach();
    subscribeShowAddFriendBtnEvent();
    subscribeHideAddFriendBtnEvent();
    loadVkFriends();
    loadPhoneNumbersContacts();
    loadInvitersImg();
    loadInvitersCount();
  }

  private void loadInvitersCount() {
    Subscription subscription = mDataManager.countInvites()
        .compose(ThreadSchedulers.applySchedulers())
        .subscribe(countInvitesResponse -> {
        }, Throwable::printStackTrace);
    addToUnsubscription(subscription);
  }

  private void loadInvitersImg() {
    Subscription subscription = mDataManager.loadInvitersImgUrls()
        .compose(ThreadSchedulers.applySchedulers())
        .subscribe(urls -> {
          getViewState().loadInviterImgUrls(urls.getPictures());
        }, throwable -> {
          Timber.e("!!!!!! " + throwable.toString());
        });
    addToUnsubscription(subscription);
  }

  private void loadPhoneNumbersContacts() {
    Subscription subscription = mDataManager.loadContactsFromContactBook()
        .compose(ThreadSchedulers.applySchedulers())
        .subscribe(facebookFriends -> {
          getViewState().addPhoneContact(facebookFriends);
        }, Throwable::printStackTrace);
    getViewState().hideLoader();
    addToUnsubscription(subscription);
  }

  private void subscribeShowAddFriendBtnEvent() {
    Subscription subscription = mRxBus.filteredObservable(RxBusHelper.ShowAddFriendBtnEvent.class)
        .compose(ThreadSchedulers.applySchedulers())
        .subscribe(showAddFriendBtnEvent -> getViewState().showAddFriendBtn());
    addToUnsubscription(subscription);
  }

  private void subscribeHideAddFriendBtnEvent() {
    Subscription subscription = mRxBus.filteredObservable(RxBusHelper.HideAddFriendBtnEvent.class)
        .compose(ThreadSchedulers.applySchedulers())
        .subscribe(hideAddFriendBtnEvent -> getViewState().hideAddFriendButton());
    addToUnsubscription(subscription);
  }

  public void addFriendsToShopGroup(ArrayList<FacebookFriend> selectedMembers) {
    Subscription subscription = mDataManager.addFriendsToShopGroup(selectedMembers)
        .compose(ThreadSchedulers.applySchedulers())
        .subscribe(voidResponse -> {
          if (voidResponse.code() == Constants.SUCCESS_RESPONSE_CODE) {
            getViewState().finishShopInviteActivity();
            getViewState().showToast();
          }
        }, Throwable::printStackTrace);
    addToUnsubscription(subscription);
  }

  public void loadVkFriends() {
    VKRequest request = new VKRequest("friends.get",
        VKParameters.from(VKApiConst.FIELDS, "photo_100", "order", "name"));
    request.executeWithListener(new VKRequest.VKRequestListener() {
      @Override public void onComplete(VKResponse response) {
        super.onComplete(response);
        JSONObject resp = response.json;
        try {
          JSONArray items = resp.getJSONObject("response").getJSONArray("items");
          List<FacebookFriend> friendsVk = new ArrayList<FacebookFriend>();
          for (int i = 0; i < items.length(); i++) {

            JSONObject item = items.getJSONObject(i);
            int id = item.getInt("id");
            String firstName = item.getString("first_name");
            String lastName = item.getString("last_name");
            String photoUrl = item.getString("photo_100");
            int online = item.getInt("online");

            FacebookFriend friend =
                new FacebookFriend(Integer.toString(id), firstName + " " + lastName, photoUrl,
                    FacebookFriend.CODE_INVITABLE, "vk");
            friendsVk.add(friend);
          }
          getViewState().hideLoader();
          getViewState().addVkFriends(friendsVk);
        } catch (JSONException e) {
          e.printStackTrace();
        }
      }

      @Override public void onError(VKError error) {
        super.onError(error);
        Timber.e("VK Error");
        //Log.e("VK", error.errorMessage);
      }
    });
  }

  public void filterFriends(String s, List<FacebookFriend> friendsToFilter) {
    Subscription subscription;
    if (!s.isEmpty()) {
      subscription = Observable.from(friendsToFilter)
          .filter(facebookFriend -> facebookFriend.getName().toLowerCase().contains(s.trim()))
          .toList()
          .subscribe(friends -> getViewState().updateRvFriends(friends),
              Timber::e);

      addToUnsubscription(subscription);
    } else {
      getViewState().updateRvFriends(friendsToFilter);
    }
  }

  public void sendInvitationInVk(List<FacebookFriend> selectedFriends, String inviteMsg) {
    for (FacebookFriend friend : selectedFriends) {
      if (friend.getSocialNetwork().equals("vk")) {
        VKRequest request = new VKRequest("messages.send",
            VKParameters.from(VKApiConst.USER_ID, Integer.parseInt(friend.getId()),
                VKApiConst.MESSAGE, inviteMsg));
        request.executeWithListener(new VKRequest.VKRequestListener() {
          @Override public void onComplete(VKResponse response) {
            super.onComplete(response);
            JSONObject resp = response.json;
            Log.e("VK response", response.responseString);
          }

          @Override public void onError(VKError error) {
            super.onError(error);
            Log.e("VK response", " " + error.errorCode + error.toString());
          }
        });
      }
    }
  }
}
