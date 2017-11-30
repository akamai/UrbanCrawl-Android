package com.akamaidev.urbancrawlapp.helpers;

import android.content.Context;

import com.akamai.android.sdk.VocService;

/**
 * Created by Aman Alam (@AmanAlam) on 10/23/17.
 */

public class Analytics {

//    public static final String EVENT


    public static void logEvent(Context ctx, String event){
        VocService vocService = VocService.createVocService(ctx);
        vocService.logEvent(event);
    }

    public static void logStartEvent(Context ctx, String event){
        VocService vocService = VocService.createVocService(ctx);
        vocService.startEvent(event);
    }

    public static void logStopEvent(Context ctx, String event){
        VocService vocService = VocService.createVocService(ctx);
        vocService.stopEvent(event);
    }
}
