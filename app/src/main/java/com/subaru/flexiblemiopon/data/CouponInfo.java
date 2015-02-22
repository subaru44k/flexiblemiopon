package com.subaru.flexiblemiopon.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shiny_000 on 2015/02/22.
 */
public class CouponInfo {

    private final String mHddServiceCode;
    private final List<HdoInfo> mHdoInfoList;
    private final List<Coupon> mCouponList;

    private CouponInfo(CouponInfoBuilder builder) {
        mHddServiceCode = builder.mHddServiceCode;
        mHdoInfoList = builder.mHdoInfoList;
        mCouponList = builder.mCouponList;
    }

    public String getHddServiceCode() {
        return mHddServiceCode;
    }

    public List<HdoInfo> getHdoInfoList() {
        return mHdoInfoList;
    }

    public List<Coupon> getCouponList() {
        return mCouponList;
    }

    public static class CouponInfoBuilder {
        private String mHddServiceCode;
        private final List<HdoInfo> mHdoInfoList = new ArrayList<>();
        private List<Coupon> mCouponList = new ArrayList<>();

        public CouponInfoBuilder setHddServiceCode(String hddServiceCode) {
            mHddServiceCode = hddServiceCode;
            return this;
        }

        public CouponInfoBuilder setHdoInfo(HdoInfo hdoInfo) {
            mHdoInfoList.add(hdoInfo);
            return this;
        }

        public CouponInfoBuilder setCoupon(Coupon coupon) {
            mCouponList.add(coupon);
            return this;
        }

        public CouponInfo build() {
            return new CouponInfo(this);
        }
    }

    public static class HdoInfo{

        private final boolean mIsCouponUsing;
        private final List<Coupon> mCouponList;

        private HdoInfo(HdoInfoBuilder builder) {
            mIsCouponUsing = builder.mIsCouponUsing;
            mCouponList = builder.mCouponList;
        }

        public boolean isCouponUsing() {
            return mIsCouponUsing;
        }

        public List<Coupon> getCouponList() {
            return mCouponList;
        }

        public static class HdoInfoBuilder {
            private boolean mIsCouponUsing;
            private List<Coupon> mCouponList = new ArrayList<>();

            public HdoInfoBuilder setCouponUse(boolean isUsing) {
                mIsCouponUsing = isUsing;
                return this;
            }

            public HdoInfoBuilder setCoupon(Coupon coupon) {
                mCouponList.add(coupon);
                return this;
            }

            public HdoInfo build() {
                return new HdoInfo(this);
            }
        }
    }

    public static class Coupon {

        private final String mVolume;
        private final String mExpire;
        private final String mType;

        private Coupon(CouponBuilder builder) {
            mVolume = builder.mVolume;
            mExpire = builder.mExpire;
            mType = builder.mType;
        }

        public String getVolume() {
            return mVolume;
        }

        public String getExpire() {
            return mExpire;
        }

        public String getType() {
            return mType;
        }

        public static class CouponBuilder {
            private String mVolume;
            private String mExpire;
            private String mType;

            public CouponBuilder setVolume(String volume) {
                mVolume = volume;
                return this;
            }

            public CouponBuilder setExpire(String expire) {
                mExpire = expire;
                return this;
            }

            public CouponBuilder setType(String type) {
                mType = type;
                return this;
            }

            public Coupon build() {
                return new Coupon(this);
            }
        }
    }
}
