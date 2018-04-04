package com.sugarman.myb.api.clients;

import android.os.Bundle;
import android.util.Log;
import com.facebook.AccessToken;
import com.facebook.FacebookRequestError;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.google.gson.Gson;
import com.sugarman.myb.api.models.responses.facebook.FacebookFriend;
import com.sugarman.myb.api.models.responses.facebook.FacebookFriendResponse;
import com.sugarman.myb.api.models.responses.facebook.FacebookInvitableResponse;
import com.sugarman.myb.constants.Config;
import com.sugarman.myb.constants.Constants;
import com.sugarman.myb.listeners.OnFBGetFriendsListener;
import com.sugarman.myb.utils.SharedPreferenceHelper;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import org.json.JSONException;
import org.json.JSONObject;
import timber.log.Timber;

public class FBApiClient {

  private static final String TAG = FBApiClient.class.getName();

  private final Gson gson;
  private final List<FacebookFriend> friends = new ArrayList<>();
  private final List<FacebookFriend> invaliable = new ArrayList<>();
  private final List<String> recipients = new ArrayList<>();
  private FacebookRequestError friendsError;
  private WeakReference<OnFBGetFriendsListener> clientListener = new WeakReference<>(null);
  private final GraphRequest.Callback infoCallback = new GraphRequest.Callback() {

    @Override public void onCompleted(GraphResponse response) {
      String rawResponse = response.getRawResponse();
      Log.d(TAG, "converted (invited) friends: " + rawResponse);
      FacebookRequestError error = response.getError();

      List<FacebookFriend> parsedFriends = parseConvertedFriends(rawResponse);
      if (clientListener.get() != null) {
        if (error == null) {
          clientListener.get().onGetFriendInfoSuccess(parsedFriends);
        } else {
          clientListener.get().onGetFriendInfoFailure(error.getErrorMessage());
        }
      }
    }
  };
  private int expectedCountFriends;
  private final GraphRequest.Callback invitableFriendsCallback = new GraphRequest.Callback() {
    @Override public void onCompleted(GraphResponse response) {
      String rawResponse = response.getRawResponse();
      Log.d(TAG, "invitable friends: " + rawResponse);

      FacebookRequestError invitableError = response.getError();

      parseInvitableFriendsResponse(rawResponse);

      if (clientListener.get() != null) {
        if (friends.isEmpty() && invaliable.isEmpty()) {
          if (friendsError != null || invitableError != null) {
            String message = friendsError == null ? ""
                : friendsError.getErrorMessage() + " " + invitableError.getErrorMessage();
            clientListener.get().onGetFacebookFriendsFailure(message);
          } else {
            logFriendsCount();
            clientListener.get().onGetFacebookFriendsSuccess(friends, invaliable);
          }
        } else {
          logFriendsCount();
          clientListener.get().onGetFacebookFriendsSuccess(friends, invaliable);
        }
      }
    }
  };
  private final GraphRequest.Callback friendsCallback = new GraphRequest.Callback() {
    @Override public void onCompleted(GraphResponse response) {
      String rawResponse = response.getRawResponse();
      Log.d(TAG, "friends: " + rawResponse);

      friendsError = response.getError();

      FacebookFriend[] parsedFriends = parseFriendsResponse(rawResponse);
      boolean isInvitable = parsedFriends.length != expectedCountFriends;
      friends.addAll(Arrays.asList(parsedFriends));

      if (isInvitable) {
        // necessary search invitable friends
        getInvitableFriends();
      } else {
        // all friends are exists
        if (clientListener.get() != null) {
          if (friends.isEmpty()) {
            if (friendsError != null) {
              clientListener.get().onGetFacebookFriendsFailure(friendsError.getErrorMessage());
            } else {
              clientListener.get()
                  .onGetFacebookFriendsSuccess(friends, new ArrayList<FacebookFriend>(0));
            }
          } else {
            clientListener.get()
                .onGetFacebookFriendsSuccess(friends, new ArrayList<FacebookFriend>(0));
          }
        }
      }
    }
  };

  public FBApiClient() {
    gson = new Gson();
  }

  public void registerListener(OnFBGetFriendsListener listener) {
    clientListener = new WeakReference<>(listener);
  }

  public void unregisterListener() {
    clientListener.clear();
  }

  public void searchFriends() {
    canLoadFriends();
  }

