package com.subaru.flexiblemiopon.data;

import android.content.SharedPreferences;
import android.content.res.Resources;

import com.subaru.flexiblemiopon.util.SettingItems;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by shiny_000 on 2015/03/15.
 */
public class SettingIO {
    private static final String LOG_TAG = SettingIO.class.getName();
    private SharedPreferences mSp;
    private Resources mRes;

    public SettingIO(SharedPreferences sp, Resources res) {
        mSp = sp;
        mRes = res;
    }

    public Map<String, Boolean> readSettings() {
        Map<String, Boolean> settingMap = new HashMap<>();
        for (Integer settingId : SettingItems.settingIdList) {
            String settingString = mRes.getString(settingId);
            settingMap.put(settingString, readSetting(settingString));
        }

        return settingMap;
    }

    public Boolean readSetting(String settingItem) {
        return mSp.getBoolean(settingItem, false);
    }

    public void writeSettings(Map<String, Boolean> settingsMap) {
        for (Map.Entry<String, Boolean> entry : settingsMap.entrySet()) {
            writeSetting(entry.getKey(), entry.getValue());
        }
    }

    public void writeSetting(String settingItem, Boolean isChecked) {
        SharedPreferences.Editor editor = mSp.edit();
        editor.putBoolean(settingItem, isChecked);
        editor.commit();
    }
}
