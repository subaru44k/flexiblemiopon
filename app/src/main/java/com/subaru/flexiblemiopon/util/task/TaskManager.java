package com.subaru.flexiblemiopon.util.task;

/**
 * Created by shiny_000 on 2015/02/27.
 */
public interface TaskManager {
    void notifyCouponInfoCheckInvoked();
    void notifyCouponChangeInvoked();
    void notifyRemainingCouponCheckInvoked();
    void blockUntilCouponInfoCheckAvailable();
    void blockUntilCouponChangeAvailable() throws InterruptedException;
    void blockUntilRemainingCouponCheckAvailable();
}
