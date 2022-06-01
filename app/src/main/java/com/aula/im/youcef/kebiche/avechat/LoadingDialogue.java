package com.aula.im.youcef.kebiche.avechat;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;

public class LoadingDialogue {
    Activity activity;
    private AlertDialog alertDialog;

    public LoadingDialogue(Activity activity) {
        this.activity = activity;
    }

    void startLoadingAnimation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater layoutInflater = activity.getLayoutInflater();
        builder.setView(layoutInflater.inflate(R.layout.dialogue_loading_animation , null));
        //so the user cant click elsewhere to dismiss the dialogue
        builder.setCancelable(false);
        alertDialog = builder.create();
        alertDialog.show();
    }

    void stopLoadingAnimation(){
        alertDialog.dismiss();
    }


}
