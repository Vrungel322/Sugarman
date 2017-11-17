package com.sugarman.myb.ui.activities.mentorList;

import com.arellomobile.mvp.InjectViewState;
import com.sugarman.myb.App;
import com.sugarman.myb.base.BasicPresenter;
import com.sugarman.myb.utils.ThreadSchedulers;
import rx.Subscription;

/**
 * Created by yegoryeriomin on 10/27/17.
 */
@InjectViewState public class MentorListActivityPresenter
    extends BasicPresenter<IMentorListActivityView> {
  @Override protected void inject() {
    App.getAppComponent().inject(this);
  }

  @Override protected void onFirstViewAttach() {
    super.onFirstViewAttach();
    getViewState().setUpUI();
    fetchMentors();
  }

  private void fetchMentors() {
    Subscription subscription = mDataManager.fetchMentors()
        .compose(ThreadSchedulers.applySchedulers())
        .subscribe(mentorStupidAbstraction -> {
          getViewState().fillMentorsList(mentorStupidAbstraction.getMentorEntities());
        }, Throwable::printStackTrace);
    addToUnsubscription(subscription);
  }
}
