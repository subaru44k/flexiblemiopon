package com.subaru.flexiblemiopon.request;

import android.util.Log;

import com.subaru.flexiblemiopon.data.AccessToken;
import com.subaru.flexiblemiopon.data.CouponChangeInfo;
import com.subaru.flexiblemiopon.data.CouponInfo;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * Created by shiny_000 on 2015/02/22.
 */
public class CouponChangeCommand extends Command {
    private CouponInfo mCouponInfo;
    private boolean mIsCouponUse;
    private final String LOG_TAG = "CouponChange";

    public CouponChangeCommand(String developerId, AccessToken token, CouponInfo couponInfo, boolean isCouponUse) {
        super(developerId, token);
        mCouponInfo = couponInfo;
        mIsCouponUse = isCouponUse;
    }

    @Override
    protected String execute() {
        CouponChangeInfo.HdoChangeInfoBuilder hdoChangeInfoBuilder = new CouponChangeInfo.HdoChangeInfoBuilder();
        CouponChangeInfo.CouponChangeInfoBuilder couponChangeInfoBuilder = new CouponChangeInfo.CouponChangeInfoBuilder();

        hdoChangeInfoBuilder.setHdoInfo(mCouponInfo.getHdoInfoList().get(0).getHdoServiceCode(), mIsCouponUse);
        couponChangeInfoBuilder.setHdoInfoObject(hdoChangeInfoBuilder.build());

        HttpPut putRequest = new HttpPut(REQUEST_URI);
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
        String result = null;
        try {
            result = httpClient.execute(putRequest, new ResponseHandler<String>() {
                @Override
                public String handleResponse(HttpResponse httpResponse) throws ClientProtocolException, IOException {
                    return Integer.toString(httpResponse.getStatusLine().getStatusCode());
                }
            });
            Log.d(LOG_TAG, "http put result : " + result);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result + ":::" + "";
    }
}
