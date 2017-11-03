package com.sugarman.myb.ui.activities.groupDetails;

import com.arellomobile.mvp.InjectViewState;
import com.sugarman.myb.App;
import com.sugarman.myb.base.BasicPresenter;
import com.sugarman.myb.utils.ThreadSchedulers;
import rx.Subscription;

/**
 * Created by nikita on 03.11.2017.
 */
@InjectViewState public class GroupDetailsActivityPresenter
    extends BasicPresenter<IGroupDetailsActivityView> {
  @Override protected void inject() {
    App.getAppComponent().inject(this);
  }

  public void sendComment(String mentorsId, float rating, String commentBody) {
    Subscription subscription =
        mDataManager.sendComment(mentorsId, String.valueOf(rating), commentBody)
            .compose(ThreadSchedulers.applySchedulers())
            .subscribe(voidResponse -> {
              if (voidResponse.code() == 200){
                getViewState().closeDialog();
              }
            }, Throwable::printStackTrace);
    addToUnsubscription(subscription);
  }
}
