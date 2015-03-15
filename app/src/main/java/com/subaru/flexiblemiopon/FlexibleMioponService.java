package com.subaru.flexiblemiopon;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.subaru.flexiblemiopon.data.AccessToken;
import com.subaru.flexiblemiopon.data.CouponInfo;
import com.subaru.flexiblemiopon.data.PacketLogInfo;
import com.subaru.flexiblemiopon.data.TokenIO;
import com.subaru.flexiblemiopon.util.Mediator;
import com.subaru.flexiblemiopon.util.task.SimpleTaskExecutor;
import com.subaru.flexiblemiopon.util.task.TaskExecutor;

import static com.subaru.flexiblemiopon.util.Constant.CALLBACK_URI;
import static com.subaru.flexiblemiopon.util.Constant.REDIRECT_URI_BASE;

import java.util.HashMap;
import java.util.Map;

public class FlexibleMioponService extends Service {

    private final IBinder mBinder = new LocalBinder();
    public class LocalBinder extends Binder {
        FlexibleMioponService getService() {
            return FlexibleMioponService.this;
        }
    }

    private final String LOG_TAG = "FlexibleMioponService";

    private OnViewOperationListener mDebugListener;
    private OnSwitchListener mSwitchListener;
    private OnPacketLogListener mPacketLogListener;
    CouponInfo mCouponInfo;
    private TaskExecutor mTaskExecutor;
    private Toast mToast;

    private Mediator mMediator;

    public void setMediator(Mediator mediator) {
        mMediator = mediator;
    }

    public void setOnDebugOutputListener(OnViewOperationListener listener) {
        mDebugListener = listener;
    }

    public void setOnSwitchListener(OnSwitchListener listener) {
        mSwitchListener = listener;
    }

    public void setOnPacketLogListener(OnPacketLogListener listener) {
        mPacketLogListener = listener;
    }

    /**
     * Check if AccessToken is valid.
     * If it's invalid then obtain it.
     */
    public void Authenticate() {
        Map<String, AccessToken> tokenMap = readExistingToken();
        if (!isTokenAvailable(tokenMap)) {
            redirectForAuthentication();
        } else {
            retrieveCouponInfo(tokenMap);
            checkPacketLog(tokenMap);
        }
    }

    /**
     * Write AccessToken to local storage
     * @param token token to be write
     */
    private void writeToken(AccessToken token) {
        TokenIO io = new TokenIO(getApplicationContext());
        io.writeAccessToken(token.getAccessToken(), token);
    }

    /**
     * Read exinting AccessToken from local storage
     * @return Map of tokens
     */
    private Map<String, AccessToken> readExistingToken() {
        Map<String, AccessToken> tokenMap = new HashMap<>();
        TokenIO io = new TokenIO(getApplicationContext());
        for (String tokenFileString : io.readAccessTokenSet()) {
            tokenMap.put(tokenFileString, io.readAccessToken(tokenFileString));
        }
        return tokenMap;
    }

    /**
     * Redirect to IIJ's website and request to input information.
     */
    public void redirectForAuthentication() {
        String redirectUri = resolveUri();
        Uri uri = Uri.parse(redirectUri);
        Intent i = new Intent(Intent.ACTION_VIEW, uri);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }

    private String resolveUri() {
        return REDIRECT_URI_BASE + "&state=" + createSession();
    }

    private String createSession() {
        return "hoge";
    }

    private boolean isTokenAvailable(Map<String, AccessToken> tokenMap) {
        if (tokenMap == null || tokenMap.size() == 0) {
            return false;
        }
        return true;
    }

    public void getTokenFromAuth(Intent intent) {
        Uri uri = intent.getData();
        Log.d(LOG_TAG, "calling callbackFromAuth" + uri.toString());
        // get an intent
        String action = intent.getAction();
        if (Intent.ACTION_VIEW.equals(action)) {
            if ((uri != null) && (uri.toString().startsWith(CALLBACK_URI))) {
                //setDebugText(uri.toString());
                Map<String, String> parameterMap = getFragmentParameterMap(uri);
                AccessToken token = new AccessToken(parameterMap.get("access_token"),
                        parameterMap.get("token_type"),
                        Integer.parseInt(parameterMap.get("expires_in")),
                        parameterMap.get("state"));

                // write token for use again without authentication
                writeToken(token);
            }
        }
    }

    private AccessToken getAccessToken(Map<String, AccessToken> tokenMap) {
        // FIXME if multiple token exists, required to be changed here.
        AccessToken token = null;
        for (AccessToken eachToken : tokenMap.values()) {
            token = eachToken;
        }
        return token;
    }

