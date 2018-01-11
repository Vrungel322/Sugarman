package com.sugarman.myb.ui.dialogs.dialogRescueBoldMan;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.arellomobile.mvp.MvpDialogFragment;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.squareup.picasso.CustomPicasso;
import com.sugarman.myb.R;
import com.sugarman.myb.api.models.responses.Member;
import com.sugarman.myb.api.models.responses.Tracking;
import com.sugarman.myb.constants.Config;
import com.sugarman.myb.constants.Constants;
import com.sugarman.myb.ui.activities.inviteForRescue.InviteForRescueActivity;
import com.sugarman.myb.ui.fragments.rescue_challenge.adapters.RescueMembersAdapter;
import com.sugarman.myb.ui.views.CropSquareTransformation;
import com.sugarman.myb.ui.views.MaskTransformation;
import com.sugarman.myb.utils.Converters;
import com.sugarman.myb.utils.inapp.IabHelper;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import timber.log.Timber;

/**
 * Created by nikita on 11.12.2017.
 */

public class DialogRescueBoldMan extends MvpDialogFragment implements IDialogRescueBoldManView {
  public static final int MONEY = 1;
  public static final int INVITES = 0;
  private static final String DIALOG_RESCUE_BOLD_MAN = "DIALOG_RESCUE_BOLD_MAN";
  private static final String MODE = "MODE";
  @InjectPresenter DialogRescueBoldManPresenter mPresenter;
  @BindView(R.id.group_avatar) ImageView mImageViewGroupAvatar;
  @BindView(R.id.ivCross) ImageView mImageViewCross;
  @BindView(R.id.tvFailText) TextView mTextViewFailText;
  @BindView(R.id.rvFailures) RecyclerView mRecyclerViewFailures;
  @BindView(R.id.tvTimeLeftForRescue) TextView mTextViewTimeLeftForRescue;
  @BindView(R.id.ivRescueLogo) ImageView mImageViewRescueLogo;
  @BindView(R.id.tvRescueForWhat) TextView tvRescueForWhat;
  private Tracking mTracking;
  private RescueMembersAdapter mRescueMembersAdapter;
  private List<Member> failures = new ArrayList<>();
  private IabHelper mHelper;
  private int mMode;
  private String mFreeSku = "v1.group_rescue";

