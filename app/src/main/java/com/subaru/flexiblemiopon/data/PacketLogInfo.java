package com.subaru.flexiblemiopon.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shiny_000 on 2015/02/22.
 */
public class PacketLogInfo {

    private final String mHddServiceCode;
    private final List<HdoInfo> mHdoInfoList;

    private PacketLogInfo(PacketLogInfoBuilder builder) {
        mHddServiceCode = builder.mHddServiceCode;
        mHdoInfoList = builder.mHdoInfoList;
    }

    public String getHddServiceCode() {
        return mHddServiceCode;
    }

    public List<HdoInfo> getHdoInfoList() {
        return mHdoInfoList;
    }

    public static class PacketLogInfoBuilder {
        private String mHddServiceCode;
        private final List<HdoInfo> mHdoInfoList = new ArrayList<>();

        public PacketLogInfoBuilder setHddServiceCode(String hddServiceCode) {
            mHddServiceCode = hddServiceCode;
            return this;
        }

        public PacketLogInfoBuilder setHdoInfo(HdoInfo hdoInfo) {
            mHdoInfoList.add(hdoInfo);
            return this;
        }

        public PacketLogInfo build() {
            return new PacketLogInfo(this);
        }
    }

    public static class HdoInfo {

        private final String mHdoServiceCode;
        private final List<PacketLog> mPacketLogList;

        private HdoInfo(HdoUsageInfoBuilder builder) {
            mHdoServiceCode = builder.mHdoServiceCode;
            mPacketLogList = builder.mPacketLogList;
        }

        public String getHdoServiceCode() {
            return mHdoServiceCode;
        }

        public List<PacketLog> getPacketLogList() {
            return mPacketLogList;
        }

        public static class HdoUsageInfoBuilder {
            private String mHdoServiceCode;
            private List<PacketLog> mPacketLogList = new ArrayList<>();

            public HdoUsageInfoBuilder setHdoServiceCode(String hdoServiceCode) {
                mHdoServiceCode = hdoServiceCode;
                return this;
            }

            public HdoUsageInfoBuilder setPacketLog(PacketLog packetLog) {
                mPacketLogList.add(packetLog);
                return this;
            }

            public HdoInfo build() {
                return new HdoInfo(this);
            }
        }
        public static class PacketLog {

            private final String mDate;
            private final String mWithCoupon;
            private final String mWithoutCoupon;

            private PacketLog(PacketLogBuilder builder) {
                mDate = builder.mDate;
                mWithCoupon = builder.mWithCoupon;
                mWithoutCoupon = builder.mWithoutCoupon;
            }

            public String getDate() {
                return mDate;
            }

            public String getWithCoupon() {
                return mWithCoupon;
            }

            public String getWithoutCoupon() {
                return mWithoutCoupon;
            }

            public static class PacketLogBuilder {
                private String mDate;
                private String mWithCoupon;
                private String mWithoutCoupon;

                public PacketLogBuilder setDate(String date) {
                    mDate = date;
                    return this;
                }

                public PacketLogBuilder setWithCoupon(String withCoupon) {
                    mWithCoupon = withCoupon;
                    return this;
                }

                public PacketLogBuilder setWithoutCoupon(String withoutCoupon) {
                    mWithoutCoupon = withoutCoupon;
                    return this;
                }

                public PacketLog build() {
                    return new PacketLog(this);
                }
            }
        }
    }


}