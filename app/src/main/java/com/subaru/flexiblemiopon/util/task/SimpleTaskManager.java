package com.subaru.flexiblemiopon.util.task;

/**
 * Created by shiny_000 on 2015/02/27.
 */
@Deprecated
public class SimpleTaskManager implements TaskManager{
    @Override
    public void notifyCouponInfoCheckInvoked() {
        // nothing to do
    }

    @Override
    public void notifyCouponInfoCheckFailed() {
        // nothing to do
    }

    @Override
    public void notifyCouponChangeInvoked() {
        // nothing to do
    }

    @Override
    public void notifyCouponChangeFailed() {
        // nothing to do
    }

    @Override
    public void notifyPacketLogCheckInvoked() {
        // nothing to do
    }

    @Override
    public void notifyPacketLogCheckFailed() {
        // nothing to do
    }

    @Override
    public void blockUntilCouponInfoCheckAvailable() throws InterruptedException {
        Thread.sleep(10000);
    }

    @Override
    public void blockUntilCouponChangeAvailable() throws InterruptedException {
        Thread.sleep(30000);
    }

    @Override
    public void blockUntilPacketLogCheckAvailable() throws InterruptedException {
        Thread.sleep(10000);

    }
}
