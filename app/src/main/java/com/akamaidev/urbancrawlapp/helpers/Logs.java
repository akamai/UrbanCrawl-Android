package com.akamaidev.urbancrawlapp.helpers;

import com.crashlytics.android.Crashlytics;

/**
 * Created by Aman Alam (@AmanAlam) on 12/4/17.
 */

public class Logs {

    public static void logException(Throwable e){
        e.printStackTrace();
        Crashlytics.logException(e);
    }
}
