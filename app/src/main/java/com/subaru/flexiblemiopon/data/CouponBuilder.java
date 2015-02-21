package com.subaru.flexiblemiopon.data;

/**
 * Created by shiny_000 on 2015/02/22.
 */
public interface CouponBuilder {
    CouponInfo setHddServiceCode(String str);
    CouponInfo setHdoInfo(CouponInfo.HdoInfo hdoInfo);

    public interface HdoInfoBuilder {
        CouponInfo.HdoInfo setCouponUse(boolean isUsing);
    }
}
