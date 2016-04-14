package com.iaware.cabuu.utils;

import android.content.Context;
import android.support.v7.app.AlertDialog;

/**
 * Created by PeDeNRiQue on 14/12/2015.
 */
public class CustomDialog {
    public static void alert(Context context, String mensagem,String negativeButton, String titulo) {
        new AlertDialog.Builder(context)
                .setTitle(titulo)
                .setMessage(mensagem)
                .setNegativeButton(negativeButton, null)
                .create()
                .show();
    }
}
