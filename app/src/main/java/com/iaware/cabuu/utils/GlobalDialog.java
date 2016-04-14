package com.iaware.cabuu.utils;

import android.content.Context;
import android.support.v7.app.AlertDialog;

/**
 * Created by PeDeNRiQue on 10/01/2016.
 */
public class GlobalDialog {
    public static void alert(Context context, String mensagem,String text) {
        new AlertDialog.Builder(context)
                .setMessage(mensagem)
                .setNegativeButton(text, null)
                .create()
                .show();
    }
}
