package com.constants;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.messaging.logistic.R;

import java.util.Vector;

public class AlertDialogEld {

    Context context;

    public AlertDialogEld(Context context) {
        this.context = context;
    }


    private Vector<AlertDialog> vectorDialogs = new Vector<AlertDialog>();


    public void ShowAlertDialog(String title, String message, String PositiveButtonText, String NegativeButtonText,
                                final int flag, final PositiveButtonCallback positiveCallback, final NegativeButtonCallBack negativeCallback) {
        try {

            closeDialogs();
            AlertDialog alertDialog;
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);  //, R.style.AlertDialogStyle
            if(title.length() > 0) {
                alertDialogBuilder.setTitle(title);
            }
            alertDialogBuilder.setMessage(message);
            alertDialogBuilder.setCancelable(false);
            alertDialogBuilder.setPositiveButton(PositiveButtonText,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int arg1) {
                            positiveCallback.getPositiveClick(flag);
                            dialog.dismiss();

                        }
                    });

            if(NegativeButtonText.length() > 0) {
                alertDialogBuilder.setNegativeButton(NegativeButtonText, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        negativeCallback.getNegativeClick(flag);
                        dialog.dismiss();
                    }
                });
            }

            alertDialog = alertDialogBuilder.create();
            vectorDialogs.add(alertDialog);
            alertDialog.show();

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public interface PositiveButtonCallback {
        void getPositiveClick(int flag);
    }

    public interface NegativeButtonCallBack {
        void getNegativeClick(int flag);
    }


    public void closeDialogs() {
        for (AlertDialog dialog : vectorDialogs)
            if (dialog.isShowing()) dialog.dismiss();
    }


}
