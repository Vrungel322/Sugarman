package com.sugarman.myb.ui.activities.mainScreeen;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import com.arellomobile.mvp.InjectViewState;
import com.sugarman.myb.App;
import com.sugarman.myb.api.models.responses.Tracking;
import com.sugarman.myb.base.BasicActivity;
import com.sugarman.myb.base.BasicPresenter;
import com.sugarman.myb.constants.Constants;
import com.sugarman.myb.models.ContactListForServer;
import com.sugarman.myb.models.animation.ImageModel;
import com.sugarman.myb.models.custom_events.CustomUserEvent;
import com.sugarman.myb.models.custom_events.Rule;
import com.sugarman.myb.models.iab.InAppSinglePurchase;
import com.sugarman.myb.utils.RxBusHelper;
import com.sugarman.myb.utils.SharedPreferenceHelper;
import com.sugarman.myb.utils.ThreadSchedulers;
import com.sugarman.myb.utils.animation.AnimationHelper;
import com.sugarman.myb.utils.inapp.Purchase;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

/**
 * Created by nikita on 06.10.2017.
 */
@InjectViewState public class MainActivityPresenter extends BasicPresenter<IMainActivityView> {
  int duration = 30;
  private Subscription mPeriodicalSubscription;

  @Override protected void inject() {
    App.getAppComponent().inject(this);
  }

  @Override protected void onFirstViewAttach() {
    super.onFirstViewAttach();
    fetchTasks();
    fetchCompletedTasks();
    fetchRules();
    subscribeShowDialogEvent();
  }

  public void startFetchingTrackingsPeriodically() {
    //interval by default subscribeOn in Computation thread
    mPeriodicalSubscription = Observable.interval(1000, 10000, TimeUnit.MILLISECONDS)
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(aLong -> {
          Timber.e("startFetchingTrackingsPeriodically");
          getViewState().refreshTrackings();
        }, Throwable::printStackTrace);
  }

  public void stopPeriodicalFetchingTracking() {
    mPeriodicalSubscription.unsubscribe();
  }

  //private void startFetchingTrackingsPeriodicallyCurrentTraking(Tracking tracking) {
  //  //interval by default subscribeOn in Computation thread
  //  Subscription subscription = Observable.interval(1000, 10000, TimeUnit.MILLISECONDS)
  //      .observeOn(AndroidSchedulers.mainThread())
  //      .subscribe(aLong -> {
  //        Timber.e("startFetchingTrackingsPeriodically");
  //        //getViewState().refreshCurrentTracking(tracking);
  //        refreshCurrentTracking(tracking);
  //      }, Throwable::printStackTrace);
  //  addToUnsubscription(subscription);
  //}

  public void refreshCurrentTracking(Tracking tracking) {
    if (tracking != null) {
      Subscription subscription = mDataManager.fetchCurrentTracking(tracking.getId())
          .compose(ThreadSchedulers.applySchedulers())
          .subscribe(trackingInfoResponseResponse -> {
            Timber.e(
                "updateCurTra refreshCurrentTracking code:" + trackingInfoResponseResponse.code());

            getViewState().updateCurrentTracking(trackingInfoResponseResponse.body());
          }, Throwable::printStackTrace);
      addToUnsubscription(subscription);
    }
  }

  private void subscribeShowDialogEvent() {
    Subscription subscription = mRxBus.filteredObservable(CustomUserEvent.class)
        .compose(ThreadSchedulers.applySchedulers())
        .subscribe(customUserEvent -> {
          if (!SharedPreferenceHelper.isEventGroupWithXNewUsersDone()) {
            getViewState().doEventActionResponse(customUserEvent);
          }
        }, Throwable::printStackTrace);
    addToUnsubscription(subscription);
  }

  private void fetchRules() {
    Subscription subscription =
        mDataManager.fetchRules().compose(ThreadSchedulers.applySchedulers()).subscribe(ruleSet -> {
          if (ruleSet.code() == 200) {
            Timber.e("ruleSet 200");
            mDataManager.saveRules(ruleSet.body());
          }
        }, Throwable::printStackTrace);
    addToUnsubscription(subscription);
  }

