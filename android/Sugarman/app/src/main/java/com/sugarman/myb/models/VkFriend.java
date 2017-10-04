package com.sugarman.myb.models;

import android.support.annotation.NonNull;

/**
 * Created by Y500 on 04.09.2017.
 */

public class VkFriend implements Comparable<VkFriend> {

  private int id;
  private String first_name;
  private String last_name;
  private String name;
  private String photoUrl;
  private int online;
  private int isInvitable;
  private boolean isSelected;
  private boolean isAdded;
  private boolean isPending;

  public VkFriend(int id, String firstName, String lastName, String photoUrl, int online) {
    this.id = id;
    this.first_name = firstName;
    this.last_name = lastName;
    this.photoUrl = photoUrl;
    this.online = online;
    this.name = firstName + " " + lastName;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public boolean isSelected() {
    return isSelected;
  }

  public void setSelected(boolean selected) {
    isSelected = selected;
  }

  public boolean isAdded() {
    return isAdded;
  }

  public void setAdded(boolean added) {
    isAdded = added;
  }

  public boolean isPending() {
    return isPending;
  }

  public void setPending(boolean pending) {
    isPending = pending;
  }

  public String getFirstName() {
    return first_name;
  }

  public void setFirstName(String first_name) {
    this.first_name = first_name;
  }

  public String getLastName() {
    return last_name;
  }

  public void setLastName(String last_name) {
    this.last_name = last_name;
  }

  public String getPhotoUrl() {
    return photoUrl;
  }

  public void setPhotoUrl(String photoUrl) {
    this.photoUrl = photoUrl;
  }

  public int getOnline() {
    return online;
  }

  public void setOnline(int online) {
    this.online = online;
  }

  public int getIsInvitable() {
    return isInvitable;
  }

  public void setIsInvitable(int isInvitable) {
    this.isInvitable = isInvitable;
  }

  public String getName() {
    return name;
  }

  @Override public String toString() {
    return "ClassPojo [id = "
        + id
        + ", first_name = "
        + first_name
        + ", last_name = "
        + last_name
        + ", online = "
        + online
        + "]";
  }

  @Override public int compareTo(@NonNull VkFriend friend) {
    return this.getName().compareTo(friend.getName());
  }
}
