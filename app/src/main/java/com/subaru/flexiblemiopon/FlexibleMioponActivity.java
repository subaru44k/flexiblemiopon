package com.subaru.flexiblemiopon;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.IBinder;
import android.os.Looper;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.subaru.flexiblemiopon.data.AccessToken;


public class FlexibleMioponActivity extends ActionBarActivity implements FlexibleMioponService.OnDebugOutputListener, FlexibleMioponService.OnAuthenticationListener{

    private final String LOG_TAG = "FlexibleMioponActivity";
    private static final String REDIRECT_URI_BASE = "https://api.iijmio.jp/mobile/d/v1/authorization/?response_type=token&client_id=lNuh3hfMUS52SCTHv4O&redirect_uri=com.subaru.flexiblemiopon://callback";

    private FlexibleMioponService mService;
    private FlexibleMioponActivity mActivity = this;
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mService = ((FlexibleMioponService.LocalBinder) iBinder).getService();
            mActivity.setListener();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mService = null;
        }
    };

    private AccessToken.TokenExpiredListener mTokenListner = new AccessToken.TokenExpiredListener() {
        @Override
        public void onTokenExpired() {
            // TODO handle token expired
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(LOG_TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent serviceIntent = new Intent(FlexibleMioponActivity.this, FlexibleMioponService.class);
        bindService(serviceIntent, mConnection, Context.BIND_AUTO_CREATE);
        redirectForAuthentication();
    }

    public void redirectForAuthentication() {
        String redirectUri = resolveUri();
        Uri uri = Uri.parse(redirectUri);
        Intent i = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(i);
    }

    private String resolveUri() {
        return REDIRECT_URI_BASE + "&state=" + createSession();
    }

    private String createSession() {
        return "hoge";
    }

    @Override
    protected void onNewIntent(Intent intent) {
        Log.d(LOG_TAG, "onNewIntent");
        super.onNewIntent(intent);

        mService.setOnDebugOutputListener(this);
        mService.getTokenFromAuth(intent);
    }

    private void setDebugText(final String text) {
        new android.os.Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                TextView debugView = (TextView) findViewById(R.id.debugText);
                debugView.setText(text);
            }
        });
    }

    private void setListener() {
        mService.setOnDebugOutputListener(this);
        mService.setOnAuthenticationListener(this);
    }

    @Override
    public void onDebugRequest(String str) {
        setDebugText(str);
    }

    @Override
    public void onAuthenticationRequest() {
        redirectForAuthentication();
    }
}
