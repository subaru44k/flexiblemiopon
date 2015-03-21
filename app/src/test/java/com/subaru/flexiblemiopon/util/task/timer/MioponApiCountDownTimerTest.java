package com.subaru.flexiblemiopon.util.task.timer;

import junit.framework.TestCase;

/**
 * Created by shiny_000 on 2015/03/21.
 */
public class MioponApiCountDownTimerTest extends TestCase {
    private MioponApiCountDownTimer mTimer;

    public void setUp() throws Exception {
        super.setUp();
        mTimer = new MioponApiCountDownTimer(1000, 100, 0);
        mTimer.start();
    }

    public void tearDown() throws Exception {
        mTimer = null;
    }

    public void testGetRemainingWaitTimeMillis() throws Exception {
        assertTrue(mTimer.getRemainingWaitTimeMillis() <= 1000);
        assertTrue(mTimer.getRemainingWaitTimeMillis() >= 0);
    }

    public void testOnTick() throws Exception {

        mTimer = new MioponApiCountDownTimer(1000, 1, 1);
        mTimer.start();

        mTimer.onTimerTick(100);
        assertEquals(0, mTimer.getRemainingWaitTimeMillis());
        mTimer.decrementRequestNum();

        assertEquals(100, mTimer.getRemainingWaitTimeMillis());
    }

    public void testOnFinish() throws Exception {
        mTimer.onTimerFinish();
        assertEquals(0, mTimer.getRemainingWaitTimeMillis());
    }

    public void testStart() throws Exception {
        mTimer.start();

        assertTrue(mTimer.getRemainingWaitTimeMillis() <= 1000);
        assertTrue(mTimer.getRemainingWaitTimeMillis() >= 0);
    }
}