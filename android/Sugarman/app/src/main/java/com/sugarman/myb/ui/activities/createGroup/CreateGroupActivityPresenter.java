package com.sugarman.myb.ui.activities.createGroup;

import android.util.Log;
import com.arellomobile.mvp.InjectViewState;
import com.sugarman.myb.App;
import com.sugarman.myb.api.models.responses.facebook.FacebookFriend;
import com.sugarman.myb.base.BasicPresenter;
import com.sugarman.myb.constants.Constants;
import com.sugarman.myb.data.DataManager;
import com.sugarman.myb.models.custom_events.CustomUserEvent;
import com.sugarman.myb.models.custom_events.Rule;
import com.sugarman.myb.utils.ThreadSchedulers;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import java.util.List;
import javax.inject.Inject;
import org.json.JSONObject;
import rx.Subscription;
import timber.log.Timber;

/**
 * Created by nikita on 02.10.2017.
 */

@InjectViewState public class CreateGroupActivityPresenter
    extends BasicPresenter<ICreateGroupActivityView> {
  @Inject DataManager mDataManager;

  @Override protected void inject() {
    App.getAppComponent().inject(this);
  }

  @Override protected void onFirstViewAttach() {
    super.onFirstViewAttach();
  }

  public void fillListByCachedData() {
    getViewState().showProgress();
    Subscription subscription = mDataManager.getCachedFriends()
        .compose(ThreadSchedulers.applySchedulers())
        .subscribe(facebookFriends -> {
          getViewState().fillListByCachedData(facebookFriends);
          getViewState().hideProgress();
        }, Throwable::printStackTrace);
    addToUnsubscription(subscription);
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

  public void cacheFriends(List<FacebookFriend> friends) {
    mDataManager.cacheFriends(friends);
  }

  public void checkRuleXNewUsersInvite(List<FacebookFriend> members) {
    Timber.e("checkRuleXNewUsersInvite " + members.size());
    int newUsersCount = 0;
    for (int i = 0; i < members.size(); i++) {
      if (members.get(i).getIsInvitable() != FacebookFriend.CODE_INVITABLE) {
        newUsersCount++;
      }
    }
    Timber.e("newUsersCount " + newUsersCount);
    List<Rule> rules = mDataManager.getRuleByName(Constants.EVENT_X_NEW_USERS_INVITE);
    if (!rules.isEmpty()) {
      Timber.e("checkRuleXNewUsersInvite " + !rules.isEmpty());
      Rule rule = rules.get(0);
      if (rule.getCount() <= newUsersCount) {
        Timber.e("rule true");
        //getViewState().doEventActionResponse(CustomUserEvent.builder()
        //    .strType(rule.getAction())
        //    .eventText(rule.getMessage())
        //    .build());
        mRxBus.post(CustomUserEvent.builder()
            .strType(rule.getAction())
            .eventText(rule.getMessage())
            .eventName(rule.getName())
            .build());
      }
    }
  }
}
