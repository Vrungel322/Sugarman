package com.sugarman.myb.ui.fragments.list_friends_fragment;

import android.os.Bundle;
import android.text.TextUtils;
import com.arellomobile.mvp.InjectViewState;
import com.facebook.AccessToken;
import com.facebook.FacebookRequestError;
import com.facebook.GraphRequest;
import com.facebook.HttpMethod;
import com.google.gson.Gson;
import com.sugarman.myb.App;
import com.sugarman.myb.api.models.responses.Member;
import com.sugarman.myb.api.models.responses.Phones;
import com.sugarman.myb.api.models.responses.facebook.FacebookFriend;
import com.sugarman.myb.api.models.responses.facebook.FacebookFriendResponse;
import com.sugarman.myb.api.models.responses.facebook.FacebookInvitableResponse;
import com.sugarman.myb.base.BasicPresenter;
import com.sugarman.myb.constants.Config;
import com.sugarman.myb.constants.Constants;
import com.sugarman.myb.utils.SharedPreferenceHelper;
import com.sugarman.myb.utils.ThreadSchedulers;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import rx.Observable;
import rx.Subscription;
import timber.log.Timber;

/**
 * Created by nikita on 19.12.2017.
 */
@InjectViewState public class FriendListFragmentPresenter
    extends BasicPresenter<IFriendListFragmentView> {
  private final List<String> recipients = new ArrayList<>();
  private List<FacebookFriend> allFriendsToShow = new ArrayList<>();
  private Gson gson;
  private int expectedCountFriends;

  @Override protected void inject() {
    App.getAppComponent().inject(this);
  }

  @Override protected void onFirstViewAttach() {
    super.onFirstViewAttach();
    gson = new Gson();
    getViewState().setUpUI();
    loadVkFriends();
    loadPhFriends();
    loadFbFriends();
  }

  private void loadFbFriends() {
    if (SharedPreferenceHelper.getCachedFbFriends() != null) {
      getViewState().showFBCounters(SharedPreferenceHelper.getCachedFbFriends());
    }

    Bundle parameters = new Bundle();
    parameters.putString(Constants.FB_FIELDS, Config.FB_FRIENDS_FIELDS);
    AccessToken accessToken = AccessToken.getCurrentAccessToken();

    if (accessToken != null) {
      String graphPath = String.format(Locale.US, Constants.FB_FRIENDS_GRAPH_PATH_TEMPLATE,
          accessToken.getUserId(), Config.FB_FRIENDS_LIMIT);

      String graphPathInviteble =
          String.format(Locale.US, Constants.FB_INVITABLE_FRIENDS_GRAPH_PATH_TEMPLATE,
              accessToken.getUserId(), Config.FB_FRIENDS_LIMIT);

      GraphRequest friends =
          new GraphRequest(accessToken, graphPath, parameters, HttpMethod.GET, response -> {
            String rawResponse = response.getRawResponse();
            Timber.e("loadFbFriends rawResponse friends: " + rawResponse);
            FacebookFriend[] parsedFriends = parseFriendsResponse(rawResponse);
            boolean isInvitable = parsedFriends.length != expectedCountFriends;
            if (SharedPreferenceHelper.getCachedFbFriends() != null && rawResponse == null) {
              Timber.e("loadFbFriends " + SharedPreferenceHelper.getCachedFbFriends().size());
              getViewState().setFriendsFb(SharedPreferenceHelper.getCachedFbFriends());
              allFriendsToShow.addAll(SharedPreferenceHelper.getCachedFbFriendsNotInviteble());
            }
            getViewState().setFriendsFb(allFriendsToShow);
          });

      GraphRequest invitableFriends =
          new GraphRequest(accessToken, graphPathInviteble, parameters, HttpMethod.GET,
              response -> {
                String rawResponse = response.getRawResponse();
                Timber.e("loadFbFriends invitable friends: " + rawResponse);
                parseInvitableFriendsResponse(rawResponse);
                if (SharedPreferenceHelper.getCachedFbFriends() != null && rawResponse == null) {
                  getViewState().setFriendsFb(SharedPreferenceHelper.getCachedFbFriends());
                  allFriendsToShow.addAll(SharedPreferenceHelper.getCachedFbFriendsInviteble());
                }
                getViewState().setFriendsFb(allFriendsToShow);
              });

      friends.executeAsync();
      invitableFriends.executeAsync();
    }
  }

  private void loadPhFriends() {
    getViewState().showPHCounters(SharedPreferenceHelper.getCachedPhFriends());
    List<FacebookFriend> friendsFromPhone = new ArrayList<>();
    Subscription subscription = mDataManager.loadContactsFromContactBook()
        .concatMap(friends -> {
          friendsFromPhone.addAll(friends);
          return Observable.just(friends);
        })
        .concatMap(Observable::from)
        .concatMap(facebookFriend -> Observable.just(facebookFriend.getId()))
        .toList()
        .concatMap(
            phones -> mDataManager.checkPhone(phones).compose(ThreadSchedulers.applySchedulers()))
        .concatMap(checkPhoneResponse -> {
          for (Phones p : checkPhoneResponse.getPhones()) {
            for (FacebookFriend friend : friendsFromPhone) {
              if (friend.getSocialNetwork().equals("ph")) {
                if (friend.getId().equals(p.getPhone())) {
                  friend.setIsInvitable(FacebookFriend.CODE_NOT_INVITABLE);
                }
              }
            }
          }
          return Observable.just(friendsFromPhone);
        })
        .concatMap(Observable::from)
        //.filter(friend -> friend.getIsInvitable() != FacebookFriend.CODE_NOT_INVITABLE)
        .toList()
        .compose(ThreadSchedulers.applySchedulers())
        .subscribe(facebookFriends -> {
          allFriendsToShow.addAll(facebookFriends);
          getViewState().setFriendsPh(allFriendsToShow);
        }, throwable -> {
          Timber.e("loadPhFriends onError");
          if (SharedPreferenceHelper.getCachedPhFriends() != null) {
            Timber.e("loadPhFriends onError !=null " + SharedPreferenceHelper.getCachedPhFriends()
                .size());
            allFriendsToShow.addAll(SharedPreferenceHelper.getCachedPhFriends());
            getViewState().setFriendsPh(SharedPreferenceHelper.getCachedPhFriends());
          }
          throwable.printStackTrace();
        });
    addToUnsubscription(subscription);
  }

  private void loadVkFriends() {
    Timber.e("loadVkFriends");

    VKRequest request = new VKRequest("friends.get",
        VKParameters.from(VKApiConst.FIELDS, "photo_100", "order", "name"));
    request.executeWithListener(new VKRequest.VKRequestListener() {
      @Override public void onComplete(VKResponse response) {
        super.onComplete(response);
        JSONObject resp = response.json;
        Timber.e("loadVkFriends VK response" + response.responseString);
        List<String> vkToCheck = new ArrayList<String>();
        try {
          JSONArray items = resp.getJSONObject("response").getJSONArray("items");
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

            allFriendsToShow.add(friend);
            vkToCheck.add(friend.getId());
          }

          checkVkFriends(vkToCheck);
          //mCheckVkClient.checkVks(vkToCheck);

          Timber.e("loadVkFriends VK LOADED");
        } catch (JSONException e) {
          e.printStackTrace();
        }
      }

      @Override public void onError(VKError error) {
        super.onError(error);
        Timber.e("loadVkFriends vkError msg: "
            + error.errorMessage
            + " code:"
            + error.errorCode
            + " all:"
            + error.toString());
      }
    });
  }

  private void checkVkFriends(List<String> vkToCheck) {
    Timber.e("checkVkFriends " + vkToCheck.size());
    Subscription subscription = mDataManager.checkVks(vkToCheck)
        .compose(ThreadSchedulers.applySchedulers())
        .subscribe(checkVkResponseResponse -> {
          List<String> vks = new ArrayList<>();
          vks.addAll(checkVkResponseResponse.body().getVks());
          // Timber.e("SET INVITABLE IF 1 " + vks.size());
          for (String s : vks) {
            //Timber.e("SET INVITABLE IF 1.5 ");
            for (FacebookFriend friend : allFriendsToShow) {
              //   Timber.e("SET INVITABLE for 2 " + friend.getName());
              if (friend.getSocialNetwork().equals("vk")) {
                //     Timber.e("SET INVITABLE IF 3 " + friend.getName());
                if (friend.getId().equals(s)) {
                  //      Timber.e("SET INVITABLE IF 4 " + friend.getName());
                  friend.setIsInvitable(FacebookFriend.CODE_NOT_INVITABLE);
                }
              }
            }
          }
          Timber.e("checkVkFriends after checking" + allFriendsToShow.size());
          getViewState().setFriendsVk(allFriendsToShow);
        }, Throwable::printStackTrace);
    addToUnsubscription(subscription);
  }

  public void getFriendsInfo(List<String> recipients) {
    Timber.e("getFriendsInfo");
    this.recipients.clear();
    this.recipients.addAll(recipients);

    StringBuilder sb = new StringBuilder();
    for (String id : recipients) {
      sb.append(id);
      sb.append(",");
    }

    String ids = sb.toString();
    ids = ids.length() > 0 ? ids.substring(0, ids.length() - 1) : ids;

    Bundle parameters = new Bundle();
    parameters.putString(Constants.FB_FIELDS, Config.FB_FRIENDS_FIELDS);
    AccessToken accessToken = AccessToken.getCurrentAccessToken();
    String graphPath =
        String.format(Constants.FB_GET_FRIEND_INFO_GRAPH_PATH_TEMPLATE, accessToken.getUserId(),
            ids);

    GraphRequest info =
        new GraphRequest(accessToken, graphPath, parameters, HttpMethod.GET, response -> {
          String rawResponse = response.getRawResponse();
          Timber.e("converted (invited) friends: " + rawResponse);
          FacebookRequestError error = response.getError();

          List<FacebookFriend> parsedFriends = parseConvertedFriends(rawResponse);
          if (error == null) {
            getViewState().onGetFriendInfoSuccess(parsedFriends);
          } else {
            getViewState().onGetFriendInfoFailure(error.getErrorMessage());
          }
        });

    info.executeAsync();
  }

  public void createGroupSendDataToServer(List<FacebookFriend> toSendList) {
    getViewState().createGroupViaListener(toSendList);
  }

  public void editGroupSendDataToServer(List<FacebookFriend> membersToSendByEditing) {
    getViewState().editGroupViaListener(membersToSendByEditing);
  }

  public void inviteToShopSendDataToServer(List<FacebookFriend> membersToSendByInviteToShop) {
    getViewState().inviteToShopViaListener(membersToSendByInviteToShop);
  }

  private List<FacebookFriend> parseConvertedFriends(String rawResponse) {
    List<FacebookFriend> friends = new ArrayList<>();
    int countRecipients = recipients.size();

    JSONObject jsonResponse = null;
    try {
      jsonResponse = new JSONObject(rawResponse);
    } catch (JSONException e) {
      Timber.e("failure parse raw response of converted friends", e);
    }

    if (jsonResponse != null) {
      for (int i = 0; i < countRecipients; i++) {
        JSONObject jsonFriend = null;

        try {
          jsonFriend = jsonResponse.getJSONObject(recipients.get(i));
        } catch (JSONException e) {
          Timber.e("failure parse converted friends", e);
        }

        if (jsonFriend != null) {
          FacebookFriend friend = gson.fromJson(String.valueOf(jsonFriend), FacebookFriend.class);

          if (friend != null) {
            friend.setSocialNetwork("fb");
            friends.add(friend);
          }
        }
      }
    }

    return friends;
  }

  private FacebookFriend[] parseFriendsResponse(String rawResponse) {
    FacebookFriendResponse response = gson.fromJson(rawResponse, FacebookFriendResponse.class);
    FacebookFriend[] friends = new FacebookFriend[0];

    if (response != null && response.getData() != null) {
      expectedCountFriends =
          response.getSummary() != null ? response.getSummary().getTotalCount() : -1;

      friends = response.getData();
      if (friends.length == 0) {
        Timber.e("facebook friends list is empty");
      }
      for (FacebookFriend fbf : friends) {
        fbf.setIsInvitable(FacebookFriend.CODE_NOT_INVITABLE);
        fbf.setSocialNetwork("fb");
      }
      allFriendsToShow.addAll(Arrays.asList(friends));
      SharedPreferenceHelper.cacheFbFriendsNotInviteble(Arrays.asList(friends));
    } else {
      Timber.e("failure parse facebook friends response");
    }

    return friends;
  }

  private void parseInvitableFriendsResponse(String rawResponse) {
    FacebookInvitableResponse response =
        gson.fromJson(rawResponse, FacebookInvitableResponse.class);
    if (response != null && response.getData() != null) {
      FacebookFriend[] friends = response.getData();
      if (friends.length > 0) {
        for (FacebookFriend fbf : friends) {
          fbf.setIsInvitable(FacebookFriend.CODE_INVITABLE);
          fbf.setSocialNetwork("fb");
        }
        allFriendsToShow.addAll(Arrays.asList(friends));
        SharedPreferenceHelper.cacheFbFriendsInviteble(Arrays.asList(friends));
      } else {
        Timber.e("facebook invitable friends list is empty");
      }
    } else {
      Timber.e("failure parse facebook invitable friends response");
    }
  }

  public void filterBySocial(List<FacebookFriend> friendsFromAdapter, String socialTag) {
    getViewState().setFriends(allFriendsToShow);
    Subscription subscription = Observable.from(friendsFromAdapter)
        .filter(
            facebookFriend -> (facebookFriend.getSocialNetwork().equals(socialTag) || facebookFriend
                .isSelected()))
        .toList()
        .subscribe(facebookFriends -> {
          getViewState().setFriendsFilter(facebookFriends);
        }, Throwable::printStackTrace);
    addToUnsubscription(subscription);
  }

  public void filterByName(List<FacebookFriend> friendsFromAdapter, String friendName) {
    getViewState().setFriends(allFriendsToShow);
    Subscription subscription = Observable.from(friendsFromAdapter)
        .filter(facebookFriend -> (facebookFriend.getName()
            .toLowerCase()
            .contains(friendName.toLowerCase()) || facebookFriend.isSelected()))
        .toList()
        .subscribe(facebookFriends -> {
          getViewState().setFriendsFilter(facebookFriends);
        }, Throwable::printStackTrace);
    addToUnsubscription(subscription);
  }

  public List<FacebookFriend> checkForUniqueMembers(List<Member> pendingsMembers,
      List<Member> addedMembers, List<FacebookFriend> friends) {
    Timber.e("checkForUnique pendingsMembers: "
        + pendingsMembers.size()
        + " addedMembers:"
        + addedMembers.size());
    for (int i = 0; i < friends.size(); i++) {
      for (Member member : addedMembers) {
        if (member.getPhoneNumber() == null) member.setPhoneNumber("");
        if (member.getFbid() == null) member.setFbid("");
        if (member.getVkId() == null) member.setVkId("");
        if (TextUtils.equals(member.getName(), friends.get(i).getName())
            || member.getFbid()
            .equals(friends.get(i).getId())
            || member.getVkId().equals(friends.get(i).getId())
            || member.getPhoneNumber().equals(friends.get(i).getId())) {
          Timber.e("checkForUniqueMembers 1");

          friends.get(i).setAdded(true);
        }
      }

      for (Member member : pendingsMembers) {
        if (member.getPhoneNumber() == null) member.setPhoneNumber("");
        if (member.getFbid() == null) member.setFbid("");
        if (member.getVkId() == null) member.setVkId("");
        //if (TextUtils.equals(member.getName(), friends.get(i).getName())) {
        if (TextUtils.equals(member.getName(), friends.get(i).getName())
            || member.getFbid()
            .equals(friends.get(i).getId())
            || member.getVkId().equals(friends.get(i).getId())
            || member.getPhoneNumber().equals(friends.get(i).getId())) {
          Timber.e("checkForUniqueMembers 2");

          friends.get(i).setPending(true);
        }
      }
      Timber.e(String.valueOf(friends.get(i).isAdded()));
    }
    return friends;
  }

  public void saveFBCounters(List<FacebookFriend> friends) {
    SharedPreferenceHelper.saveCountOfMembersFb(String.valueOf(friends.size()));
  }

  //public void saveFBMembers(List<FacebookFriend> friends) {
  //  List<FacebookFriend> tempCollection = new ArrayList<>();
  //  for (FacebookFriend f : friends) {
  //    if (f.getSocialNetwork().equals("fb")) {
  //      tempCollection.add(f);
  //    }
  //  }
  //  SharedPreferenceHelper.cacheFbFriends(tempCollection);
  //}

  public void savePhCounters(List<FacebookFriend> friends) {
    SharedPreferenceHelper.saveCountOfMembersPh(String.valueOf(friends.size()));
  }

  public void savePhMembers(List<FacebookFriend> friends) {
    List<FacebookFriend> tempCollection = new ArrayList<>();
    for (FacebookFriend f : friends) {
      if (f.getSocialNetwork().equals("ph")) {
        tempCollection.add(f);
      }
    }
    SharedPreferenceHelper.cachePhFriends(tempCollection);
  }
}
