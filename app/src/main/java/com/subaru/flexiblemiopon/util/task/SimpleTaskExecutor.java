package com.subaru.flexiblemiopon.util.task;

import android.util.Log;

import com.subaru.flexiblemiopon.data.AccessToken;
import com.subaru.flexiblemiopon.data.CouponInfo;
import com.subaru.flexiblemiopon.request.Command;
import com.subaru.flexiblemiopon.request.CouponChangeCommand;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
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
    private Future<Object> mCancelFuture;

    @Override
    public void executeCouponChange(final CouponInfo couponInfo, final boolean isCouponUse, final AccessToken token, final OnCouponChangedListener listener) {
        if (mCancelFuture != null) {
            mCancelFuture.cancel(true);
        }
        mCancelFuture = mWorkerThread.submit(new Callable<Object>() {
            @Override
            public Object call() {
                try {
                    Future<String> future;

                    // Try coupon change again and again until coupon change succeeded
                    while(true) {
                        Command couponChangeCommand = new CouponChangeCommand(DEVELOPER_ID, token, couponInfo, isCouponUse);
                        future = couponChangeCommand.executeAsync(new Command.OnCommandExecutedListener() {
                            @Override
                            public void onCommandExecuted(String response) {
                                if (response == null) {
                                    Log.w(LOG_TAG, "Response is null");
                                    return;
                                }
                                switch (response) {
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
                                        break;
                                    case "405":
                                        Log.w(LOG_TAG, "Invalid Http method. Please check Http PUT is used.");
                                        listener.onCouponChangeFailed("Invalid Http method. Please check Http PUT is used.");
                                        break;
                                    case "429":
                                        Log.w(LOG_TAG, "Too many requests");
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
                                        Log.w(LOG_TAG, response);
                                }
                                return;
                            }
                        });
                        // let manager know coupon change task is executed
                        mManager.notifyCouponChangeInvoked();

                        String response = future.get();
                        if (!response.equals("429")) {
                            break;
                        }

                        // block until coupon change available
                        mManager.blockUntilCouponChangeAvailable();
                    }
                } catch (InterruptedException e) {
                    Log.d(LOG_TAG, "Coupon change task cancelled");
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } finally {
                    Log.d(LOG_TAG, "Coupon change task finished");
                    return new Object();
                }
            }
        });
    }
}
