package com.subaru.flexiblemiopon.data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by shiny_000 on 2015/02/22.
 */
public class CouponChangeInfo {

    public static class CouponChangeInfoBuilder {
        private JSONObject mHdoInfoObject;

        public CouponChangeInfoBuilder setHdoInfoObject(JSONObject hdoInfoObject) {
            mHdoInfoObject = hdoInfoObject;
            return this;
        }

        public JSONObject build() {
            try {
                JSONArray array = new JSONArray();
                array.put(mHdoInfoObject);

                JSONObject object = new JSONObject();
                object.put("couponInfo", array);

                return object;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public static class HdoChangeInfoBuilder {
        private JSONArray hdoInfoArray = new JSONArray();

        public HdoChangeInfoBuilder setHdoInfo(String hdoServiceCode, boolean isCouponUse) {
            try {
                JSONObject jsonOneData = new JSONObject();
                jsonOneData.put("hdoServiceCode", hdoServiceCode);
                jsonOneData.put("couponUse", isCouponUse);
                hdoInfoArray.put(jsonOneData);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return this;
        }

        public JSONObject build() {
            try {
                JSONObject jsonObj = new JSONObject();
                jsonObj.put("hdoInfo", hdoInfoArray);
                return jsonObj;
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

}
