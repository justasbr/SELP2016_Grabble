package com.example.justas.grabble;

import android.content.Context;
import android.provider.Settings;

public class IdentificationUtils {
    public static String getAndroidId(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }
}
