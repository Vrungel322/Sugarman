package com.sugarman.myb.ui.activities.addMember;

import android.util.Log;
import com.arellomobile.mvp.InjectViewState;
import com.sugarman.myb.App;
import com.sugarman.myb.api.models.responses.facebook.FacebookFriend;
import com.sugarman.myb.base.BasicPresenter;
import com.sugarman.myb.utils.RxBus;
import com.sugarman.myb.utils.RxBusHelper;
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

/**
 * Created by nikita on 26.09.17.
 */
@InjectViewState public class AddMemberActivityPresenter
    extends BasicPresenter<IAddMemberActivityView> {
  @Inject RxBus mRxBus;

  @Override protected void inject() {
    App.getAppComponent().inject(this);
  }

  @Override protected void onFirstViewAttach() {
    super.onFirstViewAttach();
    subscribeAddVkFriendsEvent();
  }

  private void subscribeAddVkFriendsEvent() {
    Subscription subscription = mRxBus.filteredObservable(RxBusHelper.AddMemberVkEvent.class)
        .compose(ThreadSchedulers.applySchedulers())
        .subscribe(addMemberVkEvent -> getViewState().addMemberToServer(addMemberVkEvent.mFacebookFriends));
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
            getViewState().finishActivity();
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