  private void canLoadFriends() {
    AccessToken accessToken = AccessToken.getCurrentAccessToken();

    if (accessToken != null) {
      String graphPathCountFriends = "me/friends";

      Bundle parameters = new Bundle();
      parameters.putString(Constants.FB_FIELDS, Config.FB_FRIENDS_FIELDS);

      new GraphRequest(accessToken, graphPathCountFriends, parameters, HttpMethod.GET, response -> {
        if (response != null && response.getJSONObject() != null) {
          JSONObject jsonResponse = response.getJSONObject();
          Integer countOfFbFriends = 11;
          try {
            Timber.e(
                "canLoadFriends " + jsonResponse.getJSONObject("summary").getString("total_count"));
            countOfFbFriends =
                Integer.parseInt(jsonResponse.getJSONObject("summary").getString("total_count"));
          } catch (JSONException e) {
            e.printStackTrace();
          }
          Timber.e(
              "canLoadFriends SHP: " + SharedPreferenceHelper.getCountOfMembersFb() + " bool " + (
                  SharedPreferenceHelper.getCountOfMembersFb()
                      != countOfFbFriends));
          if (SharedPreferenceHelper.getCountOfMembersFb() != countOfFbFriends) {
            getFriends();
          }
          SharedPreferenceHelper.saveCountOfMembersFb(String.valueOf(countOfFbFriends));
        }
      }).executeAsync();
    }
  }

  // ids: "id1,id2,id3,..."
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
        new GraphRequest(accessToken, graphPath, parameters, HttpMethod.GET, infoCallback);

    info.executeAsync();
  }

  // friends without this is application
  private void getFriends() {
    Bundle parameters = new Bundle();
    parameters.putString(Constants.FB_FIELDS, Config.FB_FRIENDS_FIELDS);
    AccessToken accessToken = AccessToken.getCurrentAccessToken();

    if (accessToken != null) {
      String graphPath = String.format(Locale.US, Constants.FB_FRIENDS_GRAPH_PATH_TEMPLATE,
          accessToken.getUserId(), Config.FB_FRIENDS_LIMIT);

      GraphRequest friends =
          new GraphRequest(accessToken, graphPath, parameters, HttpMethod.GET, friendsCallback);

      friends.executeAsync();
    }
  }

  // friends with this is application
  private void getInvitableFriends() {
    Bundle parameters = new Bundle();
    parameters.putString(Constants.FB_FIELDS, Config.FB_FRIENDS_FIELDS);
    AccessToken accessToken = AccessToken.getCurrentAccessToken();
    String graphPath = String.format(Locale.US, Constants.FB_INVITABLE_FRIENDS_GRAPH_PATH_TEMPLATE,
        accessToken.getUserId(), Config.FB_FRIENDS_LIMIT);

    GraphRequest invitableFriends =
        new GraphRequest(AccessToken.getCurrentAccessToken(), graphPath, parameters, HttpMethod.GET,
            invitableFriendsCallback);

    invitableFriends.executeAsync();
  }

  private FacebookFriend[] parseFriendsResponse(String rawResponse) {
    FacebookFriendResponse response = gson.fromJson(rawResponse, FacebookFriendResponse.class);
    FacebookFriend[] friends = new FacebookFriend[0];

    if (response != null && response.getData() != null) {
      expectedCountFriends =
          response.getSummary() != null ? response.getSummary().getTotalCount() : -1;

      friends = response.getData();
      if (friends.length == 0) {
        Log.d(TAG, "facebook friends list is empty");
      }
    } else {
      Log.e(TAG, "failure parse facebook friends response");
    }

    return friends;
  }

  private void parseInvitableFriendsResponse(String rawResponse) {
    FacebookInvitableResponse response =
        gson.fromJson(rawResponse, FacebookInvitableResponse.class);
    if (response != null && response.getData() != null) {
      FacebookFriend[] friends = response.getData();
      if (friends.length > 0) {
        invaliable.addAll(Arrays.asList(friends));
      } else {
        Log.d(TAG, "facebook invitable friends list is empty");
      }
    } else {
      Log.e(TAG, "failure parse facebook invitable friends response");
    }
  }

  private void logFriendsCount() {
    int actual = friends.size() + invaliable.size();
    if (actual != expectedCountFriends) {
      Log.d(TAG,
          "problems with get fb friends expected: " + expectedCountFriends + " actual: " + actual);
    }
  }

  private List<FacebookFriend> parseConvertedFriends(String rawResponse) {
    List<FacebookFriend> friends = new ArrayList<>();
    int countRecipients = recipients.size();

    JSONObject jsonResponse = null;
    try {
      jsonResponse = new JSONObject(rawResponse);
    } catch (JSONException e) {
      Log.e(TAG, "failure parse raw response of converted friends", e);
    }

    if (jsonResponse != null) {
      for (int i = 0; i < countRecipients; i++) {
        JSONObject jsonFriend = null;

        try {
          jsonFriend = jsonResponse.getJSONObject(recipients.get(i));
        } catch (JSONException e) {
          Log.e(TAG, "failure parse converted friends", e);
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
}
