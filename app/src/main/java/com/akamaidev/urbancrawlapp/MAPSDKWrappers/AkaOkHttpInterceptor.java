package com.akamaidev.urbancrawlapp.MAPSDKWrappers;

import android.text.TextUtils;

import com.akamai.android.sdk.net.AkaURLConnection;
import com.akamai.android.sdk.net.AkaURLStreamHandler;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Map;

import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;
import okio.Sink;

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

/**
 * Created by vaanand on 11/15/16.
 * Usage: See AkaOkHttpClientSample for GET/POST samples.
 */
public class AkaOkHttpInterceptor implements Interceptor {

    private static final int CHUNK_SIZE = 4096;

    @Override
    public Response intercept(Chain chain) throws IOException {
        AkaURLConnection connection = openConnection(chain.request());
        prepareRequest(connection, chain.request());
        return readResponse(connection, chain.request());
    }

    protected AkaURLConnection openConnection(Request request) throws IOException {
        AkaURLConnection connection =
                (AkaURLConnection) new URL(null, request.url().toString(), new AkaURLStreamHandler()).openConnection();
        connection.setConnectTimeout(15000);
        connection.setReadTimeout(20000);
        return connection;
    }

    void prepareRequest(AkaURLConnection connection, Request request) throws IOException {
        connection.setRequestMethod(request.method());
        connection.setDoInput(true);

        Headers requestHeaders = request.headers();
        for (String key : requestHeaders.names()) {
            connection.setRequestProperty(key, requestHeaders.get(key));
        }

        RequestBody body = request.body();
        if (body != null) {
            connection.setDoOutput(true);
            if( body.contentType() != null ) {
                connection.setRequestProperty("Content-Type", body.contentType().toString());
            }
            long length = body.contentLength();
            if (length != -1) {
                connection.setFixedLengthStreamingMode((int) length);
                connection.setRequestProperty("Content-Length", String.valueOf(length));
            } else {
                connection.setChunkedStreamingMode(CHUNK_SIZE);
            }
            Sink sink = Okio.sink(connection.getOutputStream());
            BufferedSink bufferedSink = Okio.buffer(sink);
            body.writeTo(bufferedSink);
            bufferedSink.flush();
        }
    }

    Response readResponse(AkaURLConnection connection, Request request) throws IOException {
        int status = connection.getResponseCode();
        String reason = connection.getResponseMessage();
        if (reason == null) reason = ""; // HttpURLConnection treats empty reason as null.

        Headers.Builder responseHeaderBuilder = new Headers.Builder();
        for (Map.Entry<String, List<String>> field : ((Map<String, List<String>>)connection.getHeaderFields()).entrySet()) {
            String name = field.getKey();
            if (!TextUtils.isEmpty(name)) {
                for (String value : field.getValue()) {
                    if (!TextUtils.isEmpty(value)) {
                        responseHeaderBuilder.add(name, value);
                    }
                }
            }
        }

        String mimeType = connection.getContentType();
        int length = connection.getContentLength();
        InputStream stream;
        if (status >= 400) {
            stream = connection.getErrorStream();
        } else {
            stream = connection.getInputStream();
        }
        AkaResponseBody responseBody = new AkaResponseBody(mimeType, length, stream);
        Response response = new Response.Builder()
                .request(request)
                .headers(responseHeaderBuilder.build())
                .code(status)
                .message(reason)
                .protocol(Protocol.HTTP_1_1)
                .body(responseBody)
                .build();

        return response;
    }

    private static class AkaResponseBody extends ResponseBody {

        private final String mimeType;
        private final long length;
        private final InputStream stream;

        private AkaResponseBody(String mimeType, long length, InputStream stream) {
            this.mimeType = mimeType;
            this.length = length;
            this.stream = stream;
        }

        @Override
        public MediaType contentType() {
            return MediaType.parse(mimeType);
        }

        @Override
        public long contentLength() {
            return length;
        }

        @Override
        public BufferedSource source() {
            return Okio.buffer(Okio.source(stream));
        }
    }
}
