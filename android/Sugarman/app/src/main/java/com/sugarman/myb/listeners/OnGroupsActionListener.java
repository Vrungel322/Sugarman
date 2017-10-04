package com.sugarman.myb.listeners;

public interface OnGroupsActionListener {

  void onJoinGroup(int position, String trackingId, String groupId);

  void onClickGroup(int position, String trackingId, String groupId);
}
