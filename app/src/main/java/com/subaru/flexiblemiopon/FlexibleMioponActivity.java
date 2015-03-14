package com.subaru.flexiblemiopon;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.subaru.flexiblemiopon.data.AccessToken;
import com.subaru.flexiblemiopon.data.PacketLogInfo;
import com.subaru.flexiblemiopon.util.Mediator;
import com.subaru.flexiblemiopon.util.SettingMediator;
import com.subaru.flexiblemiopon.view.FlexibleFragmentPagerAdaper;
import com.subaru.flexiblemiopon.view.SettingFragment;
import com.subaru.flexiblemiopon.view.MainFragment;
import com.subaru.flexiblemiopon.view.PacketLogFragment;
import com.viewpagerindicator.CirclePageIndicator;


public class FlexibleMioponActivity extends ActionBarActivity
        implements SettingFragment.OnFragmentInteractionListener, PacketLogFragment.OnFragmentInteractionListener, MainFragment.OnFragmentInteractionListener,
        FlexibleMioponService.OnViewOperationListener, FlexibleMioponService.OnSwitchListener, FlexibleMioponService.OnPacketLogListener {

    private final String LOG_TAG = "FlexibleMioponActivity";

    private FlexibleMioponService mService;
    private FlexibleMioponActivity mActivity = this;
    private FlexibleFragmentPagerAdaper mFragmentPagerAdapter;

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mService = ((FlexibleMioponService.LocalBinder) iBinder).getService();
            mActivity.setListener();
            mService.Authenticate();

            SettingMediator.getInstance().setService(mService);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mService = null;
        }
    };

    public FlexibleMioponService getService() {
        return mService;
    }

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

        ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        mFragmentPagerAdapter = new FlexibleFragmentPagerAdaper(getSupportFragmentManager());
        viewPager.setAdapter(mFragmentPagerAdapter);
        viewPager.setOffscreenPageLimit(3);
        // set initial fragment
        viewPager.setCurrentItem(1);

        CirclePageIndicator circleIndicator = (CirclePageIndicator) findViewById(R.id.titles);
        circleIndicator.setViewPager(viewPager);

        Intent serviceIntent = new Intent(FlexibleMioponActivity.this, FlexibleMioponService.class);
        bindService(serviceIntent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mService != null) {
            removeSwitchListener();
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

    private void setListener() {
        mService.setOnDebugOutputListener(this);
        mService.setOnSwitchListener(this);
        mService.setOnPacketLogListener(this);
    }

    @Override
    public void onCouponViewChange(final int couponRemaining) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Fragment fragment = mFragmentPagerAdapter.getFragment(1);
                if (fragment instanceof MainFragment) {
                    MainFragment mainFragment = (MainFragment) fragment;
                    mainFragment.setRemainingPacket(couponRemaining);
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mService != null) {
            unbindService(mConnection);
        }
    }

    private void removeSwitchListener() {
        Fragment fragment = mFragmentPagerAdapter.getFragment(1);
        if (fragment instanceof MainFragment) {
            MainFragment mainFragment = (MainFragment) fragment;
            mainFragment.removeSwitchListener();
        }
    }

    @Override
    public void onCouponStatusObtained(final boolean isEnabled) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Fragment fragment = mFragmentPagerAdapter.getFragment(1);
                if (fragment instanceof MainFragment) {
                    MainFragment mainFragment = (MainFragment) fragment;
                    mainFragment.setSwitch(isEnabled);
                }

                SettingMediator.getInstance().setChecked(getString(R.string.switch_high_speed), isEnabled);
            }
        });
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onFragmentInteraction(String str) {
        mService.changeCoupon(Boolean.parseBoolean(str));
    }

    @Override
    public void onPacketLogObtained(final PacketLogInfo packetLogInfo) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Fragment fragment = mFragmentPagerAdapter.getFragment(0);
                if (fragment instanceof PacketLogFragment) {
                    PacketLogFragment packetLogFragment = (PacketLogFragment) fragment;
                    packetLogFragment.setPacketLog(packetLogInfo);
                }
            }
        });
    }
}
