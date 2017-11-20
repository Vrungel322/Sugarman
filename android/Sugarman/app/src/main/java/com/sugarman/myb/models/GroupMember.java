package com.sugarman.myb.models;

import com.sugarman.myb.api.models.responses.Member;
import com.sugarman.myb.ui.activities.groupDetails.adapter.GroupMembersAdapter;

public class GroupMember extends Member {

  private int groupType = GroupMembersAdapter.MEMBER_TYPE;

  private boolean isBroken;

  private boolean isBlinked;

  public GroupMember(Member member) {
    setAction(member.getAction());
    setStatus(member.getStatus());
    setId(member.getId());
    setKickCount(member.getKickCount());
    setFbid(member.getId());
    setName(member.getName());
    setPictureUrl(member.getPictureUrl());
    setSteps(member.getSteps());
  }

  public GroupMember() {
    groupType = GroupMembersAdapter.PENDING_LABEL_TYPE;
  }

  public int getGroupType() {
    return groupType;
  }

  public void setGroupType(int groupType) {
    this.groupType = groupType;
  }

  public boolean isBroken() {
    return isBroken;
  }

  public void setBroken(boolean broken) {
    isBroken = broken;
  }

  public boolean isBlinked() {
    return isBlinked;
  }

  public void setBlinked(boolean blinked) {
    isBlinked = blinked;
  }

  public int getSteps() {
    return this.steps;
  }

  public void setSteps(int steps) {
    this.steps = steps;
  }

  @Override public boolean equals(Object o) {
    return super.equals(o);
  }

  @Override public int hashCode() {
    return super.hashCode();
  }
}

