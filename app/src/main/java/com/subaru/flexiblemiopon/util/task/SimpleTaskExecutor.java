package com.subaru.flexiblemiopon.util.task;

import android.util.Log;

import com.subaru.flexiblemiopon.data.AccessToken;
import com.subaru.flexiblemiopon.data.CouponInfo;
import com.subaru.flexiblemiopon.data.PacketLogInfo;
import com.subaru.flexiblemiopon.request.Command;
import com.subaru.flexiblemiopon.request.CouponChangeCommand;
import com.subaru.flexiblemiopon.request.CouponStatusCheckCommand;
import com.subaru.flexiblemiopon.request.PacketLogCheckCommand;
import com.subaru.flexiblemiopon.util.ResponseParser;

import org.json.JSONException;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static com.subaru.flexiblemiopon.Constant.DEVELOPER_ID;

/**
 * Created by shiny_000 on 2015/02/27.
 */
public class SimpleTaskExecutor implements TaskExecutor {
    private static final String LOG_TAG = SimpleTaskExecutor.class.getName();
    private TaskManager mManager = new SimpleTaskManager();
    ExecutorService mWorkerThread = Executors.newCachedThreadPool();
    private Future<Object> mCouponInfoFuture;
    private Future<Object> mCouponChangeFuture;
    private Future<Object> mPacketlogFuture;

