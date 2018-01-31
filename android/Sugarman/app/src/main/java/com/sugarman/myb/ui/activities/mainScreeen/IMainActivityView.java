package com.sugarman.myb.ui.activities.mainScreeen;

import android.graphics.drawable.Drawable;
import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;
import com.sugarman.myb.models.custom_events.CustomUserEvent;
import java.util.List;

/**
 * Created by nikita on 06.10.2017.
 */
@StateStrategyType(AddToEndSingleStrategy.class) public interface IMainActivityView
    extends MvpView {
  //void setAnimation(AnimationDrawable animation);

  void doEventActionResponse(CustomUserEvent build);

  void setAnimation(List<Drawable> drawable, int duration, String animName);

  void refreshTrackings();
}
