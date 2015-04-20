package com.subaru.flexiblemiopon.util.task.timer;

import android.os.CountDownTimer;
import android.util.Log;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Created by shiny_000 on 2015/03/21.
 */
public class MioponApiCountDownTimer implements MioponCountDownTimer {
    private final static String LOG_TAG = MioponApiCountDownTimer.class.getName();

    private long mRemainingWaitTimeMills;
    private int mRemainingRequestNum;
    private SimpleTimer mTimer;

    public MioponApiCountDownTimer(long millisInFuture, int requestNum) {
        this(millisInFuture, 1000, requestNum);
    }

    public MioponApiCountDownTimer(long millisInFuture, long countDownInterval, int requestNum) {
        initializeInternalTimer(millisInFuture, countDownInterval);

        mRemainingWaitTimeMills = millisInFuture;
    }

    private void initializeInternalTimer(final long millisInFuture, final long countDownInterval) {
        if (mTimer != null) {
            mTimer.cancel();
        }
        mTimer = new SimpleTimer(millisInFuture, countDownInterval) {
            @Override
            public void onTick(long millisUntilFinished) {
                onTimerTick(millisUntilFinished);
            }

            @Override
            public void onFinish() {
                onTimerFinish();
            }
        };
    }

    public void start() {
        if (mTimer != null) {
            mTimer.start();
        }
    }

    public void decrementRequestNum() {
        mRemainingRequestNum -= 1;
    }

    public long getRemainingWaitTimeMillis() {
        debugPrint();
        if (mRemainingRequestNum > 0) {
            return 0;
        }
        return mRemainingWaitTimeMills;
    }

    private void debugPrint() {
        Log.d(LOG_TAG, "mRemainingRequestNum : " + Integer.toString(mRemainingRequestNum));
        Log.d(LOG_TAG, "mRemainingWaitTimeMills : " + Long.toString(mRemainingWaitTimeMills));
    }

    public void onTimerTick(long l) {
        mRemainingWaitTimeMills = l;
        debugPrint();
    }

    public void onTimerFinish() {
        mTimer.cancel();
    }

    abstract private class SimpleTimer {
        private Runnable mTask;
        private ScheduledExecutorService mScheduler;
        private ScheduledFuture<?> mFuture;

        private long mMillis;
        private long mInterval;

        SimpleTimer(long millis, long interval) {
            mMillis = millis;
            mInterval = interval;
            mScheduler = Executors.newSingleThreadScheduledExecutor();
            mTask = new Runnable() {
                @Override
                public void run() {
                    mMillis -= mInterval;
                    if (mMillis > 0) {
                        onTick(mMillis);
                    } else {
                        onFinish();
                    }
                }
            };
        }

        public void start() {
            mFuture = mScheduler.scheduleAtFixedRate(
                    mTask,
                    mInterval,
                    mInterval,
                    TimeUnit.MILLISECONDS
            );
        }

        public void cancel() {
            if (mFuture != null) {
                mFuture.cancel(true);
            }
        }

        abstract void onTick(long remainingTimeMillis);
        abstract void onFinish();
    }
}
