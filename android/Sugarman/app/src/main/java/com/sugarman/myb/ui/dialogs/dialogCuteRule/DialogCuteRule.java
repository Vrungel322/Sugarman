package com.sugarman.myb.ui.dialogs.dialogCuteRule;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
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
import timber.log.Timber;

/**
 * Created by nikita on 30.01.2018.
 */

public class DialogCuteRule extends MvpDialogFragment implements IDialogCuteRuleView {
  private static final String CUTE_DIALOG_MSG = "CUTE_DIALOG_MSG";
  private static final String CUTE_DIALOG_STR_VALUE = "CUTE_DIALOG_STR_VALUE";
  @InjectPresenter DialogCuteRulePresenter mPresenter;
  @BindView(R.id.cardView) CardView mCardView;
  @BindView(R.id.tvHeaderText) TextView mTextViewHeader;
  @BindView(R.id.ivPopupImg) ImageView mImageViewPopupImg;
  private IOnCuteDialogClickListener listener;

  public static DialogCuteRule newInstance(String msg, String strValue,
      IOnCuteDialogClickListener listener) {
    Bundle args = new Bundle();
    args.putString(CUTE_DIALOG_MSG, msg);
    args.putString(CUTE_DIALOG_STR_VALUE, strValue);
    DialogCuteRule fragment = new DialogCuteRule();
    fragment.setClickListener(listener);
    fragment.setArguments(args);
    return fragment;
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.dialog_rule_popup, container, false);
    ButterKnife.bind(this, rootView);
    setUpUi();
    return rootView;
  }

  private void setUpUi() {
    mTextViewHeader.setText(getArguments().getString(CUTE_DIALOG_MSG));

    Timber.e("setUpUi " + getArguments().getString(CUTE_DIALOG_STR_VALUE));
    CustomPicasso.with(mImageViewPopupImg.getContext())
        .load(getArguments().getString(CUTE_DIALOG_STR_VALUE))
        .placeholder(R.drawable.popup_accept)
        .into(mImageViewPopupImg);
  }

  @OnClick(R.id.cardView) void cardViewClicked() {
    listener.clickCuteDialog();
    getDialog().cancel();
  }

  private void setClickListener(IOnCuteDialogClickListener listener) {
    this.listener = listener;
  }
}
