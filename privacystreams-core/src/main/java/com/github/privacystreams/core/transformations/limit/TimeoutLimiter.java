package com.github.privacystreams.core.transformations.limit;

import java.util.ArrayList;
import java.util.List;

import com.github.privacystreams.core.MultiItemStream;
import com.github.privacystreams.core.Item;
import com.github.privacystreams.core.utils.Logging;

/**
 * Created by yuanchun on 28/11/2016.
 * limit the time of the function
 */
final class TimeoutLimiter extends StreamLimiter {
    private final long timeoutMillis;

    TimeoutLimiter(long timeoutMillis) {
        this.timeoutMillis = timeoutMillis;
        this.addParameters(timeoutMillis);
    }

    /*
     * Start the timer when the function starts evaluating
     */
    protected void init() {
        super.init();
        new TimeoutCancelTask(this.timeoutMillis).start();
    }

    @Override
    protected boolean keep(Item item) {
        return true;
    }

    /*
     * An idle task which simply sleeps some millis seconds.
     */
    private class TimeoutCancelTask extends Thread {
        private long millis;

        TimeoutCancelTask(long millis) {
            this.millis = millis;
        }

        @Override
        public void run() {
            try {
                long startTime = System.currentTimeMillis();
                while (!TimeoutLimiter.this.isCancelled) {
                    Thread.sleep(100);
                    long timeElapsed = System.currentTimeMillis() - startTime;
                    if (timeElapsed >= this.millis) break;
                }
            } catch (InterruptedException e) {
                Logging.warn("TimeoutLimiter failed.");
                e.printStackTrace();
            }
            TimeoutLimiter.this.cancel(TimeoutLimiter.this.getUQI());
        }
    }
}
