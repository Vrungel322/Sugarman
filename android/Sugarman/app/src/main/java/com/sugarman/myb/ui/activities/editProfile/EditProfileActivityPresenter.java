package com.sugarman.myb.ui.activities.editProfile;

import com.arellomobile.mvp.InjectViewState;
import com.sugarman.myb.App;
import com.sugarman.myb.base.BasicPresenter;
import com.sugarman.myb.utils.ThreadSchedulers;
import java.io.File;
import rx.Subscription;

/**
 * Created by nikita on 19.10.2017.
 */
@InjectViewState public class EditProfileActivityPresenter
    extends BasicPresenter<IEditProfileActivityView> {
  @Override protected void inject() {
    App.getAppComponent().inject(this);
  }

  public void sendUserDataToServer(String phone, String email, String name, String fbId,
      String vkId, String avatar, File selectedFile) {
    Subscription subscription =
        mDataManager.sendUserDataToServer(phone, email, name, fbId, vkId, avatar, selectedFile)
            .compose(ThreadSchedulers.applySchedulers())
            .subscribe(usersResponse -> {
              getViewState().finishActivity();
            });
    addToUnsubscription(subscription);
  }
}
