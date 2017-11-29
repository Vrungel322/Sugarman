package com.sugarman.myb.ui.activities.mainScreeen;

import com.arellomobile.mvp.InjectViewState;
import com.sugarman.myb.App;
import com.sugarman.myb.base.BasicPresenter;
import com.sugarman.myb.models.ContactListForServer;
import com.sugarman.myb.utils.SharedPreferenceHelper;
import com.sugarman.myb.utils.ThreadSchedulers;
import rx.Subscription;
import timber.log.Timber;

/**
 * Created by nikita on 06.10.2017.
 */
@InjectViewState public class MainActivityPresenter extends BasicPresenter<IMainActivityView> {
  @Override protected void inject() {
    App.getAppComponent().inject(this);
  }

  @Override protected void onFirstViewAttach() {
    super.onFirstViewAttach();
    fetchTasks();
    fetchCompletedTasks();
    fetchRules();
  }

  private void fetchRules() {
    Subscription subscription = mDataManager.fetchRules()
        .compose(ThreadSchedulers.applySchedulers())
        .subscribe(ruleSet -> {
          Timber.e("!!!"+ruleSet.body().getRules().get(0).getName());
          mDataManager.saveRules(ruleSet.body());
        }, Throwable::printStackTrace);
    addToUnsubscription(subscription);
  }

  private void fetchTasks() {
    Subscription subscription = mDataManager.fetchTasks()
        .compose(ThreadSchedulers.applySchedulers())
        .subscribe(taskEntities -> {
          Timber.e("All tasks " + String.valueOf(taskEntities.getTasks().size()));
        }, Throwable::printStackTrace);
    addToUnsubscription(subscription);
  }

  private void fetchCompletedTasks() {
    Subscription subscription = mDataManager.fetchCompletedTasks()
        .compose(ThreadSchedulers.applySchedulers())
        .subscribe(ids -> {
          Timber.e("Completed tasks " + String.valueOf(ids.size()));
        }, Throwable::printStackTrace);
    addToUnsubscription(subscription);
  }

  public void sendContacts(ContactListForServer contactForServer) {
    Subscription subscription = mDataManager.sendContacts(contactForServer)
        .compose(ThreadSchedulers.applySchedulers())
        .subscribe(voidResponse -> {
          Timber.e("Success");
          SharedPreferenceHelper.setContactsSent(true);
        }, Throwable::printStackTrace);
    addToUnsubscription(subscription);
  }
}
