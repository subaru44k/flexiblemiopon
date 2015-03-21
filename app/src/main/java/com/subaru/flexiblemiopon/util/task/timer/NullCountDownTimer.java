package com.subaru.flexiblemiopon.util.task.timer;

/**
 * Created by shiny_000 on 2015/03/22.
 */
public class NullCountDownTimer implements MioponCountDownTimer {
    @Override
    public void start() {
        // nothing to do
    }

    @Override
    public void decrementRequestNum() {
        // nothing to do
    }

    @Override
    public long getRemainingWaitTimeMillis() {
        return 0;
    }
}
