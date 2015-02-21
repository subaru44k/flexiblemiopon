package com.subaru.flexiblemiopon.data;

/**
 * Created by shiny_000 on 2015/02/22.
 */
public class CouponInfo {

    private final String mHddServiceCode;
    private final HdoInfo mHdoInfo;

    private CouponInfo(CouponBuilder builder) {
        mHddServiceCode = builder.mHddServiceCode;
        mHdoInfo = builder.mHdoInfo;
    }

    public static class CouponBuilder {
        private String mHddServiceCode;
        private HdoInfo mHdoInfo;

        public CouponBuilder setHddServiceCode(String hddServiceCode) {
            mHddServiceCode = hddServiceCode;
            return this;
        }

        public CouponBuilder setHdoInfo(HdoInfo hdoInfo) {
            mHdoInfo = hdoInfo;
            return this;
        }

        public CouponInfo build() {
            return new CouponInfo(this);
        }
    }

    public static class HdoInfo{

        private final boolean mIsCouponUsing;

        private HdoInfo(HdoInfoBuilder builder) {
            mIsCouponUsing = builder.mIsCouponUsing;
        }
        public static class HdoInfoBuilder {
            private boolean mIsCouponUsing;

            public HdoInfoBuilder setCouponUse(boolean isUsing) {
                mIsCouponUsing = isUsing;
                return this;
            }

            public HdoInfo build() {
                return new HdoInfo(this);
            }
        }
    }
}
