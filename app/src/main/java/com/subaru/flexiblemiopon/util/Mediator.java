package com.subaru.flexiblemiopon.util;

/**
 * Created by shiny_000 on 2015/03/07.
 */
public interface Mediator {

    void setComponent(Component component);
    boolean isChecked(Component component);
    void onClicked(Component component);
    void setChecked(Component component, boolean isChecked);
    void setChecked(String componentName, boolean isChecked);
}
