package com.akamaidev.urbancrawlapp.helpers;

import android.content.Context;

import com.akamai.android.sdk.VocService;

/*
 * Copyright 2017 Akamai Technologies, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

public class Analytics {

    public static void logEvent(Context ctx, String event){
        // MAP SDK Usage : Using Log Event
        VocService vocService = VocService.createVocService(ctx);
        vocService.logEvent(event);
    }

    public static void logStartEvent(Context ctx, String event){
        // MAP SDK Usage : Using Timed Event - Start
        VocService vocService = VocService.createVocService(ctx);
        vocService.startEvent(event);
    }

    public static void logStopEvent(Context ctx, String event){
        // MAP SDK Usage : Using Timed Event - Stop
        VocService vocService = VocService.createVocService(ctx);
        vocService.stopEvent(event);
    }
}
