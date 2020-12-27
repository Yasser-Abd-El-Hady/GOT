package com.example.muhammed.advertiapp;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

/**
 * Created by Mu7ammed_A4raf on 25-Jan-18.
 */

public class RequestJsonObject extends StringRequest {


    public RequestJsonObject(int method, String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(method, url, listener, errorListener);
    }


//    @Override
//    protected Response<String> parseNetworkResponse(NetworkResponse response) {
//
//        try {
//            Cache.Entry cacheEntry = HttpHeaderParser.parseCacheHeaders(response);
//            if (cacheEntry == null) {
//                cacheEntry = new Cache.Entry();
//            }
//            final long cacheHitButRefreshed = 3 * 60 * 1000; // in 3 minutes cache will be hit, but also refreshed on background
//            final long cacheExpired = 24 * 60 * 60 * 1000; // in 24 hours this cache entry expires completely
//            long now = System.currentTimeMillis();
//            final long softExpire = now + cacheHitButRefreshed;
//            final long ttl = now + cacheExpired;
//            cacheEntry.data = response.data;
//            cacheEntry.softTtl = softExpire;
//            cacheEntry.ttl = ttl;
//            String headerValue;
//            headerValue = response.headers.get("Date");
//            if (headerValue != null) {
//                cacheEntry.serverDate = HttpHeaderParser.parseDateAsEpoch(headerValue);
//            }
//            headerValue = response.headers.get("Last-Modified");
//            if (headerValue != null) {
//                cacheEntry.lastModified = HttpHeaderParser.parseDateAsEpoch(headerValue);
//            }
//            cacheEntry.responseHeaders = response.headers;
//            final String jsonString = new String(response.data,
//                    HttpHeaderParser.parseCharset(response.headers));
//            return Response.success(new String(jsonString), cacheEntry);
//        } catch (UnsupportedEncodingException e) {
//            return Response.error(new ParseError(e));
//        }
//    }


    @Override
    protected void deliverResponse(String response) {
        super.deliverResponse(response);
    }


    @Override
    public void deliverError(VolleyError error) {
        super.deliverError(error);
    }

    @Override
    protected VolleyError parseNetworkError(VolleyError volleyError) {
        return super.parseNetworkError(volleyError);
    }
}
