package com.subaru.flexiblemiopon.util;

/**
 * Created by shiny_000 on 2015/03/09.
 */
public interface Component {

    void setMediator(Mediator mediator);
    boolean isChecked();
    void onClicked();
    void setChecked(boolean isClicked);
}
