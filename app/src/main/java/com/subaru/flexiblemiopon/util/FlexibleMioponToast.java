package com.subaru.flexiblemiopon.util;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.subaru.flexiblemiopon.R;

/**
 * Created by shiny_000 on 2015/03/15.
 */
public class FlexibleMioponToast {
    private static Context mContext;
    private static Mediator mMediator;
    private static Toast mToast;
    private static String LOG_TAG = FlexibleMioponToast.class.getName();

    public static void setContext(Context context) {
        mContext = context;
        mToast = Toast.makeText(mContext, "toast", Toast.LENGTH_LONG);
    }

    public static void setMediator(Mediator mediator) {
        mMediator = mediator;
    }

    public static void showToast(String text) {
        if (mContext == null) {
            Log.w(LOG_TAG, "FlexibleMioponToast does not have context. Ignore showing string:" + text);
            return;
        }
        if (mMediator != null) {
            if (!mMediator.isChecked(mContext.getString(R.string.switch_show_notification))) {
                Log.d(LOG_TAG, "Toast will not shown due to use setting");
                return;
            }
        }
        if (mToast != null) {
            mToast.cancel();
        }

        mToast.setText(text);
        mToast.show();
    }
}
