package com.sugarman.myb.listeners;

import com.sugarman.myb.models.GroupMember;

public interface OnStepMembersActionListener {

  void onPokeMember(GroupMember member);

  void onPokePendingMember();

  void onPokeSelf();

  void onPokeCompletedDaily();

  void onPokeMoreThatSelf();

  void onPokeInForeignGroup();

  void onPokeSaver();

  void youCantPokeYourself();

  void failuerCantPokeAnyone();
}
