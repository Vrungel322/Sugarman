package com.sugarman.myb.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

/**
 * Created by nikita on 19.10.2017.
 */

public class DialogHelper {

  public static AlertDialog.Builder createSimpleDialog(String positiveBtnMsg, String negativeBtnMsg,
      String title, String msg, Context context, DialogInterface.OnClickListener positivBtnCallback,
      DialogInterface.OnClickListener negativeBtnCallback) {
    AlertDialog.Builder builder = new AlertDialog.Builder(context);
    builder.setMessage(msg).setTitle(title);
    builder.setPositiveButton(positiveBtnMsg, positivBtnCallback);
    builder.setNegativeButton(negativeBtnMsg, negativeBtnCallback);

    return builder;
  }
}
