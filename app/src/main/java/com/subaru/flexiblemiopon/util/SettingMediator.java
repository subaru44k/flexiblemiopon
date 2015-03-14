package com.subaru.flexiblemiopon.util;

import com.subaru.flexiblemiopon.FlexibleMioponService;

import java.util.HashMap;
import java.util.Map;

/**
 * Mediator of the setting components.
 * Since the mediator should always be a unique object, this class is singleton.
 */
public class SettingMediator implements Mediator {
    private static final SettingMediator mInstance = new SettingMediator();
    private FlexibleMioponService mService;

    private SettingMediator() {}

    /**
     * Get the instance of SettingMediator
     * @return singleton instance of SettingMediator
     */
    public static SettingMediator getInstance() {
        return mInstance;
    }

    Map<Component, Boolean> mComponentStatusMap = new HashMap<>();

    public void setService(FlexibleMioponService service) {
        mService = service;
    }

    @Override
    public void setComponent(Component component) {
        if (!mComponentStatusMap.containsKey(component)) {
            mComponentStatusMap.put(component, Boolean.FALSE);
        }
    }

    @Override
    public boolean isChecked(Component component) {
        return mComponentStatusMap.get(component);
    }

    @Override
    public void onClicked(Component component) {
        if (mComponentStatusMap.get(component)) {
            mComponentStatusMap.put(component, Boolean.FALSE);
            component.setChecked(Boolean.FALSE);
        } else {
            mComponentStatusMap.put(component, Boolean.TRUE);
            component.setChecked(Boolean.TRUE);
        }
    }

    @Override
    public void setChecked(Component component, boolean isChecked) {
        mComponentStatusMap.put(component, isChecked);
        component.setChecked(isChecked);
    }

    public void setChecked(String  componentName, boolean isChecked) {
        for (Component component : mComponentStatusMap.keySet()) {
            if (component.toString().equals(componentName)) {
                mComponentStatusMap.put(component, isChecked);
                component.setChecked(isChecked);
                break;
            }
        }
    }
}
