package com.subaru.flexiblemiopon;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import static com.subaru.flexiblemiopon.util.Constant.PREFERENCE_FILE_NAME;

import com.subaru.flexiblemiopon.data.SettingIO;

public class BootCompletedReceiver extends BroadcastReceiver {
    private static final String LOG_TAG = BootCompletedReceiver.class.getName();

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d(LOG_TAG, "Boot completed");

        SettingIO SettingIO = new SettingIO(context.getSharedPreferences(PREFERENCE_FILE_NAME, context.MODE_PRIVATE), context.getResources());
        if (!SettingIO.readSetting(context.getString(R.string.switch_auto_start))) {
            Log.d(LOG_TAG, "Auto bootup configuration is off. Do not launch service.");
            return;
        }

        Intent serviceIntent = new Intent(context, FlexibleMioponService.class);
        context.startService(serviceIntent);
    }
}
