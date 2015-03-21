package com.subaru.flexiblemiopon.util.task;

/**
 * Created by shiny_000 on 2015/02/27.
 */
public interface TaskManager {
    /**
     * Let TaskManager know coupon info check is invoked
     */
    void notifyCouponInfoCheckInvoked();

    /**
     * Let TaskManager know coupon info check is failed.
     * It would because user reboot his/her device and TaskManager is reset.
     */
    void notifyCouponInfoCheckFailed();

    /**
     * Let TaskManager know coupon change is invoked.
     */
    void notifyCouponChangeInvoked();

    /**
     * Let TaskManager know coupon change is failed.
     * It would because user reboot his/her device and TaskManager is reset.
     */
    void notifyCouponChangeFailed();

    /**
     * Let TaskManager know packet log check is invoked.
     */
    void notifyPacketLogCheckInvoked();

    /**
     * Let TaskManager know packet log check is failed.
     * It would because user reboot his/her device and TaskManager is reset.
     */
    void notifyPacketLogCheckFailed();

    /**
     * Return time to wait until coupon info check is available
     * @throws InterruptedException received a request to suspend this task
     */
    void blockUntilCouponInfoCheckAvailable() throws InterruptedException;

    /**
     * Return time to wait until coupon change is available.
     * @throws InterruptedException received a request to suspend this task
     */
    void blockUntilCouponChangeAvailable() throws InterruptedException;

    /**
     * Return time to wait until packet log check is available
     * @throws InterruptedException received a request to suspend this task
     */
    void blockUntilPacketLogCheckAvailable() throws InterruptedException;
}
