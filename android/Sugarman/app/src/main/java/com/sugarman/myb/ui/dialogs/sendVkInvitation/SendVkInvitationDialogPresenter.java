package com.sugarman.myb.ui.dialogs.sendVkInvitation;

import com.arellomobile.mvp.InjectViewState;
import com.sugarman.myb.App;
import com.sugarman.myb.api.models.responses.facebook.FacebookFriend;
import com.sugarman.myb.base.BasicPresenter;
import com.sugarman.myb.utils.RxBus;
import com.sugarman.myb.utils.RxBusHelper;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import java.util.ArrayList;
import javax.inject.Inject;
import org.json.JSONObject;
import timber.log.Timber;

/**
 * Created by nikita on 29.09.2017.
 */
@InjectViewState public class SendVkInvitationDialogPresenter
    extends BasicPresenter<ISendVkInvitationDialogView> {
  @Inject RxBus mRxBus;

  @Override protected void inject() {
    App.getAppComponent().inject(this);
  }

  public void sendInvitations(ArrayList<FacebookFriend> friends, String invitationMsg) {
    Timber.e("sendInvitations " + friends.get(0).getSocialNetwork());
    mRxBus.post(new RxBusHelper.AddMemberVkEvent(friends));

    for (FacebookFriend friend : friends) {
      if (friend.getSocialNetwork().equals("vk")) {
        VKRequest request = new VKRequest("messages.send",
            VKParameters.from(VKApiConst.USER_ID, Integer.parseInt(friend.getId()),
                VKApiConst.MESSAGE, invitationMsg));
        request.executeWithListener(new VKRequest.VKRequestListener() {
          @Override public void onComplete(VKResponse response) {
            super.onComplete(response);
            JSONObject resp = response.json;
            getViewState().doAction();
            Timber.e("sendInvitations VK response " + response.responseString);
          }

          @Override public void onError(VKError error) {
            super.onError(error);
            Timber.e("sendInvitations VK response " + error.errorCode + error.toString());
          }
        });
      }
    }
  }
}