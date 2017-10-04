package com.sugarman.myb.models;

import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import com.sugarman.myb.api.models.responses.Member;

public class ChallengeMember extends Member {

  private @ColorInt int color;

  private @DrawableRes int holder;

  private boolean isPending;

  public ChallengeMember(Member member) {
    setAction(member.getAction());
    setSteps(member.getSteps());
    setPictureUrl(member.getPictureUrl());
    setFbid(member.getFbid());
    setKickCount(member.getKickCount());
    setId(member.getId());
    setName(member.getName());
    setStatus(member.getStatus());
  }

  public int getColor() {
    return color;
  }

  public void setColor(int color) {
    this.color = color;
  }

  public int getHolder() {
    return holder;
  }

  public void setHolder(int holder) {
    this.holder = holder;
  }

  public boolean isPending() {
    return isPending;
  }

  public void setPending(boolean pending) {
    isPending = pending;
  }
}