    @Override
    public void getCouponInfo(final AccessToken token, final OnCouponInfoObtainedListener listener) {
        if (mCouponInfoFuture != null) {
            mCouponInfoFuture.cancel(true);
        }
        mCouponInfoFuture = mWorkerThread.submit(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                try {
                Future<String> future;

                    while(true) {
                        Command command = new CouponStatusCheckCommand(DEVELOPER_ID, token);
                        future = command.executeAsync(new Command.OnCommandExecutedListener() {
                            @Override
                            public void onCommandExecuted(String status, String response) {
                                if (status == null || "".equals(status)) {
                                    Log.w(LOG_TAG, "Status is null or blank");
                                    return;
                                }
                                if (response == null || "".equals(response)) {
                                    Log.w(LOG_TAG, "Response is null or brank");
                                    return;
                                }
                                switch (status) {
                                    case "200":
                                        Log.d(LOG_TAG, "Get coupon info correctly.");
                                        try {
                                            CouponInfo couponInfo = ResponseParser.parseCouponInfoResponse(response);
                                            listener.onCouponInfoObtained(couponInfo);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                            listener.onCouponInfoFailedToObtain("Parsing response string failed");
                                            Log.w(LOG_TAG, response);
                                        }
                                        break;
                                    case "403":
                                        Log.w(LOG_TAG, "Invalid header or you have to authorize again");
                                        listener.onCouponInfoFailedToObtain("Invalid header or you have to authorize again");
                                        break;
                                    case "405":
                                        Log.w(LOG_TAG, "Invalid Http method. Please check Http PUT is used.");
                                        listener.onCouponInfoFailedToObtain("Invalid Http method. Please check Http PUT is used.");
                                        break;
                                    case "413":
                                        Log.w(LOG_TAG, "Request is too large");
                                        listener.onCouponInfoFailedToObtain("Too large request");
                                        break;
                                    case "429":
                                        Log.w(LOG_TAG, "Too many requests:");
                                        Log.d(LOG_TAG, response);
                                        listener.onNotifyRetryLater();
                                        break;
                                    case "500":
                                        Log.w(LOG_TAG, "Error in server side");
                                        listener.onCouponInfoFailedToObtain("Error in server side");
                                        break;
                                    case "503":
                                        Log.w(LOG_TAG, "Server is in maintenance");
                                        listener.onCouponInfoFailedToObtain("Server is in maintenance");
                                        break;
                                    default:
                                        Log.w(LOG_TAG, "Unexpected response in getting coupon info. Ignore this task.");
                                        Log.w(LOG_TAG, response);
                                }
                            }
                        });
                        mManager.notifyCouponInfoCheckInvoked();

                        String response = future.get().split(":::")[0];
                        if (!response.equals("429")) {
                            break;
                        }

                        mManager.blockUntilCouponInfoCheckAvailable();
                    }
                } catch (InterruptedException e) {
                    Log.d(LOG_TAG, "Get coupon info task cancelled");
                } finally {
                    Log.d(LOG_TAG, "Get coupon info task finished");
                    return new Object();
                }
            }
        });
    }

    @Override
    public void executeCouponChange(final CouponInfo couponInfo, final boolean isCouponUse, final AccessToken token, final OnCouponChangedListener listener) {
        if (mCouponChangeFuture != null) {
            mCouponChangeFuture.cancel(true);
        }
        mCouponChangeFuture = mWorkerThread.submit(new Callable<Object>() {
            @Override
            public Object call() {
                try {
                    Future<String> future;

                    // Try coupon change again and again until coupon change succeeded
                    while(true) {
                        Command couponChangeCommand = new CouponChangeCommand(DEVELOPER_ID, token, couponInfo, isCouponUse);
                        future = couponChangeCommand.executeAsync(new Command.OnCommandExecutedListener() {
                            @Override
                            public void onCommandExecuted(String status, String response) {
                                if (status == null || "".equals(status)) {
                                    Log.w(LOG_TAG, "Response is null or blank");
                                    return;
                                }
                                switch (status) {
                                    case "200":
                                        Log.d(LOG_TAG, "Coupon changed correctly.");
                                        listener.onCouponChanged();
                                        break;
                                    case "400":
                                        Log.w(LOG_TAG, "Invalid json or content type");
                                        listener.onCouponChangeFailed("Invalid json or content type");
                                        break;
                                    case "403":
                                        Log.w(LOG_TAG, "Invalid header or you have to authorize again");
                                        listener.onCouponChangeFailed("Invalid header or you have to authorize again");
                                        //TODO implement here
                                        listener.onAccessTokenInvalid();
                                        break;
                                    case "405":
                                        Log.w(LOG_TAG, "Invalid Http method. Please check Http PUT is used.");
                                        listener.onCouponChangeFailed("Invalid Http method. Please check Http PUT is used.");
                                        break;
                                    case "429":
                                        Log.w(LOG_TAG, "Too many requests:");
                                        Log.d(LOG_TAG, status);
                                        listener.onNotifyRetryLater();
                                        break;
                                    case "500":
                                        Log.w(LOG_TAG, "Error in server side");
                                        listener.onCouponChangeFailed("Error in server side");
                                        break;
                                    case "503":
                                        Log.w(LOG_TAG, "Server is in maintenance");
                                        listener.onCouponChangeFailed("Server is in maintenance");
                                        break;
                                    default:
                                        Log.w(LOG_TAG, "Unexpected response in coupon change. Ignore this.");
                                        Log.w(LOG_TAG, status);
                                }
                                return;
                            }
                        });
                        // let manager know coupon change task is executed
                        mManager.notifyCouponChangeInvoked();

                        String response = future.get().split(":::")[0];
                        if (!response.equals("429")) {
                            break;
                        }

                        // block until coupon change available
                        mManager.blockUntilCouponChangeAvailable();
                    }
                } catch (InterruptedException e) {
                    Log.d(LOG_TAG, "Coupon change task cancelled");
                } finally {
                    Log.d(LOG_TAG, "Coupon change task finished");
                    return new Object();
                }
            }
        });
    }

    @Override
    public void getPacketLog(final CouponInfo couponInfo, final AccessToken token, final OnPacketLogObtainedListener listener) {
        if (mPacketlogFuture != null) {
            mPacketlogFuture.cancel(true);
        }
        mPacketlogFuture = mWorkerThread.submit(new Callable<Object>() {
            @Override
            public Object call() {
                try {
                    Future<String> future;

                    // Try coupon change again and again until coupon change succeeded
                    while(true) {

                        Command command = new PacketLogCheckCommand(DEVELOPER_ID, token);
                        future = command.executeAsync(new Command.OnCommandExecutedListener() {
                            @Override
                            public void onCommandExecuted(String status, String response) {
                                if (status == null || "".equals(status)) {
                                    Log.w(LOG_TAG, "Status is null or blank");
                                    return;
                                }
                                if (response == null || "".equals(response)) {
                                    Log.w(LOG_TAG, "Response is null or brank");
                                    return;
                                }
                                switch (status) {
                                    case "200":
                                        Log.d(LOG_TAG, "Get packetlog info correctly.");
                                        try {
                                            PacketLogInfo packetLogInfo = ResponseParser.parsePacketLogInfo(response);
                                            listener.onPacketLogInfoObtained(packetLogInfo);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                            listener.onPacketLogFailedToObtain("Parsing response string failed");
                                            Log.w(LOG_TAG, response);
                                        }
                                        break;
                                    case "403":
                                        Log.w(LOG_TAG, "Invalid header or you have to authorize again");
                                        listener.onPacketLogFailedToObtain("Invalid header or you have to authorize again");
                                        break;
                                    case "405":
                                        Log.w(LOG_TAG, "Invalid Http method. Please check Http Get is used.");
                                        listener.onPacketLogFailedToObtain("Invalid Http method. Please check Http GET is used.");
                                        break;
                                    case "429":
                                        Log.w(LOG_TAG, "Too many requests:");
                                        Log.d(LOG_TAG, response);
                                        listener.onNotifyRetryLater();
                                        break;
                                    case "500":
                                        Log.w(LOG_TAG, "Error in server side");
                                        listener.onPacketLogFailedToObtain("Error in server side");
                                        break;
                                    case "503":
                                        Log.w(LOG_TAG, "Server is in maintenance");
                                        listener.onPacketLogFailedToObtain("Server is in maintenance");
                                        break;
                                    default:
                                        Log.w(LOG_TAG, "Unexpected response in getting packetlog. Ignore this task.");
                                        Log.w(LOG_TAG, response);
                                }
                            }
                        });
                        mManager.notifyPacketLogCheckInvoked();

                        String response = future.get().split(":::")[0];
                        if (!response.equals("429")) {
                            break;
                        }

                        mManager.blockUntilPacketLogCheckAvailable();
                    }
                } catch (InterruptedException e) {
                    Log.d(LOG_TAG, "Get packetlog task cancelled");
                } finally {
                    Log.d(LOG_TAG, "Get packetlog task finished");
                    return new Object();
                }
            }
        });

    }
}
