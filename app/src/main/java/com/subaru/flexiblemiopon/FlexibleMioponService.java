package com.subaru.flexiblemiopon;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.subaru.flexiblemiopon.data.AccessToken;
import com.subaru.flexiblemiopon.request.Command;
import com.subaru.flexiblemiopon.request.CouponChangeCommand;

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
    private static final String CALLBACK_URI = "com.subaru.flexiblemiopon://callback";
    private static final String DEVELOPER_ID = "lNuh3hfMUS52SCTHv4O";

    private OnDebugOutputListener mDebugListener;
    private OnAuthenticationListener mAuthenticationListener;

    public void setOnDebugOutputListener(OnDebugOutputListener listener) {
        mDebugListener = listener;
    }

    public void setOnAuthenticationListener(OnAuthenticationListener listener) {
        mAuthenticationListener = listener;
    }

    public void checkAuthentication() {
        if (!isTokenAvailable()) {

        }
    }

    private boolean isTokenAvailable() {
        return false;
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
                Command command = new CouponChangeCommand(DEVELOPER_ID,
                        new AccessToken(parameterSet.get("access_token"),
                                parameterSet.get("token_type"),
                                10000,
                                parameterSet.get("state")));

                String response = command.executeAsync(new Command.OnCommandExecutedListener() {
                    @Override
                    public void onCommandExecuted(String response) {
                        Log.d(LOG_TAG, response);
                        mDebugListener.onDebugRequest(response);
                    }
                });
            }
        }
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

    interface OnAuthenticationListener {
        public void onAuthenticationRequest();
    }
}
