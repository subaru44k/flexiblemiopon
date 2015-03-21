package com.subaru.flexiblemiopon.util.task;

import com.subaru.flexiblemiopon.util.task.timer.MioponApiCountDownTimer;
import com.subaru.flexiblemiopon.util.task.timer.MioponCountDownTimer;
import com.subaru.flexiblemiopon.util.task.timer.NullCountDownTimer;

/**
 * Created by shiny_000 on 2015/03/21.
 */
public class TimeOptimizedTaskManager implements TaskManager {
    private MioponCountDownTimer mRemainingCouponInfoTimer = new NullCountDownTimer();
    private MioponCountDownTimer mRemainingCouponChangeTimer = new NullCountDownTimer();
    private MioponCountDownTimer mRemainingPacketLogCheckTimer = new NullCountDownTimer();

    private final long WAIT_TIME_BETWEEN_REQUESTS = 61000;

    public TimeOptimizedTaskManager() {
    }

    private void initializePacketLogCheckTimer() {
        setPacketLogCheckTimerWithChallanges(4);
    }

    private void setPacketLogCheckTimerWithChallanges(int challangeNum) {
        mRemainingPacketLogCheckTimer = new MioponApiCountDownTimer(WAIT_TIME_BETWEEN_REQUESTS, challangeNum);
        mRemainingPacketLogCheckTimer.start();
    }

    private void initializeCouponInfoTimer() {
        setCouponInfoTimerWithChallanges(4);
    }

    private void setCouponInfoTimerWithChallanges(int challangeNum) {
        mRemainingCouponInfoTimer = new MioponApiCountDownTimer(WAIT_TIME_BETWEEN_REQUESTS, challangeNum);
        mRemainingCouponInfoTimer.start();
    }

    private void initializeCouponChangeTimer() {
        setCouponChangeTimerWithChallanges(0);
    }

    private void setCouponChangeTimerWithChallanges(int challangeNum) {
        mRemainingCouponChangeTimer = new MioponApiCountDownTimer(WAIT_TIME_BETWEEN_REQUESTS, challangeNum);
        mRemainingCouponChangeTimer.start();
    }

    @Override
    public void notifyCouponInfoCheckInvoked() {
        mRemainingCouponInfoTimer.decrementRequestNum();
    }

    @Override
    public void notifyCouponInfoCheckFailed() {
        setCouponInfoTimerWithChallanges(0);
    }

    @Override
    public void notifyCouponChangeInvoked() {
        initializeCouponChangeTimer();
    }

    @Override
    public void notifyCouponChangeFailed() {
        setCouponChangeTimerWithChallanges(0);
    }

    @Override
    public void notifyPacketLogCheckInvoked() {
        mRemainingPacketLogCheckTimer.decrementRequestNum();
    }

    @Override
    public void notifyPacketLogCheckFailed() {
        setPacketLogCheckTimerWithChallanges(0);
    }

    @Override
    public void blockUntilCouponInfoCheckAvailable() throws InterruptedException {
        Thread.sleep(mRemainingCouponInfoTimer.getRemainingWaitTimeMillis());
        if (mRemainingCouponInfoTimer instanceof NullCountDownTimer) {
        } else {
            initializeCouponInfoTimer();
        }
    }

    @Override
    public void blockUntilCouponChangeAvailable() throws InterruptedException {
        Thread.sleep(mRemainingCouponChangeTimer.getRemainingWaitTimeMillis());
        if (mRemainingCouponInfoTimer instanceof NullCountDownTimer) {
        } else {
            initializeCouponChangeTimer();
        }
    }

    @Override
    public void blockUntilPacketLogCheckAvailable() throws InterruptedException {
        Thread.sleep(mRemainingPacketLogCheckTimer.getRemainingWaitTimeMillis());
        if (mRemainingCouponInfoTimer instanceof NullCountDownTimer) {
        } else {
            initializePacketLogCheckTimer();
        }
    }
}
