package com.sugarman.myb.ui.activities.mainScreeen;

import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import com.arellomobile.mvp.InjectViewState;
import com.sugarman.myb.App;
import com.sugarman.myb.base.BasicPresenter;
import com.sugarman.myb.constants.Constants;
import com.sugarman.myb.models.ContactListForServer;
import com.sugarman.myb.models.animation.ImageModel;
import com.sugarman.myb.models.custom_events.CustomUserEvent;
import com.sugarman.myb.models.custom_events.Rule;
import com.sugarman.myb.models.iab.InAppSinglePurchase;
import com.sugarman.myb.utils.SharedPreferenceHelper;
import com.sugarman.myb.utils.ThreadSchedulers;
import com.sugarman.myb.utils.animation.AnimationHelper;
import com.sugarman.myb.utils.inapp.Purchase;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import rx.Observable;
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
    subscribeShowDialogEvent();
  }

  private void subscribeShowDialogEvent() {
    Subscription subscription = mRxBus.filteredObservable(CustomUserEvent.class)
        .compose(ThreadSchedulers.applySchedulers())
        .subscribe(customUserEvent -> {
          if (!SharedPreferenceHelper.isEventGroupWithXNewUsersDone()) {
            getViewState().doEventActionResponse(customUserEvent);
          }
        });
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

  public void checkIfRuleStepsDone(int todaySteps) {
    List<Rule> rules = mDataManager.getRuleByName(Constants.EVENT_X_STEPS_DONE);
    if (rules != null && !rules.isEmpty()) {
      Rule rule = rules.get(0);

      Timber.e(
          "rule " + rule.getName() + " todaySteps " + todaySteps + " getCount()" + rule.getCount());
      if (todaySteps >= rule.getCount()) {
        Timber.e("rule true");
        if (!SharedPreferenceHelper.isEventXStepsDone()) {
          getViewState().doEventActionResponse(CustomUserEvent.builder()
              .strType(rule.getAction())
              .eventText(rule.getMessage())
              .eventName(rule.getName())
              .build());
        }
      }
    }
  }

  public void checkIfRule15KStepsDone(int todaySteps) {
    List<Rule> rules = mDataManager.getRuleByName(Constants.EVENT_15K_STEPS_DONE);
    if (!rules.isEmpty()) {
      Rule rule = rules.get(0);

      Timber.e(
          "rule " + rule.getName() + " todaySteps " + todaySteps + " getCount()" + rule.getCount());
      if (todaySteps >= rule.getCount()) {
        Timber.e("rule 15K true");
        //if (!SharedPreferenceHelper.isEventXStepsDone()) {
        getViewState().doEventActionResponse(CustomUserEvent.builder()
            .strType(rule.getAction())
            .eventText(rule.getMessage())
            .eventName(rule.getName())
            .build());
        //}
      }
    }
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

  public void getAnimations(File filesDir) {
    List<Drawable> animationList = new ArrayList<>();
    Subscription subscription =
        mDataManager.getAnimations().concatMap(getAnimationResponseResponse -> {
          mDataManager.saveAnimation(getAnimationResponseResponse.body());
          return Observable.just(getAnimationResponseResponse);
        }).compose(ThreadSchedulers.applySchedulers()).subscribe(animations -> {
          Timber.e("Got inside animations");
          if (!filesDir.exists()) filesDir.mkdirs();
          List<String> urls = new ArrayList<>();

          List<ImageModel> anims = animations.body().getAnimations();
          for (int i = 0; i < anims.size(); i++) {

            for (int j = 0; j < anims.get(i).getImageUrl().size(); j++) {
              urls.add(anims.get(i).getImageUrl().get(j));
              Timber.e(anims.get(i).getImageUrl().get(j));
            }
          }
          AnimationHelper animationHelper = new AnimationHelper(filesDir, urls);
          AnimationDrawable animationDrawable = new AnimationDrawable();

          animationHelper.download(new AnimationHelper.Callback() {
            @Override public void onEach(File image) {
              animationList.add(Drawable.createFromPath(image.getAbsolutePath()));
            }

            @Override public void onDone(File imagesDir) {
              Timber.e("Everything is downloaded");
              for (Drawable drawable : animationList) {
                animationDrawable.addFrame(drawable, 30);
              }
              getViewState().setAnimation(animationDrawable);
            }
          });
        }, Throwable::printStackTrace);
    addToUnsubscription(subscription);
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
}
