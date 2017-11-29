package com.sugarman.myb.ui.activities.mainScreeen;

import com.arellomobile.mvp.InjectViewState;
import com.sugarman.myb.App;
import com.sugarman.myb.base.BasicPresenter;
import com.sugarman.myb.constants.Constants;
import com.sugarman.myb.models.ContactListForServer;
import com.sugarman.myb.models.custom_events.CustomUserEvent;
import com.sugarman.myb.models.custom_events.Rule;
import com.sugarman.myb.utils.SharedPreferenceHelper;
import com.sugarman.myb.utils.ThreadSchedulers;
import java.util.List;
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
    Subscription subscription =
        mDataManager.fetchRules().compose(ThreadSchedulers.applySchedulers()).subscribe(ruleSet -> {
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

  public void checkIfRuleStepsDone(int todaySteps) {
    List<Rule> rules = mDataManager.getRuleByName(Constants.EVENT_X_STEPS_DONE);
    if (!rules.isEmpty()) {
      Rule rule = rules.get(0);

      Timber.e(
          "rule " + rule.getName() + " todaySteps " + todaySteps + " getCount()" + rule.getCount());
      if (rule.getCount() <= todaySteps){
        Timber.e("rule true");
        getViewState().doEventActionResponse(CustomUserEvent.builder()
            .strType(rule.getAction())
            .eventText(rule.getMessage())
            .build());
      }
    }
  }
}
