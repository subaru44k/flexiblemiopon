package com.subaru.flexiblemiopon;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.subaru.flexiblemiopon.data.AccessToken;


public class FlexibleMioponActivity extends ActionBarActivity implements FlexibleMioponService.OnDebugOutputListener, FlexibleMioponService.OnSwitchListener{

    private final String LOG_TAG = "FlexibleMioponActivity";

    private FlexibleMioponService mService;
    private FlexibleMioponActivity mActivity = this;
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mService = ((FlexibleMioponService.LocalBinder) iBinder).getService();
            mActivity.setListener();
            mService.Authenticate();
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

        Switch switchView = (Switch) findViewById(R.id.switch1);
        switchView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    mService.Authenticate();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mService != null) {
            mService.Authenticate();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        Log.d(LOG_TAG, "onNewIntent");
        super.onNewIntent(intent);

        // if this activity is launched again, it should not be handled here
        if (!Intent.ACTION_VIEW.equals(intent.getAction())) {
            return;
        }

        mService.getTokenFromAuth(intent);
        mService.retrieveCouponInfo();
    }

    private void setDebugText(final String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView debugView = (TextView) findViewById(R.id.debugText);
                debugView.setText(text);
            }
        });
    }

    private void setListener() {
        mService.setOnDebugOutputListener(this);
        mService.setOnSwitchListener(this);
    }

    @Override
    public void onDebugRequest(String str) {
        setDebugText(str);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mService != null) {
            unbindService(mConnection);
        }
    }

    @Override
    public void onCouponStatusObtained(final boolean isEnabled) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Switch switchView = (Switch) findViewById(R.id.switch1);
                switchView.setChecked(isEnabled);
            }
        });

    }
}
