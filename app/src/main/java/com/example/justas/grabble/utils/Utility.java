package com.example.justas.grabble.utils;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;

import com.example.justas.grabble.MapsActivity;
import com.example.justas.grabble.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Utility {
    public static String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    public static String getDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    public static void confirmExitDialog(final Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        int imageResource = android.R.drawable.ic_dialog_alert;
        Drawable image = ResourcesCompat.getDrawable(context.getResources(), imageResource, null);

        builder.setTitle(context.getString(R.string.exit_dialog_title))
                .setMessage(context.getString(R.string.question_close_grabble))
                .setIcon(image).setCancelable(false)
                .setPositiveButton(context.getString(R.string.yes),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                ((Activity) context).finish();
                            }
                        })
                .setNegativeButton(context.getString(R.string.no),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                            }
                        });

        AlertDialog alert = builder.create();
        alert.setCancelable(false);
        alert.show();
    }

    public static void showFirstTimePlayerAlert(final Context context) {
        FragmentManager fragmentManager = ((Activity) context).getFragmentManager();

        FirstTimePlayerDialogFragment alert = new FirstTimePlayerDialogFragment();
        alert.setCancelable(false);
        alert.show(fragmentManager, "dialog");

    }

    private static class FirstTimePlayerDialogFragment extends DialogFragment {
        public FirstTimePlayerDialogFragment() {

        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.new_player_dialog, null);

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(getActivity().getString(R.string.welcome_to_grabble))
                    .setView(dialogView)
                    .setPositiveButton(getActivity().getString(R.string.okay),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    SharedPreferences sharedPrefs =
                                            PreferenceManager.getDefaultSharedPreferences(getContext());
                                    SharedPreferences.Editor editor = sharedPrefs.edit();

                                    //Make sure we do not show the popup again after user closes it the first time
                                    editor.putBoolean(MapsActivity.SHOW_BEGINNER_POPUP, false).apply();
                                }
                            });

            return builder.create();
        }
    }
}