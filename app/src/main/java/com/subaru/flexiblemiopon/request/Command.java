package com.subaru.flexiblemiopon.request;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.subaru.flexiblemiopon.data.AccessToken;

/**
 * Created by shiny_000 on 2015/02/21.
 */
abstract public class Command {
    protected final String REQUEST_URI = "https://api.iijmio.jp/mobile/d/v1/coupon/";
    protected String mDeveloperId;
    protected AccessToken mAccessToken;

    Command(String developerId, AccessToken token) {
        this.mDeveloperId = developerId;
        this.mAccessToken = token;
    }

    ExecutorService pool = Executors.newCachedThreadPool();

    public Future<String> executeAsync(final OnCommandExecutedListener listener) {
        Future<String> future = pool.submit(new Callable<String>() {
            @Override
            public String call() throws Exception {
                String response = execute();
                listener.onCommandExecuted(response);
                return response;
            }
        });
        return future;
    }

    abstract protected String execute();

    abstract public static class OnCommandExecutedListener {
        public abstract void onCommandExecuted(String response);
    }
}
