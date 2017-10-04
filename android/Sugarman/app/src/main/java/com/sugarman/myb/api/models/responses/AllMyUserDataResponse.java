package com.sugarman.myb.api.models.responses;

import com.google.gson.annotations.SerializedName;
import com.sugarman.myb.api.models.responses.me.invites.Invite;
import com.sugarman.myb.api.models.responses.me.notifications.Notification;
import com.sugarman.myb.api.models.responses.me.requests.Request;
import com.sugarman.myb.api.models.responses.me.stats.Stats;
import com.sugarman.myb.api.models.responses.users.User;

public class AllMyUserDataResponse {

  @SerializedName("user") private User user;

  @SerializedName("stats") private Stats[] stats;

  @SerializedName("trackings") private Tracking[] trackings;

  @SerializedName("invites") private Invite[] invites;

  @SerializedName("requests") private Request[] requests;

  @SerializedName("notifications") private Notification[] notifications;

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public Stats[] getStats() {
    return stats;
  }

  public void setStats(Stats[] stats) {
    this.stats = stats;
  }

  public Tracking[] getTrackings() {
    return trackings;
  }

  public void setTrackings(Tracking[] trackings) {
    this.trackings = trackings;
  }

  public Invite[] getInvites() {
    return invites;
  }

  public void setInvites(Invite[] invites) {
    this.invites = invites;
  }

  public Request[] getRequests() {
    return requests;
  }

  public void setRequests(Request[] requests) {
    this.requests = requests;
  }

  public Notification[] getNotifications() {
    return notifications;
  }

  public void setNotifications(Notification[] notifications) {
    this.notifications = notifications;
  }
}
