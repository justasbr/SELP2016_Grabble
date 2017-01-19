package com.example.justas.grabble;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.Toast;

public class BatteryLevelReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context ctx, Intent intent) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor editor = prefs.edit();

        String batterySaverPref = ctx.getString(R.string.pref_battery_saver);

        boolean isBatteryLow = intent.getAction().equals(Intent.ACTION_BATTERY_LOW);
        boolean batterySaverMode = prefs.getBoolean(batterySaverPref, false);

        if (isBatteryLow && !batterySaverMode) {
            editor.putBoolean(batterySaverPref, true).apply();

            Toast.makeText(ctx, ctx.getString(R.string.low_battery_warning), Toast.LENGTH_SHORT).show();
        }

    }
}
