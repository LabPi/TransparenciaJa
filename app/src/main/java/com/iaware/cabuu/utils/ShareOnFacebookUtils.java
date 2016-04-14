package com.iaware.cabuu.utils;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;

import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;

/**
 * Created by PeDeNRiQue on 23/01/2015.
 */
public class ShareOnFacebookUtils {
    Context cxt;
    CallbackManager callbackManager;
    ShareDialog shareDialog;
    // Your Facebook APP ID
    //private static String APP_ID = "694701837297483"; // Replace with your App//5eJkDSOSboNh8zP7Dl7GLloZByQ=
    // ID
    // Instance of Facebook Class


    public ShareOnFacebookUtils(Context context){
        this.cxt = context;
    }

    public void share(String titulo,String descricao,String link){
        FacebookSdk.sdkInitialize(this.cxt);
        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog((Activity)this.cxt);

        if (ShareDialog.canShow(ShareLinkContent.class)) {
            ShareLinkContent linkContent = new ShareLinkContent.Builder()
                    .setContentTitle(titulo)
                    .setContentDescription(
                            descricao)
                    .setContentUrl(Uri.parse(link))
                    .build();

            shareDialog.show(linkContent);
        }
    }public void share(String titulo,String descricao){
        String link = "http://redeglobo.globo.com/pi/redeclube/";
        FacebookSdk.sdkInitialize(this.cxt);
        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog((Activity)this.cxt);

        if (ShareDialog.canShow(ShareLinkContent.class)) {
            ShareLinkContent linkContent = new ShareLinkContent.Builder()
                    .setContentTitle(titulo)
                    .setContentDescription(
                            descricao)
                    .setContentUrl(Uri.parse(link))
                    .build();

            shareDialog.show(linkContent);
        }
    }


}
