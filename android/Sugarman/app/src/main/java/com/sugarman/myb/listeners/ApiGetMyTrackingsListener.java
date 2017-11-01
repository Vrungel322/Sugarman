package com.sugarman.myb.listeners;

import com.sugarman.myb.api.models.responses.Tracking;
import com.sugarman.myb.models.mentors_group.MentorsGroup;
import java.util.List;

public interface ApiGetMyTrackingsListener extends ApiBaseListener {

  void onApiGetMyTrackingSuccess(Tracking[] trackings, List<MentorsGroup> mentorsGroup, boolean isRefreshNotification);

  void onApiGetMyTrackingsFailure(String message);
}
