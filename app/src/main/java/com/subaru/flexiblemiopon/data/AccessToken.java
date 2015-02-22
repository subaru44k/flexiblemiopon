package com.subaru.flexiblemiopon.data;

import java.io.Serializable;

/**
 * Created by shiny_000 on 2015/02/21.
 */
public class AccessToken implements Serializable {
    private static final long serialVersionUID = 8531245739641223373L;

    public AccessToken(String accessToken, String tokenType, int expireSec, String state) {
        mAccessToken = accessToken;
        mTokenType = tokenType;
        mExpireSec = expireSec;
        mState = state;
    }

    private String mAccessToken;
    private String mTokenType;
    private int mExpireSec;
    private String mState;

    public String getAccessToken() {
        return mAccessToken;
    }

    public String getmTokenType() {
        return mTokenType;
    }

    public int getExpireSec() {
        return mExpireSec;
    }

    public String getState() {
        return mState;
    }

    public interface TokenExpiredListener {
        public void onTokenExpired();
    }
}