  public void checkIfRuleStepsDone(int todaySteps, int groupsCount) {
    List<Rule> rules = new ArrayList<>();
    List<Rule> rulesTempo = mDataManager.getRuleByName(Constants.EVENT_X_STEPS_DONE);
    for (Rule r : rulesTempo) {
      if (r.getCount() <= todaySteps) {
        rules.add(r);
      }
    }
    if (rules != null && !rules.isEmpty()) {
      //Rule rule = Collections.max(rules, (a, b) -> {
      //  return a.getCount().compareTo(b.getCount());
      //});

      // выбор того правила у которого значение шагов ближе всего к текущему количеству шагов
      // TODO: 07.12.2017 простестить с 1 рулом на количество шагов
      Rule rule = getRuleApproximatelyToCurrentSteps(todaySteps, groupsCount, rules);
      if (rule.getCount() != null) {

        Timber.e(" checkIfRuleStepsDone rule "
            + rule.getName()
            + " animation name "
            + rule.getNameOfAnim()
            + " todaySteps "
            + todaySteps
            + " getCount()"
            + rule.getCount()
            + " RuleGroupsCount:"
            + rule.getGroupCount());
        if (todaySteps >= rule.getCount()) {
          Timber.e("checkIfRuleStepsDone groupsCount:" + groupsCount);

          if ((rule.getGroupCount() == 0 && groupsCount == 0) || (rule.getGroupCount() > 0
              && groupsCount > 0)) {
            Timber.e(
                "checkIfRuleStepsDone rule true &&" + !SharedPreferenceHelper.isEventXStepsDone(
                    rule.getCount()));
            if (!SharedPreferenceHelper.isEventXStepsDone(rule.getCount())) {
              Timber.e("checkIfRuleStepsDone rule true and SHP OK");

              Timber.e("checkIfRuleStepsDone setAnimation bool: "
                  + (!SharedPreferenceHelper.getNameOfCurrentAnim().equals(rule.getNameOfAnim()))
                  + " storedname:"
                  + SharedPreferenceHelper.getNameOfCurrentAnim()
                  + " animName:"
                  + rule.getNameOfAnim()
                  + "  isCanLaunchLastAnim: "
                  + SharedPreferenceHelper.isCanLaunchLastAnim());

              if ((rule.getAction().equals(BasicActivity.ANIMATION_ACTION)
                  && !SharedPreferenceHelper.getNameOfCurrentAnim().equals(rule.getNameOfAnim()))
                  || rule.getAction().equals(BasicActivity.POPUP_ACTION)) {
                getViewState().doEventActionResponse(CustomUserEvent.builder()
                    .strType(rule.getAction())
                    .eventText(rule.getMessage())
                    .eventName(rule.getName())
                    .nameOfAnim(rule.getNameOfAnim())
                    .numValue(rule.getCount())
                    .groupCount(rule.getGroupCount())
                    .strValue(rule.getPopUpImg())
                    //.strValue("http://fs.kinomania.ru/file/person/1/95/195688ae35f80d6aca00e2fb5cd80b90.jpeg")
                    .build());
              } else {
                if (SharedPreferenceHelper.isCanLaunchLastAnim()) {
                  launchLastAnim(rulesTempo, todaySteps);
                }
              }
            } else {
              if (SharedPreferenceHelper.isCanLaunchLastAnim()) {
                launchLastAnim(rulesTempo, todaySteps);
              }
            }
          }
        }
      }
    }
  }

  private void launchLastAnim(List<Rule> rulesTempo, int todaySteps) {
    Rule rule = new Rule();
    int min = Integer.MAX_VALUE;
    Collections.sort(rulesTempo, (rule1, t1) -> t1.getGroupCount() - rule1.getGroupCount());
    for (Rule r : rulesTempo) {
      final int diff = Math.abs(r.getCount() - todaySteps);

      if (diff < min && r.getNameOfAnim() != null) {
        min = diff;
        rule = r;
      }
    }
    SharedPreferenceHelper.canLaunchLastAnim(false);

    Timber.e(" launchLastAnim rule "
        + rule.getName()
        + " animation name "
        + rule.getNameOfAnim()
        + " todaySteps "
        + todaySteps
        + " getCount()"
        + rule.getCount()
        + " RuleGroupsCount:"
        + rule.getGroupCount());
    getViewState().doEventActionResponse(CustomUserEvent.builder()
        .strType(rule.getAction())
        .eventText(rule.getMessage())
        .eventName(rule.getName())
        .nameOfAnim(rule.getNameOfAnim())
        .numValue(rule.getCount())
        .build());
  }

  private Rule getRuleApproximatelyToCurrentSteps(int todaySteps, int groupsCount,
      List<Rule> rules) {
    Rule rule = new Rule();
    int min = Integer.MAX_VALUE;
    for (Rule currentRule : rules) {
      final int diff = Math.abs(currentRule.getCount() - todaySteps);

      if ((groupsCount == 0 && currentRule.getGroupCount() == 0) || (groupsCount > 0
          && currentRule.getGroupCount() > 0)) {
        if (diff < min) {
          min = diff;
          rule = currentRule;
        }
      } else {
        continue;
      }
    }
    Timber.e("getRuleApproximatelyToCurrentSteps " + rule.toString());
    return rule;
  }

