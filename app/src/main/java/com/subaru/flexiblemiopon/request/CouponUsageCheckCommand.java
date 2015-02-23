package com.subaru.flexiblemiopon.request;

import android.util.Log;

import com.subaru.flexiblemiopon.data.AccessToken;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

/**
 * Created by shiny_000 on 2015/02/23.
 */
public class CouponUsageCheckCommand extends Command {

    private final String LOG_TAG = "CouponUsageCheckCommand";
    private final String USAGE_CHECK_URI = "https://api.iijmio.jp/mobile/d/v1/log/packet/";

    public CouponUsageCheckCommand(String developerId, AccessToken token) {
        super(developerId, token);
    }

    @Override
    protected String execute() {
        if (mAccessToken == null) {
            Log.d(LOG_TAG, "access token is null");
            return "";
        }
        HttpGet httpGet = new HttpGet(USAGE_CHECK_URI);
        httpGet.setHeader("X-IIJmio-Developer", mDeveloperId);
        httpGet.setHeader("X-IIJmio-Authorization", mAccessToken.getAccessToken());
        DefaultHttpClient client = new DefaultHttpClient();
        HttpResponse httpResponse = null;
        try {
            httpResponse = client.execute(httpGet);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // get status code
        int statusCode = httpResponse.getStatusLine().getStatusCode();

        // get response
        HttpEntity entity = httpResponse.getEntity();
        String response = "";
        try {
            response = EntityUtils.toString(entity);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // free resource
        try {
            entity.consumeContent();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // shutdown client
        client.getConnectionManager().shutdown();

        return response;
    }
}
