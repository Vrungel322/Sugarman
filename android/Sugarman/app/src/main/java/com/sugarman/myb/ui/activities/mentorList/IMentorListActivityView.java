package com.sugarman.myb.ui.activities.mentorList;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;
import com.sugarman.myb.models.mentor.MentorEntity;
import java.util.List;

/**
 * Created by nikita on 02.10.2017.
 */
@StateStrategyType(AddToEndSingleStrategy.class) public interface IMentorListActivityView
    extends MvpView {
  void fillMentorsList(List<MentorEntity> mentorEntities);

  void setUpUI();
}