  public void checkIfRule15KStepsDone(int todaySteps) {
    //List<Rule> rules = mDataManager.getRuleByName(Constants.EVENT_15K_STEPS_DONE);
    //if (!rules.isEmpty()) {
    //  Rule rule = rules.get(0);
    //
    //  Timber.e(
    //      "rule " + rule.getName() + " todaySteps " + todaySteps + " getCount()" + rule.getCount());
    //  if (todaySteps >= rule.getCount()) {
    //    Timber.e("rule 15K true");
    //    //if (!SharedPreferenceHelper.isEventXStepsDone()) {
    //    getViewState().doEventActionResponse(CustomUserEvent.builder()
    //        .strType(rule.getAction())
    //        .eventText(rule.getMessage())
    //        .eventName(rule.getName())
    //        .build());
    //    //}
    //  }
    //}
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
    Timber.e("sendContacts : " + (SharedPreferenceHelper.getNumberOfContacts()
        != contactForServer.getContactForServerList().size()));
    if (SharedPreferenceHelper.getNumberOfContacts() != contactForServer.getContactForServerList()
        .size()) {
      Subscription subscription = mDataManager.sendContacts(contactForServer)
          .compose(ThreadSchedulers.applySchedulers())
          .subscribe(voidResponse -> {
            Timber.e("sendContacts Success");
            SharedPreferenceHelper.saveNumberOfContacts(
                contactForServer.getContactForServerList().size());
          }, Throwable::printStackTrace);
      addToUnsubscription(subscription);
    }
  }

  public void clearCachedImages(File filesDir) {

    if (filesDir.exists() && filesDir.isDirectory()) {
      String[] children = filesDir.list();
      for (int i = 0; i < children.length; i++) {
        new File(filesDir, children[i]).delete();
      }
    } else {
      Timber.e("Not Deleted");
    }
  }

  public void checkInAppBilling(Purchase purchase, String productName, String freeSku) {
    Subscription subscription = mDataManager.checkInAppBilling(
        new InAppSinglePurchase(productName, purchase.getSku(), purchase.getToken(), freeSku))
        .compose(ThreadSchedulers.applySchedulers())
        .subscribe(subscriptionsResponse -> {
          SharedPreferenceHelper.saveListSubscriptionEntity(
              subscriptionsResponse.body().getSubscriptionEntities());
          Timber.e(String.valueOf(subscriptionsResponse.code()));
          if (subscriptionsResponse.code() == 200) {
          }
        }, Throwable::printStackTrace);
    addToUnsubscription(subscription);
  }

  public void getAnimationByName(String name, String filesDir) {
    SharedPreferenceHelper.saveNameOfCurrentAnim(name);

    Timber.e("getAnimationByName Animation name : " + name + " filesDir : " + filesDir);

    ImageModel anim = mDataManager.getAnimationByNameFromRealm(name);
    if (anim != null) {
      List<Drawable> animationList = new ArrayList<>();
      AnimationDrawable animationDrawable = new AnimationDrawable();
      for (int j = 0; j < anim.getImageUrl().size(); j++) {
        String framePath = "Pustaya stroka";
        try {
          framePath = filesDir + "/animations/" + AnimationHelper.getFilenameFromURL(
              new URL(anim.getImageUrl().get(j)));
        } catch (MalformedURLException e) {
          e.printStackTrace();
        }
        if (new File(framePath).exists() && !SharedPreferenceHelper.isBlockedGetAnimationByName()) {
          animationList.add(Drawable.createFromPath(framePath));

          if (!animationList.isEmpty() && animationList.size() == anim.getImageUrl().size()) {
            Timber.e("getAnimationByName from storage " + animationList.size());
            getViewState().setAnimation(animationList, duration, name);
          }
        } else {

          if (!SharedPreferenceHelper.isBlockedGetAnimationByName()) {
            Timber.e("getAnimationByName Start by need");

            AnimationHelper animationHelper =
                new AnimationHelper(new File(filesDir + "/animations/"),
                    new ArrayList<>(anim.getImageUrl()), 1);
            SharedPreferenceHelper.blockGetAnimsByName();

            animationHelper.download(new AnimationHelper.Callback() {

              @Override public void onEach(File image) {
                animationList.add(Drawable.createFromPath(image.getAbsolutePath()));
              }

              @Override public void onDone(File imagesDir) {
                SharedPreferenceHelper.unBlockGetAnimsByName();
                Timber.e("Everything is downloaded by need");
                Collections.reverse(animationList);
                getViewState().setAnimation(animationList, duration, name);

                //for (Drawable drawable : animationList) {
                //  animationDrawable.addFrame(drawable, duration);
                //}
                //getViewState().setAnimation(animationDrawable1);
              }
            });
          }
        }
      }

      Timber.e("getAnimationByName Animation list size : " + animationList.size());
      //for (Drawable drawable : animationList) {
      //  animationDrawable.addFrame(drawable, duration);
      //}
    }
  }

  public void postEventAboutInAppPurchase(int requestCode, int resultCode, Intent data) {
    mRxBus.post(new RxBusHelper.EventAboutInAppPurchase(requestCode, resultCode, data));
  }
}
