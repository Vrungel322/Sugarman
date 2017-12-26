package com.sugarman.myb.ui.activities.base;

import com.arellomobile.mvp.InjectViewState;
import com.sugarman.myb.App;
import com.sugarman.myb.api.models.requests.RefreshUserDataRequest;
import com.sugarman.myb.base.BasicPresenter;
import com.sugarman.myb.data.DataManager;
import com.sugarman.myb.utils.SharedPreferenceHelper;
import com.sugarman.myb.utils.ThreadSchedulers;
import java.util.TimeZone;
import javax.inject.Inject;
import rx.Subscription;
import timber.log.Timber;

/**
 * Created by nikita on 19.09.17.
 */
@InjectViewState public class BasicActivityPresenter extends BasicPresenter<IBaseActivityView> {
  @Inject DataManager mDataManager;

  @Override protected void inject() {
    App.getAppComponent().inject(this);
  }

  public void refreshToken(String fbAccessToken, String vkToken, String phoneNumber) {
    RefreshUserDataRequest request = new RefreshUserDataRequest();
    request.setToken(fbAccessToken);
    request.setVkToken(vkToken);
    request.setgToken("none");
    request.setPhoneToken(phoneNumber);
    request.setPhoneNumber(phoneNumber);
    request.setEmail("none@mail.com");
    request.setName("none");
    request.setVkId("none");
    request.setFbId("none");
    request.setPictureUrl("none");
    request.setCampaign(SharedPreferenceHelper.getCampaignParam("utm_source"));
    request.setTimezone(TimeZone.getDefault().getID());
    Subscription subscriptions = mDataManager.refreshRxUserData(request)
        .compose(ThreadSchedulers.applySchedulers())
        .subscribe(usersResponseCall -> {
          Timber.e(usersResponseCall.getBaseUrl());
          SharedPreferenceHelper.saveBaseUrl(usersResponseCall.getBaseUrl());

          SharedPreferenceHelper.saveToken(usersResponseCall.getTokens());
          getViewState().startSplashActivity();
        }, Throwable::printStackTrace);
    addToUnsubscription(subscriptions);
  }

  public void clearRuleDailyData() {
    mDataManager.clearRuleDailyData();
  }
}