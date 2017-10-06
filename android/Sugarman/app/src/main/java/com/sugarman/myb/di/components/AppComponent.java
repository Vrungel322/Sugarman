package com.sugarman.myb.di.components;

import com.sugarman.myb.adapters.membersAdapter.MembersAdapterPresenter;
import com.sugarman.myb.base.BasicActivity;
import com.sugarman.myb.base.BasicFragment;
import com.sugarman.myb.di.modules.AppModule;
import com.sugarman.myb.di.scopes.AppScope;
import com.sugarman.myb.ui.activities.addMember.AddMemberActivityPresenter;
import com.sugarman.myb.ui.activities.base.BasicActivityPresenter;
import com.sugarman.myb.ui.activities.checkout.CheckoutActivityPresenter;
import com.sugarman.myb.ui.activities.createGroup.CreateGroupActivityPresenter;
import com.sugarman.myb.ui.activities.mainScreeen.MainActivityPresenter;
import com.sugarman.myb.ui.activities.profile.ProfileActivityPresenter;
import com.sugarman.myb.ui.activities.shopInviteFriend.ShopInviteFriendsActivityPresenter;
import com.sugarman.myb.ui.dialogs.sendVkInvitation.SendVkInvitationDialogPresenter;
import dagger.Component;

/**
 * Created by Vrungel on 25.01.2017.
 */
@AppScope @Component(modules = AppModule.class) public interface AppComponent {

  void inject(BasicFragment presenter);

  void inject(BasicActivityPresenter presenter);

  void inject(BasicActivity presenter);

  void inject(CheckoutActivityPresenter presenter);

  void inject(ShopInviteFriendsActivityPresenter presenter);

  void inject(MembersAdapterPresenter presenter);

  void inject(AddMemberActivityPresenter presenter);

  void inject(SendVkInvitationDialogPresenter presenter);

  void inject(CreateGroupActivityPresenter presenter);

  void inject(ProfileActivityPresenter presenter);

  void inject(MainActivityPresenter presenter);
}
