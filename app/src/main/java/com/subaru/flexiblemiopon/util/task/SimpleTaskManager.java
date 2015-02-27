package com.subaru.flexiblemiopon.util.task;

/**
 * Created by shiny_000 on 2015/02/27.
 */
public class SimpleTaskManager implements TaskManager{
    @Override
    public void notifyCouponInfoCheckInvoked() {
        // nothing to do
    }

    @Override
    public void notifyCouponChangeInvoked() {

    }

    @Override
    public void notifyRemainingCouponCheckInvoked() {

    }

    @Override
    public void blockUntilCouponInfoCheckAvailable(){

    }

    @Override
    public void blockUntilCouponChangeAvailable() throws InterruptedException  {
        Thread.sleep(30000);
    }

    @Override
    public void blockUntilRemainingCouponCheckAvailable() {

    }
}
