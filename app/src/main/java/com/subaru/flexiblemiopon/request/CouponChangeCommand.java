package com.subaru.flexiblemiopon.request;

import android.util.Log;

import com.subaru.flexiblemiopon.data.AccessToken;
import com.subaru.flexiblemiopon.data.CouponChangeInfo;
import com.subaru.flexiblemiopon.data.CouponInfo;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * Created by shiny_000 on 2015/02/22.
 */
public class CouponChangeCommand extends Command {
    private CouponInfo mCouponInfo;
    private boolean mIsCouponUse;

    public CouponChangeCommand(String developerId, AccessToken token, CouponInfo couponInfo, boolean isCouponUse) {
        super(developerId, token);
        mCouponInfo = couponInfo;
        mIsCouponUse = isCouponUse;
    }

    private final String LOG_TAG = "CouponChange";
    private final String PUT_TARGET = "https://api.iijmio.jp/mobile/d/v1/coupon/";

    @Override
    protected String execute() {
        CouponChangeInfo.HdoInfoBuilder hdoInfoBuilder = new CouponChangeInfo.HdoInfoBuilder();
        CouponChangeInfo.CouponChangeInfoBuilder couponChangeInfoBuilder = new CouponChangeInfo.CouponChangeInfoBuilder();

        hdoInfoBuilder.setHdoInfo(mCouponInfo.getHdoInfoList().get(0).getHdoServiceCode(), mIsCouponUse);
        couponChangeInfoBuilder.setHdoInfoObject(hdoInfoBuilder.build());

        HttpPut putRequest = new HttpPut(PUT_TARGET);
        putRequest.setHeader("X-IIJmio-Developer", mDeveloperId);
        putRequest.setHeader("X-IIJmio-Authorization", mAccessToken.getAccessToken());
        putRequest.setHeader("Content-Type", "application/json");
        try {
            String jsonString = couponChangeInfoBuilder.build().toString();
            StringEntity entity = new StringEntity(jsonString);
            putRequest.setEntity(entity);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        DefaultHttpClient httpClient = new DefaultHttpClient();
        try {
            String result = httpClient.execute(putRequest, new ResponseHandler<String>() {
                @Override
                public String handleResponse(HttpResponse httpResponse) throws ClientProtocolException, IOException {
                    switch(httpResponse.getStatusLine().getStatusCode()){
                        case HttpStatus.SC_OK:
                            System.out.println(HttpStatus.SC_OK);
                            return EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
                        case HttpStatus.SC_NOT_FOUND:
                            System.out.println(HttpStatus.SC_NOT_FOUND);
                            return "404";
                        default:
                            System.out.println("unknown");
                            return "unknown";
                    }
                }
            });
            Log.d(LOG_TAG, "http put result : " + result);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
