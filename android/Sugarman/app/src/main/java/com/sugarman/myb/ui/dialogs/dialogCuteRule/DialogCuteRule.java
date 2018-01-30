package com.sugarman.myb.ui.dialogs.dialogCuteRule;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.arellomobile.mvp.MvpDialogFragment;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.sugarman.myb.R;

/**
 * Created by nikita on 30.01.2018.
 */

public class DialogCuteRule extends MvpDialogFragment implements IDialogCuteRuleView {
  private static final String CUTE_DIALOG_MSG = "CUTE_DIALOG_MSG";
  @InjectPresenter DialogCuteRulePresenter mPresenter;
  @BindView(R.id.cardView)CardView mCardView;
  @BindView(R.id.tvHeaderText)TextView mTextViewHeader;
  private IOnCuteDialogClickListener listener;

  public static DialogCuteRule newInstance(String msg, IOnCuteDialogClickListener listener) {
     Bundle args = new Bundle();
     args.putString(CUTE_DIALOG_MSG, msg);
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
  }

  @OnClick(R.id.cardView) void cardViewClicked(){
    listener.clickCuteDialog();
    getDialog().cancel();
  }

  private void setClickListener(IOnCuteDialogClickListener listener) {
    this.listener=listener;
  }
}
