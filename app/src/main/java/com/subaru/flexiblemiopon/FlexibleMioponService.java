package com.subaru.flexiblemiopon;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.subaru.flexiblemiopon.data.AccessToken;
import com.subaru.flexiblemiopon.data.CouponInfo;
import com.subaru.flexiblemiopon.request.Command;
import com.subaru.flexiblemiopon.request.CouponChangeCommand;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class FlexibleMioponService extends Service {

    private AccessToken mToken;

    private final IBinder mBinder = new LocalBinder();
    public class LocalBinder extends Binder {

        FlexibleMioponService getService() {
            return FlexibleMioponService.this;
        }
    }

    private final String LOG_TAG = "FlexibleMioponService";
    private static final String CALLBACK_URI = "com.subaru.flexiblemiopon://callback";
    private static final String DEVELOPER_ID = "lNuh3hfMUS52SCTHv4O";
    private static final String REDIRECT_URI_BASE = "https://api.iijmio.jp/mobile/d/v1/authorization/?response_type=token&client_id=lNuh3hfMUS52SCTHv4O&redirect_uri=com.subaru.flexiblemiopon://callback";

    private OnDebugOutputListener mDebugListener;
    private OnSwitchListener mSwitchListener;

    public void setOnDebugOutputListener(OnDebugOutputListener listener) {
        mDebugListener = listener;
    }

    public void setOnSwitchListener(OnSwitchListener listener) {
        mSwitchListener = listener;
    }

    public void checkAuthentication() {
        if (!isTokenAvailable()) {
            redirectForAuthentication();
        }
    }

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

    private boolean isTokenAvailable() {
        return mToken == null ? false : true;
    }

    public void getTokenFromAuth(Intent intent) {
        Uri uri = intent.getData();
        Log.d(LOG_TAG, "calling callbackFromAuth" + uri.toString());
        // インテントを取得
        String action = intent.getAction();
        if (Intent.ACTION_VIEW.equals(action)) {
            if ((uri != null) && (uri.toString().startsWith(CALLBACK_URI))) {
                //setDebugText(uri.toString());
                Map<String, String> parameterSet = getFragmentParameterMap(uri);
                AccessToken token = new AccessToken(parameterSet.get("access_token"),
                        parameterSet.get("token_type"),
                        10000,
                        parameterSet.get("state"));
                storeToken(token);
            }
        }
    }

    public void retrieveCouponInfo() {
        if (mToken == null) {
            return;
        }

        Command command = new CouponChangeCommand(DEVELOPER_ID, mToken);

        String response = command.executeAsync(new Command.OnCommandExecutedListener() {
            @Override
            public void onCommandExecuted(String response) {
                Log.d(LOG_TAG, response);

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    CouponInfo.HdoInfo hdoInfo = new CouponInfo.HdoInfo.HdoInfoBuilder()
                            .setCouponUse(true)
                            .build();
                    CouponInfo couponInfo = new CouponInfo.CouponBuilder()
                            .setHddServiceCode(jsonObject.getString("couponInfo"))
                            .setHdoInfo(hdoInfo)
                            .build();
                    mSwitchListener.onCouponStatusObtained(true);
                } catch (JSONException e) {
                    e.printStackTrace();
                    return;
                }
            }
        });
        mDebugListener.onDebugRequest(response);
    }

    private void storeToken(AccessToken token) {
        mToken = token;
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

    @Override
    public void onCreate() {
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

    interface OnDebugOutputListener {
        public void onDebugRequest(String str);
    }

    interface OnSwitchListener {
        public void onCouponStatusObtained(boolean isEnabled);
    }
}
