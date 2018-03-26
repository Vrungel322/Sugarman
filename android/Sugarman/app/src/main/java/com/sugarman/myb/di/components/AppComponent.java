package com.sugarman.myb.di.components;

import com.sugarman.myb.adapters.membersAdapter.MembersAdapterPresenter;
import com.sugarman.myb.base.BasicActivity;
import com.sugarman.myb.base.BasicFragment;
import com.sugarman.myb.di.modules.AppModule;
import com.sugarman.myb.di.scopes.AppScope;
import com.sugarman.myb.services.fetching_animation.FetchingAnimationServicePresenter;
import com.sugarman.myb.services.hourlySaveSteps.HourlySaveStepsServicePresenter;
import com.sugarman.myb.ui.activities.addMember.AddMemberActivityPresenter;
import com.sugarman.myb.ui.activities.approveOtp.ApproveOtpActivityPresenter;
import com.sugarman.myb.ui.activities.base.BasicActivityPresenter;
import com.sugarman.myb.ui.activities.checkout.CheckoutActivityPresenter;
import com.sugarman.myb.ui.activities.createGroup.CreateGroupActivityPresenter;
import com.sugarman.myb.ui.activities.editProfile.EditProfileActivityPresenter;
import com.sugarman.myb.ui.activities.exceptionHidenActivity.SendExceptionHiddenPresenter;
import com.sugarman.myb.ui.activities.groupDetails.GroupDetailsActivityPresenter;
import com.sugarman.myb.ui.activities.groupDetails.adapter.GroupMembersAdapterPresenter;
import com.sugarman.myb.ui.activities.inviteForRescue.InviteForRescueActivityPresenter;
import com.sugarman.myb.ui.activities.invitesScreen.InvitesActivityPresenter;
import com.sugarman.myb.ui.activities.mainScreeen.MainActivityPresenter;
import com.sugarman.myb.ui.activities.mentorDetail.MentorDetailActivityPresenter;
import com.sugarman.myb.ui.activities.mentorList.MentorListActivityPresenter;
import com.sugarman.myb.ui.activities.myStats.MyStatsPresenter;
import com.sugarman.myb.ui.activities.newStats.NewStatsActivityPresenter;
import com.sugarman.myb.ui.activities.productDetail.ProductDetailsActivityPresenter;
import com.sugarman.myb.ui.activities.profile.ProfileActivityPresenter;
import com.sugarman.myb.ui.activities.requestsScreen.RequestsActivityPresenter;
import com.sugarman.myb.ui.activities.searchGroups.SearchGroupsActivityPresenter;
import com.sugarman.myb.ui.activities.shop.ShopActivityPresenter;
import com.sugarman.myb.ui.activities.shopInviteFriend.ShopInviteFriendsActivityPresenter;
import com.sugarman.myb.ui.activities.splash.SplashActivityPresenter;
import com.sugarman.myb.ui.activities.statsTracking.StatsTrackingActivityPresenter;
import com.sugarman.myb.ui.dialogs.dialogCuteRule.DialogCuteRulePresenter;
import com.sugarman.myb.ui.dialogs.dialogRescueBoldMan.DialogRescueBoldManPresenter;
import com.sugarman.myb.ui.dialogs.dialogRescueBoldManKick.DialogRescueBoldManKickPresenter;
import com.sugarman.myb.ui.dialogs.dialogRescueGirl.DialogRescueGirlPresenter;
import com.sugarman.myb.ui.dialogs.dialogRescueGirlCongratulations.DialogRescueGirCongratulationsPresenter;
import com.sugarman.myb.ui.dialogs.sendVkInvitation.SendVkInvitationDialogPresenter;
import com.sugarman.myb.ui.fragments.list_friends_fragment.FriendListFragmentPresenter;
import com.sugarman.myb.ui.fragments.mentors_challenge.MentorsChallengeFragmentPresenter;
import com.sugarman.myb.ui.fragments.no_mentors_challenge.NoMentorsChallengeFragmentPresenter;
import com.sugarman.myb.ui.fragments.rescue_challenge.ChallengeRescueFragmentPresenter;
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

  void inject(ProductDetailsActivityPresenter presenter);

  void inject(ShopActivityPresenter presenter);

  void inject(EditProfileActivityPresenter presenter);

  void inject(ProfileActivityPresenter presenter);

  void inject(MainActivityPresenter presenter);

  void inject(MentorsChallengeFragmentPresenter presenter);

  void inject(NoMentorsChallengeFragmentPresenter presenter);

  void inject(MentorListActivityPresenter presenter);

  void inject(MentorDetailActivityPresenter presenter);

  void inject(GroupDetailsActivityPresenter presenter);

  void inject(GroupMembersAdapterPresenter presenter);

  void inject(SplashActivityPresenter presenter);

  void inject(ApproveOtpActivityPresenter presenter);

  void inject(ChallengeRescueFragmentPresenter presenter);

  void inject(DialogRescueGirlPresenter presenter);

  void inject(DialogRescueBoldManPresenter presenter);

  void inject(InviteForRescueActivityPresenter presenter);

  void inject(DialogRescueGirCongratulationsPresenter presenter);

  void inject(DialogRescueBoldManKickPresenter presenter);

  void inject(FetchingAnimationServicePresenter presenter);

  void inject(DialogCuteRulePresenter presenter);

  void inject(FriendListFragmentPresenter presenter);

  void inject(SendExceptionHiddenPresenter presenter);

  void inject(MyStatsPresenter presenter);

  void inject(StatsTrackingActivityPresenter presenter);

  void inject(InvitesActivityPresenter presenter);

  void inject(RequestsActivityPresenter presenter);

  void inject(NewStatsActivityPresenter presenter);

  void inject(SearchGroupsActivityPresenter presenter);

  void inject(HourlySaveStepsServicePresenter presenter);
}
