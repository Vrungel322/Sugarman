package com.sugarman.myb.listeners;

import com.sugarman.myb.api.models.responses.Tracking;
import com.sugarman.myb.models.mentor.comments.MentorsCommentsEntity;
import java.util.List;

public interface ApiGetTrackingInfoListener extends ApiBaseListener {

  void onApiGetTrackingInfoSuccess(Tracking tracking, List<MentorsCommentsEntity> commentsEntities, String successRate);

  void onApiGetTrackingInfoFailure(String message);
}
