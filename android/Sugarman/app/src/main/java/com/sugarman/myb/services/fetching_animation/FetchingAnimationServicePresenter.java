package com.sugarman.myb.services.fetching_animation;

import android.graphics.drawable.Drawable;
import com.sugarman.myb.App;
import com.sugarman.myb.api.models.responses.animation.GetAnimationResponse;
import com.sugarman.myb.base.BasicPresenter;
import com.sugarman.myb.models.animation.ImageModel;
import com.sugarman.myb.utils.SharedPreferenceHelper;
import com.sugarman.myb.utils.ThreadSchedulers;
import com.sugarman.myb.utils.animation.AnimationHelper;
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
 * Created by nikita on 02.01.2018.
 */
public class FetchingAnimationServicePresenter
    extends BasicPresenter<IFetchingAnimationServiceView> {
  int duration = 30;
  private IFetchingAnimationServiceView view;

  @Override protected void inject() {
    App.getAppComponent().inject(this);
  }

  public void getAnimations(File filesDir) {
    List<Drawable> animationList = new ArrayList<>();
    Subscription subscription = mDataManager.getAnimations()

        .concatMap(getAnimationResponseResponse -> {
          GetAnimationResponse getAnimationResponseFromDB = mDataManager.getAnimationFromBd();
          Timber.e("getAnimations db_anim_side:"
              + getAnimationResponseFromDB.getAnimations().size()
              + "   server_anim_size:"
              + getAnimationResponseResponse.body().getAnimations().size());

          if (getAnimationResponseFromDB != null
              && getAnimationResponseFromDB.getAnimations().size()
              > getAnimationResponseResponse.body().getAnimations().size()) {
            for (ImageModel im : getAnimationResponseFromDB.getAnimations()) {

              Timber.e("getAnimations isInternet anim list contain "
                  + im.getName()
                  + "  bool:"
                  + (!getAnimationResponseResponse.body().getAnimations().contains(im)));
              // если в ответе с интернета есть та анимация которая содержится в бд то все ок, иначе  нужно удалить с памяти телефона эту анимацию
              if (!getAnimationResponseResponse.body().getAnimations().contains(im)) {
                for (String stringUrl : im.getImageUrl()) {
                  Timber.e("getAnimations url_to_delete " + stringUrl);
                  URL urlToDel = null;
                  try {
                    urlToDel = new URL(stringUrl);
                  } catch (MalformedURLException e) {
                    e.printStackTrace();
                  }
                  File file =
                      new File(filesDir + "/" + AnimationHelper.getFilenameFromURL(urlToDel));
                  boolean deleted = file.delete();
                  Timber.e("getAnimations deleted " + deleted+"   url:"+stringUrl);

                }
              }
            }
          }
          return Observable.just(getAnimationResponseResponse);
        })

        .concatMap(getAnimationResponseResponse -> {
          mDataManager.saveAnimation(getAnimationResponseResponse.body());
          return Observable.just(getAnimationResponseResponse);
        }).compose(ThreadSchedulers.applySchedulers()).subscribe(animations -> {

          Timber.e("Got inside animations");
          if (!filesDir.exists()) filesDir.mkdirs();
          Set<String> urls = new HashSet<>();

          List<ImageModel> anims = animations.body().getAnimations();
          for (int i = 0; i < anims.size(); i++) {
            duration = anims.get(i).getDuration();
            //  скачиваются все анимации у которых статус "now"=true
            if (anims.get(i).getDownloadImmediately()) {
              for (int j = 0; j < anims.get(i).getImageUrl().size(); j++) {
                urls.add(anims.get(i).getImageUrl().get(j));
                Timber.e("getAnimations urls from server " + anims.get(i).getImageUrl().get(j));
              }
            }
          }
          if (filesDir.listFiles() != null) {
            List<File> files = Arrays.asList(filesDir.listFiles());
            if (files.size() != urls.size()) {
              SharedPreferenceHelper.blockRules();
            }
            for (File f : files) {
              Timber.e("getAnimations " + f.getName());
              if (urls.contains("https://sugarman-myb.s3.amazonaws.com/" + f.getName())) {
                urls.remove("https://sugarman-myb.s3.amazonaws.com/" + f.getName());
              }
            }
          }
          //test
          for (String u : urls) {
            Timber.e("getAnimations urls to download " + u);
          }

          AnimationHelper animationHelper = new AnimationHelper(filesDir, new ArrayList<>(urls));
          //AnimationDrawable animationDrawable = new AnimationDrawable();

          animationHelper.download(new AnimationHelper.Callback() {

            @Override public void onEach(File image) {
              animationList.add(Drawable.createFromPath(image.getAbsolutePath()));
            }

            @Override public void onDone(File imagesDir) {
              Timber.e("Everything is downloaded");
              SharedPreferenceHelper.unBlockRules();
              Collections.reverse(animationList);
              for (Drawable drawable : animationList) {
                //animationDrawable.addFrame(drawable, duration);
              }
              //getViewState().setAnimation(animationDrawable);
            }
          });
        }, Throwable::printStackTrace);
    addToUnsubscription(subscription);
  }

  public void bind(IFetchingAnimationServiceView view) {
    this.view = view;
  }

  public void unbind() {
    this.view = null;
  }
}
