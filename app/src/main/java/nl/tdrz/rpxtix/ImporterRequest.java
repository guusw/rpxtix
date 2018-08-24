package nl.tdrz.rpxtix;

import android.support.annotation.Nullable;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ImporterRequest extends StringRequest {
    public static final int VERSION_CODE_DEFAULT = 678;
    public static final String VERSION_NAME_DEFAULT = "5.5.8";

    public ImporterRequest(int method, String url, Response.Listener<String> listener, @Nullable Response.ErrorListener errorListener) {
        super(method, url, listener, errorListener);
    }

    public static String GetUserAgent(String versionName, int versionCode) {
        return String.format("rpx_android/%s:%d", versionName, versionCode);
    }

    public static String GetUserAgentDefault() {
        return GetUserAgent(VERSION_NAME_DEFAULT, VERSION_CODE_DEFAULT);
    }

    public static String GetCallerVersion(String versionName, int versionCode) {
        return String.format("%s-%d", versionName, versionCode);
    }

    public static String GetCallerVersionDefault() {
        return GetCallerVersion(VERSION_NAME_DEFAULT, VERSION_CODE_DEFAULT);
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String,String> headers = new HashMap<>();
        headers.put("User-Agent", GetUserAgentDefault());

        UUID requestID = UUID.randomUUID();

        headers.put("X-Request-Id", requestID.toString());
        headers.put("X-Caller-ID", "rpx_android");
        headers.put("X-Caller-Version", GetCallerVersionDefault());
        return headers;
    }
}
