package com.example.justas.grabble;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;

class Utility {
    static void confirmExitDialog(final Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        int imageResource = android.R.drawable.ic_dialog_alert;
        Drawable image = ResourcesCompat.getDrawable(context.getResources(), imageResource, null);

        builder.setTitle("Exit").setMessage("Do you want to close the application?")
                .setIcon(image).setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ((Activity) context).finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });

        AlertDialog alert = builder.create();
        alert.setCancelable(false);
        alert.show();
    }
}