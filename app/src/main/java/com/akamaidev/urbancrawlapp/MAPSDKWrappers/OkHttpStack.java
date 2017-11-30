package com.akamaidev.urbancrawlapp.MAPSDKWrappers;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.HttpStack;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolVersion;
import org.apache.http.StatusLine;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Original source : https://gist.github.com/bryanstern/4e8f1cb5a8e14c202750
 */

/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Circle Internet Financial
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */




/**
 * OkHttp backed {@link com.android.volley.toolbox.HttpStack HttpStack} that does not
 * use okhttp-urlconnection
 */
public class OkHttpStack implements HttpStack {
    private final OkHttpClient client;

    public OkHttpStack(OkHttpClient client) {
        this.client = client;
    }

    @Override
    public HttpResponse performRequest(Request<?> request, Map<String, String> additionalHeaders)
            throws IOException, AuthFailureError {
        OkHttpClient.Builder clientBuilder = client.newBuilder();
        int timeoutMs = request.getTimeoutMs();
        clientBuilder.connectTimeout(timeoutMs, TimeUnit.MILLISECONDS);
        clientBuilder.readTimeout(timeoutMs, TimeUnit.MILLISECONDS);
        clientBuilder.writeTimeout(timeoutMs, TimeUnit.MILLISECONDS);
        OkHttpClient client = clientBuilder.build();

        okhttp3.Request.Builder requestBuilder = new okhttp3.Request.Builder();
        requestBuilder.url(request.getUrl());

        setHeaders(requestBuilder, request, additionalHeaders);
        setConnectionParameters(requestBuilder, request);

        okhttp3.Request okHttpRequest = requestBuilder.build();
        Call okHttpCall = client.newCall(okHttpRequest);
        Response okHttpResponse = okHttpCall.execute();

        StatusLine responseStatus =
                new BasicStatusLine(parseProtocol(okHttpResponse.protocol()), okHttpResponse.code(),
                        okHttpResponse.message());
        BasicHttpResponse response = new BasicHttpResponse(responseStatus);
        response.setEntity(getEntity(okHttpResponse));

        Headers responseHeaders = okHttpResponse.headers();
        for (int i = 0, len = responseHeaders.size(); i < len; i++) {
            response.addHeader(new BasicHeader(responseHeaders.name(i), responseHeaders.value(i)));
        }

        return response;
    }

    private static ProtocolVersion parseProtocol(Protocol p) {
        switch (p) {
            case HTTP_1_0:
                return new ProtocolVersion("HTTP", 1, 0);
            case HTTP_1_1:
                return new ProtocolVersion("HTTP", 1, 1);
            case SPDY_3:
                return new ProtocolVersion("SPDY", 3, 1);
            case HTTP_2:
                return new ProtocolVersion("HTTP", 2, 0);
        }
        throw new IllegalAccessError("Unknown protocol: " + p);
    }

    private static HttpEntity getEntity(Response response) throws IOException {
        BasicHttpEntity entity = new BasicHttpEntity();
        ResponseBody body = response.body();
        entity.setContent(body.byteStream());
        entity.setContentLength(body.contentLength());
        entity.setContentEncoding(response.header("Content-Encoding"));
        if (body.contentType() != null) {
            entity.setContentType(body.contentType().type());
        }
        return entity;
    }

    private static void setHeaders(okhttp3.Request.Builder builder, Request<?> request,
                                   Map<String, String> additionalHeaders) throws AuthFailureError {
        for (Map.Entry<String, String> entry : request.getHeaders().entrySet()) {
            builder.addHeader(entry.getKey(), entry.getValue());
        }
        for (Map.Entry<String, String> entry : additionalHeaders.entrySet()) {
            builder.addHeader(entry.getKey(), entry.getValue());
        }
    }

    private static void setConnectionParameters(okhttp3.Request.Builder builder, Request<?> request)
            throws AuthFailureError {
        switch (request.getMethod()) {
            case Request.Method.DEPRECATED_GET_OR_POST:
                // Ensure backwards compatibility. Volley assumes a request with a null body is a GET.
                byte[] postBody = request.getPostBody();
                if (postBody == null) {
                    builder.get();
                } else {
                    builder.post(
                            RequestBody.create(MediaType.parse(request.getPostBodyContentType()), postBody));
                }
                break;
            case Request.Method.GET:
                builder.get();
                break;
            case Request.Method.DELETE:
                builder.delete();
                break;
            case Request.Method.POST:
                builder.post(createRequestBody(request));
                break;
            case Request.Method.PUT:
                builder.put(createRequestBody(request));
                break;
            case Request.Method.HEAD:
                builder.head();
                break;
            case Request.Method.OPTIONS:
                builder.method("OPTIONS", null);
                break;
            case Request.Method.TRACE:
                builder.method("TRACE", null);
                break;
            case Request.Method.PATCH:
                builder.patch(createRequestBody(request));
                break;
            default:
                throw new IllegalStateException("Unknown method type.");
        }
    }

    private static RequestBody createRequestBody(Request r) throws AuthFailureError {
        byte[] body = r.getBody();
        if (body == null) {
            return null;
        }
        return RequestBody.create(MediaType.parse(r.getBodyContentType()), body);
    }
}