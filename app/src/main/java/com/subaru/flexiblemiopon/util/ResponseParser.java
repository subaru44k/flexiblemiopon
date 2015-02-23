package com.subaru.flexiblemiopon.util;

import com.subaru.flexiblemiopon.data.CouponInfo;
import com.subaru.flexiblemiopon.data.PacketLogInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by shiny_000 on 2015/02/23.
 */
public class ResponseParser {

    /**
     * Create CouponInfo from response
     * @param response response from IIJ
     * @return instance of CouponInfo created from response
     * @throws org.json.JSONException
     */
    public static CouponInfo parseCouponInfoResponse(String response) throws JSONException {
        return createCouponInfo(response);
    }

    private static CouponInfo createCouponInfo(String response) throws JSONException {
        CouponInfo.CouponInfoBuilder couponInfoBuilder = new CouponInfo.CouponInfoBuilder();
        JSONObject jsonObject = new JSONObject(response);
        JSONArray couponInfoArray = jsonObject.getJSONArray("couponInfo");
        { // FIXME strictly, couponInfo can be a multiple value, so this block must be for loop
            int i = 0;
            JSONObject couponInfoObject = (JSONObject) couponInfoArray.get(i);
            String hddServiceCode = couponInfoObject.getString("hddServiceCode");
            JSONArray hdoInfoArray = couponInfoObject.getJSONArray("hdoInfo");
            JSONArray couponArray = couponInfoObject.getJSONArray("coupon");
            for (int j=0; j<hdoInfoArray.length(); j++) {
                CouponInfo.HdoInfo hdoInfo = getHdoInfo(hdoInfoArray, j);
                couponInfoBuilder.setHdoInfo(hdoInfo);
            }
            for (int k=0; k<couponArray.length(); k++) {
                CouponInfo.Coupon coupon = getCoupon(couponArray, k);
                couponInfoBuilder.setCoupon(coupon);
            }
            couponInfoBuilder.setHddServiceCode(hddServiceCode);
        }
        return couponInfoBuilder.build();
    }

    private static CouponInfo.HdoInfo getHdoInfo(JSONArray hdoInfoArray, int j) throws JSONException {
        CouponInfo.HdoInfo.HdoInfoBuilder hdoInfoBuilder = new CouponInfo.HdoInfo.HdoInfoBuilder();
        CouponInfo.HdoInfo hdoInfo;

        JSONObject hdoInfoObject = (JSONObject) hdoInfoArray.get(j);
        String couponUse = hdoInfoObject.getString("couponUse");
        String hdoServiceCode = hdoInfoObject.getString("hdoServiceCode");
        JSONArray couponArrayInHdoInfo = hdoInfoObject.getJSONArray("coupon");
        for (int l=0; l<couponArrayInHdoInfo.length(); l++) {
            CouponInfo.Coupon coupon = getCoupon(couponArrayInHdoInfo, l);
            hdoInfoBuilder.setCoupon(coupon);
        }
        hdoInfo = hdoInfoBuilder
                .setCouponUse(Boolean.parseBoolean(couponUse))
                .setHdoServiceCode(hdoServiceCode)
                .build();
        return hdoInfo;
    }

    private static CouponInfo.Coupon getCoupon(JSONArray couponArrayInHdoInfo, int l) throws JSONException {
        CouponInfo.Coupon.CouponBuilder couponBuilder = new CouponInfo.Coupon.CouponBuilder();
        CouponInfo.Coupon coupon;

        JSONObject couponObject = (JSONObject) couponArrayInHdoInfo.get(l);
        String volume = couponObject.getString("volume");
        String expire = couponObject.getString("expire");
        String type = couponObject.getString("type");

        coupon = couponBuilder
                .setVolume(volume)
                .setExpire(expire)
                .setType(type)
                .build();
        return coupon;
    }

    public static PacketLogInfo parsePacketLogInfo(String response) throws JSONException {
        return createCouponUsageInfo(response);
    }

    private static PacketLogInfo createCouponUsageInfo(String response) throws JSONException {
        PacketLogInfo.PacketLogInfoBuilder builder = new PacketLogInfo.PacketLogInfoBuilder();
        JSONObject jsonObject = new JSONObject(response);
        JSONArray couponInfoArray = jsonObject.getJSONArray("packetLogInfo");
        { // FIXME strictly, couponInfo can be a multiple value, so this block must be for loop
            int i = 0;
            JSONObject couponInfoObject = (JSONObject) couponInfoArray.get(i);
            String hddServiceCode = couponInfoObject.getString("hddServiceCode");
            JSONArray hdoInfoArray = couponInfoObject.getJSONArray("hdoInfo");
            for (int j=0; j<hdoInfoArray.length(); j++) {
                PacketLogInfo.HdoInfo hdoInfo = getHdoInfoForCouponUsage(hdoInfoArray, j);
                builder.setHdoInfo(hdoInfo);
            }
            builder.setHddServiceCode(hddServiceCode);
        }
        return builder.build();
    }

    private static PacketLogInfo.HdoInfo getHdoInfoForCouponUsage(JSONArray hdoInfoArray, int j) throws JSONException{
        PacketLogInfo.HdoInfo.HdoUsageInfoBuilder builder = new PacketLogInfo.HdoInfo.HdoUsageInfoBuilder();

        JSONObject hdoInfoObject = (JSONObject) hdoInfoArray.get(j);
        String hdoServiceCode = hdoInfoObject.getString("hdoServiceCode");
        JSONArray packetLogArray = hdoInfoObject.getJSONArray("packetLog");
        for (int l=0; l<packetLogArray.length(); l++) {
            PacketLogInfo.HdoInfo.PacketLog packetLog = getPacketLog(packetLogArray, l);
            builder.setPacketLog(packetLog);
        }
        return builder.setHdoServiceCode(hdoServiceCode).build();
    }

    private static PacketLogInfo.HdoInfo.PacketLog getPacketLog(JSONArray packetLogArray, int k) throws JSONException {
        PacketLogInfo.HdoInfo.PacketLog.PacketLogBuilder builder = new PacketLogInfo.HdoInfo.PacketLog.PacketLogBuilder();

        JSONObject packetLogObject = (JSONObject) packetLogArray.get(k);
        String date = packetLogObject.getString("date");
        String withCoupon = packetLogObject.getString("withCoupon");
        String withoutCoupon = packetLogObject.getString("withoutCoupon");

        return builder.setDate(date).setWithCoupon(withCoupon).setWithoutCoupon(withoutCoupon).build();
    }
}
