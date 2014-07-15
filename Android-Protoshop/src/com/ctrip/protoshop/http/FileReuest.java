package com.ctrip.protoshop.http;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.ctrip.protoshop.util.Util;

public class FileReuest extends Request<String> {
    private static final int FILE_TIME_OUT = 1000;
    private static final int FILE_MAX_RETRIES = 2;
    private static final float FILE_BACKOFF_MULT = 2f;

    private static final Object sDecodeLock = new Object();

    private String defaultZipName = "default.zip";

    private Listener<String> mListener;

    public FileReuest(int method, String url, Listener<String> listener, ErrorListener errorListener) {
        super(method, url, errorListener);
        setRetryPolicy(new DefaultRetryPolicy(FILE_TIME_OUT, FILE_MAX_RETRIES, FILE_BACKOFF_MULT));
        mListener = listener;
    }

    public FileReuest(String url, Response.Listener<String> listener, ErrorListener errorListener) {
        this(Method.POST, url, listener, errorListener);
    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        // Serialize all decode on a global lock to reduce concurrent heap usage.
        synchronized (sDecodeLock) {
            try {
                return doParse(response);
            } catch (OutOfMemoryError e) {
                VolleyLog.e("Caught OOM for %d byte ZipFile, url=%s", response.data.length, getUrl());
                return Response.error(new ParseError(e));
            }
        }
    }

    private Response<String> doParse(NetworkResponse response) {
        byte[] bytes = response.data;
        File file;
        if (bytes.length > 0) {
            try {
                file = new File(Util.getUserRootFile(), defaultZipName);
                if (!file.exists()) {
                    file.createNewFile();
                }
                FileOutputStream outputStream = new FileOutputStream(file);
                outputStream.write(bytes);
                outputStream.flush();
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
                return Response.error(new VolleyError("Create ZipFile Error!"));
            }
        } else {
            VolleyLog.e("Zip File does not exist,url=%s", response.data.length, getUrl());
            return Response.error(new VolleyError("Download the file does not exist!"));
        }
        return Response.success(file.getAbsolutePath(), HttpHeaderParser.parseCacheHeaders(response));
    }

    @Override
    protected void deliverResponse(String response) {
        mListener.onResponse(response);
    }

}
