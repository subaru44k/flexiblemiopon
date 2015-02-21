package com.subaru.flexiblemiopon.data;

/**
 * Created by shiny_000 on 2015/02/21.
 */
public class AccessToken {

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

    public interface TokenExpiredListener {
        public void onTokenExpired();
    }
}
