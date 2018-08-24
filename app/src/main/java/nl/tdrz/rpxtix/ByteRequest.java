package nl.tdrz.rpxtix;

import android.support.annotation.GuardedBy;
import android.support.annotation.Nullable;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;

import java.io.UnsupportedEncodingException;

public class ByteRequest extends Request<byte[]> {

    private final Response.Listener<byte[]> listener;

    public ByteRequest(int method, String url, Response.Listener<byte[]> listener1, @Nullable Response.ErrorListener listener) {
        super(method, url, listener);
        this.listener = listener1;
    }

    @Override
    @SuppressWarnings("DefaultCharset")
    protected Response<byte[]> parseNetworkResponse(NetworkResponse response) {
        return Response.success(response.data, HttpHeaderParser.parseCacheHeaders(response));
    }

    @Override
    protected void deliverResponse(byte[] response) {
        listener.onResponse(response);
    }
}
