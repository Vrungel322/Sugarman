package com.sugarman.myb.ui.activities.mentorDetail;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;
import com.sugarman.myb.models.mentor.comments.MentorsCommentsEntity;
import java.util.List;

/**
 * Created by yegoryeriomin on 10/30/17.
 */

@StateStrategyType(AddToEndSingleStrategy.class) public interface IMentorDetailActivityView
    extends MvpView {
  void fillMentorsFriendsList();

  void fillMentorsVideosList();

  void setUpUI();

  void fillCommentsList(List<MentorsCommentsEntity> mentorsCommentsEntities);

  void moveToMainActivity();

  void startPurchaseFlow(String freeSku);
}
