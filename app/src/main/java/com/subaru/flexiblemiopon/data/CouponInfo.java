package com.subaru.flexiblemiopon.data;

/**
 * Created by shiny_000 on 2015/02/22.
 */
public class CouponInfo implements CouponBuilder{

    private String mHddServiceCode;
    private HdoInfo mHdoInfo;

    @Override
    public CouponInfo setHddServiceCode(String hddServiceCode) {
        mHddServiceCode = hddServiceCode;
        return this;
    }

    @Override
    public CouponInfo setHdoInfo(HdoInfo hdoInfo) {
        mHdoInfo = hdoInfo;
        return this;
    }

    public static class HdoInfo implements HdoInfoBuilder{

        private boolean mIsCouponUsing;
        @Override
        public HdoInfo setCouponUse(boolean isUsing) {
            mIsCouponUsing = isUsing;
            return this;
        }
    }
}
