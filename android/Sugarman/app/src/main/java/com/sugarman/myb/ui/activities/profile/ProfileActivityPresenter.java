package com.sugarman.myb.ui.activities.profile;

import android.util.Log;
import com.arellomobile.mvp.InjectViewState;
import com.sugarman.myb.App;
import com.sugarman.myb.api.models.responses.facebook.FacebookFriend;
import com.sugarman.myb.base.BasicPresenter;
import com.sugarman.myb.ui.activities.createGroup.ICreateGroupActivityView;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import java.util.List;
import org.json.JSONObject;

/**
 * Created by nikita on 02.10.2017.
 */

@InjectViewState public class ProfileActivityPresenter
    extends BasicPresenter<IProfileActivityView> {
  @Override protected void inject() {
    App.getAppComponent().inject(this);
  }

  public void startAnimation()
  {

  }

}
