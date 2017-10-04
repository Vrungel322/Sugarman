package com.sugarman.myb.models;

import com.sugarman.myb.api.models.responses.Tracking;

public class SearchTracking extends Tracking {

  private boolean isRequested;

  public SearchTracking(Tracking tracking) {
    setChallengeName(tracking.getChallengeName());
    setCreatedAt(tracking.getCreatedAt());
    setEndDate(tracking.getEndDate());
    setFailingMembers(tracking.getFailingMembers());
    setGroup(tracking.getGroup());
    setGroupOnwerName(tracking.getGroupOnwerName());
    setGroupOwnerId(tracking.getGroupOwnerId());
    setGroupStepsCount(tracking.getGroupStepsCount());
    setId(tracking.getId());
    setCreatedAt(tracking.getCreatedAt());
    setMembers(tracking.getMembers());
    setUpdatedAt(tracking.getUpdatedAt());
    setPending(tracking.getPending());
    setStartDate(tracking.getStartDate());
    setStatus(tracking.getStatus());
  }

  public boolean isRequested() {
    return isRequested;
  }

  public void setRequested(boolean requested) {
    isRequested = requested;
  }
}