  public static DialogRescueBoldMan newInstance(Tracking tracking, int type) {
    Bundle args = new Bundle();
    args.putParcelable(DIALOG_RESCUE_BOLD_MAN, tracking);
    args.putInt(MODE, type);
    DialogRescueBoldMan fragment = new DialogRescueBoldMan();
    fragment.setArguments(args);
    return fragment;
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mTracking = getArguments().getParcelable(DIALOG_RESCUE_BOLD_MAN);
    mMode = getArguments().getInt(MODE);
    setupInAppPurchase();
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.dialog_rescue_bold_man, container, false);
    ButterKnife.bind(this, rootView);
    setUpUi();
    return rootView;
  }

  private void setUpUi() {
    CustomPicasso.with(mImageViewGroupAvatar.getContext())
        .load(mTracking.getGroup().getPictureUrl())
        .transform(new CropSquareTransformation())
        .transform(
            new MaskTransformation(mImageViewGroupAvatar.getContext(), R.drawable.profile_mask,
                false, 0xfff))
        .into(mImageViewGroupAvatar);

    switch(mMode) {
      case MONEY:
      tvRescueForWhat.setText("FOR 1$");
      break;
      case INVITES:
        tvRescueForWhat.setText("FOR INVITES");
        break;
    }

    mTextViewFailText.setText(String.format(getString(R.string.your_group_has_failed_thanks_to),
        mTracking.getGroup().getName()));

    mRecyclerViewFailures.setLayoutManager(
        new LinearLayoutManager(mRecyclerViewFailures.getContext(), LinearLayoutManager.HORIZONTAL,
            false));
    for (Member m : mTracking.getMembers()) {
      if (m.getFailureStatus() == Member.FAIL_STATUS_FAILUER
          || m.getFailureStatus() == Member.FAIL_STATUS_SAVED) {
        failures.add(m);
      }
    }
    mRescueMembersAdapter = new RescueMembersAdapter(getMvpDelegate());
    //mRescueMembersAdapter.setMembers(Arrays.asList(mTracking.getMembers()));
    mRescueMembersAdapter.setMembers(failures);
    mRecyclerViewFailures.setAdapter(mRescueMembersAdapter);

    CountDownTimer timer = new CountDownTimer(
        mTracking.getRemainToFailUTCDate().getTime() - System.currentTimeMillis(), 1000) {
      @Override public void onTick(long l) {
        mTextViewTimeLeftForRescue.setText(Converters.timeFromMilliseconds(mTextViewTimeLeftForRescue.getContext(), l));
      }

      @Override public void onFinish() {
        mTextViewTimeLeftForRescue.setText(Converters.timeFromMilliseconds(mTextViewTimeLeftForRescue.getContext(), 0L));
      }
    }.start();
  }

  @OnClick(R.id.ivCross) public void ivCrossClick() {
    getDialog().cancel();
  }

  @OnClick(R.id.ivRescueLogo) public void startPurchaseFlowClick() {
    if (mMode == MONEY) {
      mImageViewRescueLogo.setClickable(false);
      startPurchaseFlow("v1.group_rescue");
    }
    if (mMode == INVITES) {
      Intent intent = new Intent(getActivity(), InviteForRescueActivity.class);
      startActivityForResult(intent, Constants.CREATE_GROUP_ACTIVITY_REQUEST_CODE);
    }
  }

  private void setupInAppPurchase() {
    mHelper = new IabHelper(getActivity(), Config.BASE_64_ENCODED_PUBLIC_KEY);

    mHelper.startSetup(result -> {
      if (!result.isSuccess()) {
        Timber.e("In-app Billing setup failed: " + result);
      } else {
        Timber.e("In-app Billing is set up OK");
      }
    });
    mHelper.enableDebugLogging(true);
  }

  public void startPurchaseFlow(String freeSku) {
    mFreeSku = freeSku;
    mHelper.launchPurchaseFlow(getActivity(), freeSku, 10001, (result, purchase) -> {

      if (result.isFailure()) {
        Timber.e("Result is failure");
        consumeItem();

        // Handle error
        return;
      } else if (purchase.getSku().equals(mFreeSku)) {
        Timber.e("Id is correct");
        consumeItem();
        Timber.e(mHelper.getMDataSignature());
      } else {
        Timber.e(result.getMessage());
      }
    }, "mypurchasetoken");
  }

  public void consumeItem() {
    mHelper.queryInventoryAsync(true, Arrays.asList(mFreeSku), (result, inventory) -> {

      if (result.isFailure()) {
        // Handle failure
      } else {
        mHelper.consumeAsync(inventory.getPurchase(mFreeSku), (purchase, result1) -> {
          Timber.e("mConsumeFinishedListener" + purchase.toString());
          Timber.e("mConsumeFinishedListener" + result.toString());
        });
        //mHelper.consumeAsync(inventory.getAllPurchases(), mOnConsumeMultiFinishedListener);
        Timber.e(result.getMessage());
        Timber.e(inventory.getSkuDetails(mFreeSku).getTitle());
        Timber.e(inventory.getSkuDetails(mFreeSku).getSku());

        mPresenter.checkInAppBillingOneDollar(mTracking.getId(), inventory.getPurchase(mFreeSku),
            inventory.getSkuDetails(mFreeSku).getTitle(), mFreeSku);
      }
    });
  }

  @Override public void onDestroy() {
    super.onDestroy();
    if (mHelper != null) mHelper.dispose();
  }

  @Override public void enableButton() {
    mImageViewRescueLogo.setClickable(true);
  }
}
