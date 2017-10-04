package com.sugarman.myb.listeners;

import com.sugarman.myb.ui.dialogs.DialogButton;
import com.sugarman.myb.ui.dialogs.SugarmanDialog;

public interface SugarmanDialogListener {

  void onClickDialog(SugarmanDialog dialog, DialogButton button);
}
