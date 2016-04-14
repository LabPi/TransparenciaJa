package com.iaware.cabuu.utils;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by PeDeNRiQue on 05/05/2015.
 */
public class Connection {


    public static boolean existeConexao(Activity activity){
        ConnectivityManager connectivity = (ConnectivityManager)
                activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null)
        {
            NetworkInfo netInfo = connectivity.getActiveNetworkInfo();

            // Se não existe nenhum tipo de conexão retorna false
            if (netInfo == null) {
                CustomDialog.alert(activity,"Verifique sua conexão com a internet e tente novamente.","Voltar", "");
                return false;
            }

            int netType = netInfo.getType();

            // Verifica se a conexão é do tipo WiFi ou Mobile e
            // retorna true se estiver conectado ou false em
            // caso contrário
            if (netType == ConnectivityManager.TYPE_WIFI ||
                    netType == ConnectivityManager.TYPE_MOBILE) {
                return netInfo.isConnected();

            } else {
                CustomDialog.alert(activity,"Verifique sua conexão com a internet e tente novamente.","Voltar", "");
                return false;
            }
        }else{
            CustomDialog.alert(activity,"Verifique sua conexão com a internet e tente novamente.","Voltar", "");
            return false;
        }
    }
}
