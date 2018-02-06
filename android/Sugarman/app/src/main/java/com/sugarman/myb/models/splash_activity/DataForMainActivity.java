package com.sugarman.myb.models.splash_activity;

import com.sugarman.myb.api.models.responses.Tracking;
import com.sugarman.myb.api.models.responses.me.invites.Invite;
import com.sugarman.myb.api.models.responses.me.notifications.Notification;
import com.sugarman.myb.api.models.responses.me.requests.Request;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by nikita on 06.02.2018.
 */
@AllArgsConstructor @NoArgsConstructor public class DataForMainActivity {
  @Getter @Setter private Tracking[] trackings;
  @Getter @Setter private Invite[] invites;
  @Getter @Setter private Request[] requests;
  @Getter @Setter private int openActivityCode;
  @Getter @Setter private String trackingIdFromFcm;
  @Getter @Setter private Notification[] notifications;
}
