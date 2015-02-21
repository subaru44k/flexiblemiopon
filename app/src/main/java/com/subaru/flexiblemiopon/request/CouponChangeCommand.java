package com.subaru.flexiblemiopon.request;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

import com.subaru.flexiblemiopon.data.AccessToken;

/**
 * Created by shiny_000 on 2015/02/21.
 */
public class CouponChangeCommand extends Command {

    public CouponChangeCommand(String developerId, AccessToken token) {
        super(developerId, token);
    }

    @Override
    public String execute() {
        HttpGet httpGet = new HttpGet(REQUEST_URI);
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