    /**
     * Change coupon On/Off
     * @param isCouponUse true -> On, false -> Off
     */
    public void changeCoupon(boolean isCouponUse) {

        // return if current coupon status is already what specified by argument
        if (mMediator.isChecked(getString(R.string.switch_high_speed)) == isCouponUse) {
            Log.d(LOG_TAG, "Ignore changeCoupon since it is already in that state");
            return;
        }

        Map<String, AccessToken> tokenMap = readExistingToken();

        AccessToken token = getAccessToken(tokenMap);
        mTaskExecutor.executeCouponChange(mCouponInfo, isCouponUse, token, new TaskExecutor.OnCouponChangedListener() {
            @Override
            public void onCouponChanged(boolean isCouponUse) {
                // show toast or something?
                mToast.setText("coupon changed successfully");
                mToast.show();

                // set switch with the correct status. Please care, while coupon change is retrying again and again in background, Activity is closed and opened.
                // In this case, getCouponInfo will run and obtain the current status. And switch is changed to that state.
                mSwitchListener.onCouponStatusObtained(isCouponUse);
            }

            @Override
            public void onCouponChangeFailed(String reason) {
                Log.w(LOG_TAG, "Coupon change failed : " + reason);
            }

            @Override
            public void onAccessTokenInvalid() {
                Log.w(LOG_TAG, "Invalid token");
            }

            @Override
            public void onNotifyRetryLater() {
                mToast.setText("Coupon change rejected by IIJ side. I try it later, so just a moment please.");
                mToast.show();
            }
        });
    }

    public void retrieveCouponInfo() {
        retrieveCouponInfo(readExistingToken());
    }

    private void retrieveCouponInfo(Map<String, AccessToken> tokenMap) {
        if (tokenMap == null) {
            return;
        }
        AccessToken token = getAccessToken(tokenMap);
        mTaskExecutor.getCouponInfo(token, new TaskExecutor.OnCouponInfoObtainedListener() {
            @Override
            public void onCouponInfoObtained(CouponInfo couponInfo) {
                mCouponInfo = couponInfo;
                mSwitchListener.onCouponStatusObtained(mCouponInfo.getHdoInfoList().get(0).isCouponUsing());
                int couponRemaining = 0;
                for (CouponInfo.Coupon coupon : mCouponInfo.getCouponList()) {
                    couponRemaining += Integer.parseInt(coupon.getVolume());
                }
                for (CouponInfo.HdoInfo hdoInfo : mCouponInfo.getHdoInfoList()) {
                    for (CouponInfo.Coupon coupon : hdoInfo.getCouponList()) {
                        couponRemaining += Integer.parseInt(coupon.getVolume());
                    }
                }
                mDebugListener.onCouponViewChange(couponRemaining);
            }

            @Override
            public void onCouponInfoFailedToObtain(String reason) {

            }

            @Override
            public void onNotifyRetryLater() {
                mToast.setText("Coupon change rejected by IIJ side. I try it later, so just a moment please.");
                mToast.show();

            }
        });
    }

    public void checkPacketLog(Map<String, AccessToken> tokenMap) {
        if (tokenMap == null) {
            return;
        }
        AccessToken token = getAccessToken(tokenMap);
        mTaskExecutor.getPacketLog(token, new TaskExecutor.OnPacketLogObtainedListener() {
            @Override
            public void onPacketLogInfoObtained(PacketLogInfo packetLogInfo) {
                mPacketLogListener.onPacketLogObtained(packetLogInfo);

            }

            @Override
            public void onPacketLogFailedToObtain(String reason) {

            }

            @Override
            public void onAccessTokenInvalid() {

            }

            @Override
            public void onNotifyRetryLater() {

            }
        });
    }

    private Map<String, String> getFragmentParameterMap(Uri uri) {
        Map<String, String> queryMap = new HashMap<>();
        String encodedParams = uri.getFragment();
        String[] params = encodedParams.split("&");
        for (String eachParam : params) {
            String[] splittedParam = eachParam.split("=");
            queryMap.put(splittedParam[0], splittedParam[1]);
        }
        return queryMap;
    }

    private Runnable mOffRunnable = new Runnable() {
        @Override
        public void run() {
            changeCoupon(false);
        }
    };

    private BroadcastReceiver mScreenStatusReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Handler handler = new Handler();
            if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
                Log.d(LOG_TAG, "Screen is on");
                handler.removeCallbacks(mOffRunnable);
                changeCoupon(true);
            } else if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)){
                Log.d(LOG_TAG, "Screen is off");
                handler.postDelayed(mOffRunnable, 60000); // TODO Delay value should be read from user setting
            } else {
                Log.d(LOG_TAG, "Do not handle this intent from screen status receiver");
            }
        }
    };

    public void registerScreenOnOffReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(mScreenStatusReceiver, filter);
    }

    public void unregisterScreenOnOffReceiver() {
        unregisterReceiver(mScreenStatusReceiver);
    }

    @Override
    public void onCreate() {
        mTaskExecutor = new SimpleTaskExecutor();
        mToast = Toast.makeText(this, "Toast", Toast.LENGTH_LONG);
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new LocalBinder();
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    interface OnViewOperationListener {
        void onCouponViewChange(int couponRemaining);
    }

    interface OnSwitchListener {
        void onCouponStatusObtained(boolean isEnabled);
    }

    interface OnPacketLogListener {
        void onPacketLogObtained(PacketLogInfo packetLogInfo);
    }
}
