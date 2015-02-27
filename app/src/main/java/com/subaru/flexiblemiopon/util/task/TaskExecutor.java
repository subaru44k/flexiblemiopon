package com.subaru.flexiblemiopon.util.task;

import com.subaru.flexiblemiopon.data.AccessToken;
import com.subaru.flexiblemiopon.data.CouponInfo;
import com.subaru.flexiblemiopon.data.PacketLogInfo;

/**
 * Created by shiny_000 on 2015/02/27.
 */
public interface TaskExecutor {
    public void getCouponInfo(AccessToken token, OnCouponInfoObtainedListener listener);
    public void executeCouponChange(CouponInfo couponInfo, boolean isCouponUse, AccessToken token, OnCouponChangedListener listener);
    public void getPacketLog(CouponInfo couponInfo, AccessToken token, OnPacketLogObtainedListener listener);

    interface OnCouponInfoObtainedListener {
        /**
         * Called when coupon info obtained
         * @param couponInfo coupon information
         */
        void onCouponInfoObtained(CouponInfo couponInfo);

        /**
         * Called when coupon cannot be changed due to some reason.
         * @param reason reason of the failure.
         */
        void onCouponInfoFailedToObtain(String reason);

        /**
         * Called when coupon cannot be changed due to many trials.
         */
        void onNotifyRetryLater();
    }

    interface OnCouponChangedListener {

        /**
         * Called when coupon changed correctly.
         */
        void onCouponChanged();

        /**
         * Called when coupon cannot be changed due to some reason.
         * @param reason reason of the failure.
         */
        void onCouponChangeFailed(String reason);

        /**
         * Called when coupon cannot be changed due to AccessToken is invalid.
         */
        void onAccessTokenInvalid();

        /**
         * Called when coupon cannot be changed due to many trials.
         */
        void onNotifyRetryLater();
    }

    interface OnPacketLogObtainedListener {

        void onPacketLogInfoObtained(PacketLogInfo packetLogInfo);

        void onPacketLogFailedToObtain(String reason);

        /**
         * Called when coupon cannot be changed due to AccessToken is invalid.
         */
        void onAccessTokenInvalid();

        /**
         * Called when coupon cannot be changed due to many trials.
         */
        void onNotifyRetryLater();
    }
}
