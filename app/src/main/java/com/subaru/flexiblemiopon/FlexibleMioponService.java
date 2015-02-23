package com.subaru.flexiblemiopon;

import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.subaru.flexiblemiopon.data.AccessToken;
import com.subaru.flexiblemiopon.data.CouponInfo;
import com.subaru.flexiblemiopon.data.PacketLogInfo;
import com.subaru.flexiblemiopon.data.TokenIO;
import com.subaru.flexiblemiopon.request.Command;
import com.subaru.flexiblemiopon.request.CouponChangeCommand;
import com.subaru.flexiblemiopon.request.CouponStatusCheckCommand;
import com.subaru.flexiblemiopon.request.CouponUsageCheckCommand;
import com.subaru.flexiblemiopon.util.ResponseParser;

import org.json.JSONException;

import java.util.HashMap;
import java.util.List;
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
    private static final String REDIRECT_URI_BASE = "https://api.iijmio.jp/mobile/d/v1/authorization/?response_type=token&client_id=lNuh3hfMUS52SCTHv4O&redirect_uri=com.subaru.flexiblemiopon://callback";

    private OnViewOperationListener mDebugListener;
    private OnSwitchListener mSwitchListener;
    CouponInfo mCouponInfo;
    PacketLogInfo mPacketLogInfo;

    public void setOnDebugOutputListener(OnViewOperationListener listener) {
        mDebugListener = listener;
    }

    public void setOnSwitchListener(OnSwitchListener listener) {
        mSwitchListener = listener;
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
            checkCouponUsage(tokenMap);
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
        // インテントを取得
        String action = intent.getAction();
        if (Intent.ACTION_VIEW.equals(action)) {
            if ((uri != null) && (uri.toString().startsWith(CALLBACK_URI))) {
                //setDebugText(uri.toString());
                Map<String, String> parameterSet = getFragmentParameterMap(uri);
                AccessToken token = new AccessToken(parameterSet.get("access_token"),
                        parameterSet.get("token_type"),
                        Integer.parseInt(parameterSet.get("expires_in")),
                        parameterSet.get("state"));

                // write token for use again without authentication
                writeToken(token);
            }
        }
    }

    public void changeCoupon(boolean isCouponUse) {
        Map<String, AccessToken> tokenMap = readExistingToken();

        AccessToken token = null;
        for (AccessToken eachToken : tokenMap.values()) {
            token = eachToken;
        }
        Command couponChangeCommand = new CouponChangeCommand(DEVELOPER_ID, token, mCouponInfo, isCouponUse);
        couponChangeCommand.executeAsync(new Command.OnCommandExecutedListener() {
            @Override
            public void onCommandExecuted(String response) {
                // TODO check response code and change the switch. If it failed, set timer to execute later.
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

        // FIXME if multiple token exists, required to be changed here.
        AccessToken token = null;
        for (AccessToken eachToken : tokenMap.values()) {
            token = eachToken;
        }
        Command command = new CouponStatusCheckCommand(DEVELOPER_ID, token);

        String response = command.executeAsync(new Command.OnCommandExecutedListener() {
            @Override
            public void onCommandExecuted(String response) {
                Log.d(LOG_TAG, response);

                try {
                    mCouponInfo = ResponseParser.parseCouponInfoResponse(response);
                    mSwitchListener.onCouponStatusObtained(mCouponInfo.getHdoInfoList().get(0).isCouponUsing());
                    TextView view = new TextView(getApplicationContext());
                    int couponRemaining = 0;
                    for (CouponInfo.Coupon coupon : mCouponInfo.getCouponList()) {
                        couponRemaining += Integer.parseInt(coupon.getVolume());
                    }
                    for (CouponInfo.HdoInfo hdoInfo : mCouponInfo.getHdoInfoList()) {
                        for (CouponInfo.Coupon coupon : hdoInfo.getCouponList()) {
                            couponRemaining += Integer.parseInt(coupon.getVolume());
                        }
                    }
                    view.setText(Integer.toString(couponRemaining));
                    view.setBackgroundColor(Color.GREEN);
                    mDebugListener.onCouponViewChange(view, 0);
                } catch (JSONException e) {
                    e.printStackTrace();
                    return;
                }
            }
        });
    }

    public void checkCouponUsage(Map<String, AccessToken> tokenMap) {
        if (tokenMap == null) {
            return;
        }

        // FIXME if multiple token exists, required to be changed here.
        AccessToken token = null;
        for (AccessToken eachToken : tokenMap.values()) {
            token = eachToken;
        }
        Command command = new CouponUsageCheckCommand(DEVELOPER_ID, token);

        String response = command.executeAsync(new Command.OnCommandExecutedListener() {
            @Override
            public void onCommandExecuted(String response) {
                Log.d(LOG_TAG, response);
                try {
                    mPacketLogInfo = ResponseParser.parsePacketLogInfo(response);

                    List<PacketLogInfo.HdoInfo.PacketLog> packetLogList = mPacketLogInfo.getHdoInfoList().get(0).getPacketLogList();
                    RelativeLayout layout = new RelativeLayout(getApplicationContext());
                    RelativeLayout.LayoutParams params;
                    int i=0;
                    for (PacketLogInfo.HdoInfo.PacketLog info : packetLogList) {
                        i++;
                        TextView withCouponView = new TextView(getApplicationContext());
                        withCouponView.setText(info.getWithCoupon());
                        withCouponView.setBackgroundColor(Color.BLUE);
                        mDebugListener.onCouponViewChange(withCouponView, i);

//                        i++;
//                        TextView withoutCouponView = new TextView(getApplicationContext());
//                        withoutCouponView.setText(info.getWithoutCoupon());
//                        withoutCouponView.setBackgroundColor(Color.GRAY);
//                        mDebugListener.onCouponViewChange(withoutCouponView, i);

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
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

    interface OnViewOperationListener {
        public void onDebugRequest(String str);
        public void onCouponViewChange(View view, int i);
    }

    interface OnSwitchListener {
        public void onCouponStatusObtained(boolean isEnabled);
    }
}
