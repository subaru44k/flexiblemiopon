package com.subaru.flexiblemiopon.util;

import android.util.Log;

import com.subaru.flexiblemiopon.FlexibleMioponService;
import com.subaru.flexiblemiopon.R;
import com.subaru.flexiblemiopon.data.SettingIO;

import static com.subaru.flexiblemiopon.util.Constant.PREFERENCE_FILE_NAME;

import java.util.HashMap;
import java.util.Map;

/**
 * Mediator of the setting components.
 * Since the mediator should always be a unique object, this class is singleton.
 */
public class SettingMediator implements Mediator {
    private static final String LOG_TAG = SettingMediator.class.getName();
    private static final SettingMediator mInstance = new SettingMediator();
    private FlexibleMioponService mService;
    private SettingIO mSettingIO;
    Map<Component, Boolean> mComponentStatusMap = new HashMap<>();
    Map<String, Boolean> mComponentNameStatusMap = new HashMap<>();

    private SettingMediator() {
    }

    /**
     * Get the instance of SettingMediator
     * @return singleton instance of SettingMediator
     */
    public static SettingMediator getInstance() {
        return mInstance;
    }

    public void setService(FlexibleMioponService service) {
        mService = service;

        mSettingIO = new SettingIO(mService.getSharedPreferences(PREFERENCE_FILE_NAME, mService.MODE_PRIVATE), mService.getResources());
        Map<String, Boolean> settingMap = mSettingIO.readSettings();

        for (Map.Entry<String, Boolean> entry : settingMap.entrySet()) {
            Log.d(LOG_TAG, entry.getKey() + ":" + entry.getValue().toString());
            mComponentNameStatusMap.put(entry.getKey(), entry.getValue());
            setChecked(entry.getKey(), entry.getValue());
            handleClick(entry.getKey(), entry.getValue());
        }
    }

    @Override
    synchronized public void setComponent(Component component) {
        if (!mComponentStatusMap.containsKey(component)) {
            if (mComponentNameStatusMap.containsKey(component.toString())) {
                mComponentStatusMap.put(component, mComponentNameStatusMap.get(component.toString()));
            } else {
                mComponentStatusMap.put(component, Boolean.FALSE);
                mComponentNameStatusMap.put(component.toString(), Boolean.FALSE);
            }
        }
    }

    @Override
    public boolean isChecked(Component component) {
        return mComponentStatusMap.get(component);
    }

    @Override
    public boolean isChecked(String componentName) {
        for (Component component : mComponentStatusMap.keySet()) {
            if (component.toString().equals(componentName)) {
                return mComponentStatusMap.get(component);
            }
        }
        if (mComponentNameStatusMap.containsKey(componentName)) {
            return mComponentNameStatusMap.get(componentName);
        }
        return false;
    }

    @Override
    public void onClicked(Component component) {
        Boolean isChecked = mComponentStatusMap.get(component);
        Boolean toBeChecked = (isChecked) ? Boolean.FALSE : Boolean.TRUE;

        // handle appropriate operation
        handleClick(component, toBeChecked);

        // change the status
        if (isChecked) {
            mComponentStatusMap.put(component, toBeChecked);
            mComponentNameStatusMap.put(component.toString(), toBeChecked);
            component.setChecked(toBeChecked);
            mSettingIO.writeSetting(component.toString(), toBeChecked);
        } else {
            mComponentStatusMap.put(component, toBeChecked);
            mComponentNameStatusMap.put(component.toString(), toBeChecked);
            component.setChecked(toBeChecked);
            mSettingIO.writeSetting(component.toString(), toBeChecked);
        }
    }

    private void handleClick(String componentName, Boolean isChecked) {
        if (mService == null) {
            return;
        }
        if (componentName.equals(mService.getString(R.string.switch_high_speed))) {
            mService.changeCoupon(isChecked);
        }
        // TODO consider the sensitive case. If one is not ON the coupon and enable auto coupon on/off, then coupon status will be on automatically.
        if (componentName.equals(mService.getString(R.string.switch_change_basedon_screen))) {
            if (isChecked) {
                mService.registerScreenOnOffReceiver();
                mService.toForegroundService();
            } else {
                mService.unregisterScreenOnOffReceiver();
                mService.toBackgroundService();
            }
        }
    }

    private void handleClick(Component component, Boolean isChecked) {
        handleClick(component.toString(), isChecked);
    }

    @Override
    synchronized public void setChecked(Component component, boolean isChecked) {
        mComponentStatusMap.put(component, isChecked);
        mComponentNameStatusMap.put(component.toString(), isChecked);
        component.setChecked(isChecked);
        if (mSettingIO != null) {
            mSettingIO.writeSetting(component.toString(), isChecked);
        }
    }

    synchronized public void setChecked(String componentName, boolean isChecked) {
        for (Component component : mComponentStatusMap.keySet()) {
            if (component.toString().equals(componentName)) {
                mComponentStatusMap.put(component, isChecked);
                component.setChecked(isChecked);
                if (mSettingIO != null) {
                    mSettingIO.writeSetting(componentName, isChecked);
                }
                break;
            }
        }
        if (mComponentNameStatusMap.containsKey(componentName)) {
            mComponentNameStatusMap.put(componentName, isChecked);
        }
    }
}
