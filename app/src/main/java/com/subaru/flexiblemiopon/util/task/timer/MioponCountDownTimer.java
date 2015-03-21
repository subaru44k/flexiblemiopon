package com.subaru.flexiblemiopon.util.task.timer;

/**
 * Created by shiny_000 on 2015/03/22.
 */
public interface MioponCountDownTimer {

    void start();
    void decrementRequestNum();
    long getRemainingWaitTimeMillis();
}
