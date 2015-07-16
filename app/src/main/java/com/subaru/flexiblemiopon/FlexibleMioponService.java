package com.subaru.flexiblemiopon;

import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.subaru.flexiblemiopon.data.AccessToken;
import com.subaru.flexiblemiopon.data.CouponInfo;
import com.subaru.flexiblemiopon.data.PacketLogInfo;
import com.subaru.flexiblemiopon.data.TokenIO;
import com.subaru.flexiblemiopon.util.FlexibleMioponToast;
import com.subaru.flexiblemiopon.util.Mediator;
import com.subaru.flexiblemiopon.util.SettingMediator;
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

    private OnViewOperationListener mDebugListener = new OnViewOperationListener() {
        @Override
        public void onCouponViewChange(int couponRemaining) {
            Log.d(LOG_TAG, "Default mDebugListener instance is used");
        }
    };

    private OnPacketLogListener mPacketLogListener = new OnPacketLogListener() {
        @Override
        public void onPacketLogObtained(PacketLogInfo packetLogInfo) {
            Log.d(LOG_TAG, "Default mPacketLogListener instance is used");
        }
    };

    CouponInfo mCouponInfo;
    private TaskExecutor mTaskExecutor;

    private Mediator mMediator;

    public void setMediator(Mediator mediator) {
        mMediator = mediator;
        FlexibleMioponToast.setMediator(mediator);
    }

    public void setOnDebugOutputListener(OnViewOperationListener listener) {
        mDebugListener = listener;
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
                FlexibleMioponToast.showToast("coupon changed successfully");
                mMediator.setChecked(getString(R.string.switch_high_speed), isCouponUse);

                if (mMediator.isChecked(getString(R.string.switch_change_basedon_screen))) {
                    // if notification is showing, internal text should be changed
                    toForegroundService();
                }
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
                FlexibleMioponToast.showToast("Change coupon rejected from IIJ side. This will automatically be retried.");
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
                mMediator.setChecked(getString(R.string.switch_high_speed), mCouponInfo.getHdoInfoList().get(0).isCouponUsing());
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
                FlexibleMioponToast.showToast("Couopon information cannot be obtained. This will automatically be retried.");
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
        Handler mHandler = new Handler();

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
                Log.d(LOG_TAG, "Screen is on");
                mHandler.removeCallbacks(mOffRunnable);
                changeCoupon(true);
            } else if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)){
                Log.d(LOG_TAG, "Screen is off");
                mHandler.postDelayed(mOffRunnable, 60000); // TODO Delay value should be read from user setting
            } else {
                Log.d(LOG_TAG, "Do not handle this intent from screen status receiver");
            }
        }
    };

    public void registerScreenOnOffReceiver() {
        Log.d(LOG_TAG, "registerScreenOnOffReceiver");
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(mScreenStatusReceiver, filter);
    }

    public void unregisterScreenOnOffReceiver() {
        try {
            if (mScreenStatusReceiver != null) {
                unregisterReceiver(mScreenStatusReceiver);
            }
        } catch (IllegalArgumentException e) {
            Log.d(LOG_TAG, "Receiver not registered");
        }
    }

    public void toForegroundService() {

        Intent notificationIntent = new Intent(this, FlexibleMioponActivity.class);
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, notificationIntent, 0);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplicationContext());
        notificationBuilder.setContentIntent(pendingIntent);
        notificationBuilder.setTicker("FlexibleMiopon");
        notificationBuilder.setContentTitle("FlexibleMiopon");
        if (mMediator.isChecked(getString(R.string.switch_high_speed))) {
            notificationBuilder.setContentText("Coupon is On");
        } else {
            notificationBuilder.setContentText("Coupon is Off");
        }
        notificationBuilder.setSmallIcon(R.drawable.notification);

        startForeground(R.string.app_name, notificationBuilder.build());
    }

    public void toBackgroundService() {
        stopForeground(true);
    }

    @Override
    public void onCreate() {
        mTaskExecutor = new SimpleTaskExecutor();
        FlexibleMioponToast.setContext(this);
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(LOG_TAG, "onStartCommand");
        Map<String, AccessToken> tokenMap = readExistingToken();
        if (!isTokenAvailable(tokenMap)) {
            return super.onStartCommand(intent, flags, startId);
        }
        // set mCouponInfo from existing file
        retrieveCouponInfo();

        SettingMediator mediator = SettingMediator.getInstance();
        setMediator(mediator);
        mediator.setService(this);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        toBackgroundService();
        unregisterScreenOnOffReceiver();
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

    interface OnPacketLogListener {
        void onPacketLogObtained(PacketLogInfo packetLogInfo);
    }
}
