package com.subaru.flexiblemiopon.data;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by shiny_000 on 2015/02/22.
 */
public class CouponIO {

    public void writeCoupon(CouponInfo coupon) {
        // TODO implement here
        // check hdd service code with startsWith()

        // store it to storage
    }

    public CouponInfo readCoupon(String hddServiceCode) {
        // TODO implement here
        // check hddServiceCode

        return null;
    }

    public Set<CouponInfo> readAllCouponSet() {
        Set<CouponInfo> couponSet = new HashSet<>();
        for (String hddServiceCode : getHddServiceCodeSet()) {
            couponSet.add(readCoupon(hddServiceCode));
        }
        return couponSet;
    }

    public Set<String> getHddServiceCodeSet() {
        return null;
    }
}
