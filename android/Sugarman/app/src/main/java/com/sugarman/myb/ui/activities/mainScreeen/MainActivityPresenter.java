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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import rx.Observable;
import rx.Subscription;
import timber.log.Timber;

/**
 * Created by nikita on 06.10.2017.
 */
@InjectViewState public class MainActivityPresenter extends BasicPresenter<IMainActivityView> {
  int duration = 30;

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
      Rule rule = new Rule();
      int min = Integer.MAX_VALUE;
      for (Rule currentRule : rules) {
        final int diff = Math.abs(currentRule.getCount() - todaySteps);

        if (diff < min) {
          min = diff;
          rule = currentRule;
        }
      }

      Timber.e(
          "rule " + rule.getName() + " todaySteps " + todaySteps + " getCount()" + rule.getCount());
      if (todaySteps >= rule.getCount()) {
        Timber.e("rule true");
        if (!SharedPreferenceHelper.isEventXStepsDone()) {
          getViewState().doEventActionResponse(CustomUserEvent.builder()
              .strType(rule.getAction())
              .eventText(rule.getMessage())
              .eventName(rule.getName())
              .nameOfAnim(rule.getNameOfAnim())
              .build());
        }
      }
    }
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
          Set<String> urls = new HashSet<>();

          List<ImageModel> anims = animations.body().getAnimations();
          for (int i = 0; i < anims.size(); i++) {
            duration = anims.get(i).getDuration();
            for (int j = 0; j < anims.get(i).getImageUrl().size(); j++) {
              urls.add(anims.get(i).getImageUrl().get(j));
              Timber.e("getAnimations urls from server " + anims.get(i).getImageUrl().get(j));
            }
          }
          if (filesDir.listFiles() != null) {
            List<File> files = Arrays.asList(filesDir.listFiles());
            for (File f : files) {
              Timber.e("getAnimations " + f.getName());
              if (urls.contains("https://sugarman-myb.s3.amazonaws.com/" + f.getName())) {
                urls.remove("https://sugarman-myb.s3.amazonaws.com/" + f.getName());
              }

            }
          }
          //test
          for (String u : urls) {
            Timber.e("getAnimations urls to download "+ u);
          }

          AnimationHelper animationHelper = new AnimationHelper(filesDir, new ArrayList<>(urls));
          AnimationDrawable animationDrawable = new AnimationDrawable();

          animationHelper.download(new AnimationHelper.Callback() {

            @Override public void onEach(File image) {
              animationList.add(Drawable.createFromPath(image.getAbsolutePath()));
            }

            @Override public void onDone(File imagesDir) {
              Timber.e("Everything is downloaded");
              Collections.reverse(animationList);
              for (Drawable drawable : animationList) {
                animationDrawable.addFrame(drawable, duration);
              }
              //getViewState().setAnimation(animationDrawable);
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

  public void getAnimationByName(String name, String filesDir) {
    ImageModel anim = mDataManager.getAnimationByNameFromRealm(name);
    List<String> files = new ArrayList<>();
    List<Drawable> animationList = new ArrayList<>();
    AnimationDrawable animationDrawable = new AnimationDrawable();
    for (int j = 0; j < anim.getImageUrl().size(); j++) {
      try {
        files.add(AnimationHelper.getFilenameFromURL(new URL(anim.getImageUrl().get(j))));
        animationList.add(Drawable.createFromPath(
            filesDir + "/animations/" + AnimationHelper.getFilenameFromURL(
                new URL(anim.getImageUrl().get(j)))));
      } catch (MalformedURLException e) {
        e.printStackTrace();
      }
    }
    for (Drawable drawable : animationList) {
      animationDrawable.addFrame(drawable, duration);
    }
    getViewState().setAnimation(animationDrawable);
  }
}
