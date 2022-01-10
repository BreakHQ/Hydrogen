/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  net.frozenorb.qlib.qLib
 *  okhttp3.MediaType
 *  okhttp3.Request$Builder
 *  okhttp3.RequestBody
 *  okhttp3.Response
 *  org.json.JSONException
 *  org.json.JSONObject
 *  org.json.JSONTokener
 */
package net.frozenorb.hydrogen.connection;

import com.google.common.collect.ImmutableMap;
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.util.Map;
import net.frozenorb.hydrogen.Hydrogen;
import net.frozenorb.hydrogen.Settings;
import net.frozenorb.hydrogen.connection.RequestResponse;
import net.frozenorb.hydrogen.util.HttpUtils;
import net.frozenorb.qlib.qLib;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

public class RequestHandler {
    private static long lastAPIError = 0L;
    private static long lastAPIRequest = 0L;
    private static boolean apiDown = false;
    private static long averageLatency;
    private static int averageLatencyTicks;
    private static long lastLatency;

    public static double getAverageLatency() {
        if (averageLatencyTicks == 0) {
            return -1.0;
        }
        return (double)averageLatency / (double)averageLatencyTicks;
    }

    public static RequestResponse get(String endpoint) {
        return RequestHandler.get(endpoint, null);
    }

    public static RequestResponse get(String endpoint, Map<String, Object> queryParameters) {
        Request.Builder builder = new Request.Builder();
        builder.get();
        builder.url(Settings.getApiHost() + endpoint + (queryParameters != null ? HttpUtils.generateQueryString(queryParameters) : ""));
        HttpUtils.authorize(builder);
        lastAPIRequest = System.currentTimeMillis();
        try {
            long start = System.currentTimeMillis();
            Response response = Hydrogen.getOkHttpClient().newCall(builder.build()).execute();
            if (response.code() >= 500) {
                apiDown = true;
                lastAPIError = System.currentTimeMillis();
                return new RequestResponse(false, "Could not connect to API", null, builder);
            }
            apiDown = false;
            lastLatency = System.currentTimeMillis() - start;
            averageLatency += System.currentTimeMillis() - start;
            ++averageLatencyTicks;
            String body = response.body().string();
            try {
                JSONObject object = new JSONObject(new JSONTokener(body));
                if (object.has("success") && !object.getBoolean("success")) {
                    return new RequestResponse(false, object.getString("message"), body, builder);
                }
            }
            catch (JSONException jSONException) {
                // empty catch block
            }
            return new RequestResponse(true, null, body, builder);
        }
        catch (ConnectException | UnknownHostException e) {
            apiDown = true;
            lastAPIError = System.currentTimeMillis();
            return new RequestResponse(false, "Could not connect to API", null, builder);
        }
        catch (Exception e) {
            e.printStackTrace();
            lastAPIError = System.currentTimeMillis();
            return new RequestResponse(false, "Failed to get response from API", null, builder);
        }
    }

    public static RequestResponse post(String endpoint, Map<String, Object> bodyParams) {
        Request.Builder builder = new Request.Builder();
        if (bodyParams != null) {
            for (Map.Entry<String, Object> entry : bodyParams.entrySet()) {
                if (!(entry.getValue() instanceof Double) || Double.isFinite((Double)entry.getValue())) continue;
                entry.setValue(0.0);
            }
        }
        String bodyJson = qLib.PLAIN_GSON.toJson(bodyParams == null ? ImmutableMap.of() : bodyParams);
        builder.post(RequestBody.create((MediaType)MediaType.parse((String)"application/json; charset=utf-8"), (String)bodyJson));
        builder.url(Settings.getApiHost() + endpoint);
        HttpUtils.authorize(builder);
        lastAPIRequest = System.currentTimeMillis();
        try {
            long start = System.currentTimeMillis();
            Response response = Hydrogen.getOkHttpClient().newCall(builder.build()).execute();
            if (response.code() >= 500) {
                apiDown = true;
                lastAPIError = System.currentTimeMillis();
                return new RequestResponse(false, "Could not connect to API", null, builder);
            }
            apiDown = false;
            lastLatency = System.currentTimeMillis() - start;
            averageLatency += System.currentTimeMillis() - start;
            ++averageLatencyTicks;
            String body = response.body().string();
            try {
                JSONObject object = new JSONObject(new JSONTokener(body));
                if (object.has("success") && !object.getBoolean("success")) {
                    return new RequestResponse(false, object.getString("message"), body, builder);
                }
            }
            catch (JSONException jSONException) {
                // empty catch block
            }
            return new RequestResponse(true, null, body, builder);
        }
        catch (ConnectException | UnknownHostException e) {
            apiDown = true;
            lastAPIError = System.currentTimeMillis();
            return new RequestResponse(false, "Could not connect to API", null, builder);
        }
        catch (Exception e) {
            e.printStackTrace();
            lastAPIError = System.currentTimeMillis();
            return new RequestResponse(false, "Failed to get response from API", null, builder);
        }
    }

    public static RequestResponse delete(String endpoint, Map<String, Object> bodyParams) {
        Request.Builder builder = new Request.Builder();
        String bodyJson = qLib.PLAIN_GSON.toJson(bodyParams == null ? ImmutableMap.of() : bodyParams);
        builder.delete(RequestBody.create((MediaType)MediaType.parse((String)"application/json; charset=utf-8"), (String)bodyJson));
        builder.url(Settings.getApiHost() + endpoint);
        HttpUtils.authorize(builder);
        lastAPIRequest = System.currentTimeMillis();
        try {
            long start = System.currentTimeMillis();
            Response response = Hydrogen.getOkHttpClient().newCall(builder.build()).execute();
            if (response.code() >= 500) {
                apiDown = true;
                lastAPIError = System.currentTimeMillis();
                return new RequestResponse(false, "Could not connect to API", null, builder);
            }
            apiDown = false;
            lastLatency = System.currentTimeMillis() - start;
            averageLatency += System.currentTimeMillis() - start;
            ++averageLatencyTicks;
            String body = response.body().string();
            try {
                JSONObject object = new JSONObject(new JSONTokener(body));
                if (object.has("success") && !object.getBoolean("success")) {
                    return new RequestResponse(false, object.getString("message"), body, builder);
                }
            }
            catch (JSONException jSONException) {
                // empty catch block
            }
            return new RequestResponse(true, null, body, builder);
        }
        catch (ConnectException | UnknownHostException e) {
            apiDown = true;
            lastAPIError = System.currentTimeMillis();
            return new RequestResponse(false, "Could not connect to API", null, builder);
        }
        catch (Exception e) {
            e.printStackTrace();
            lastAPIError = System.currentTimeMillis();
            return new RequestResponse(false, "Failed to get response from API", null, builder);
        }
    }

    public static RequestResponse send(Request.Builder builder) {
        lastAPIRequest = System.currentTimeMillis();
        try {
            long start = System.currentTimeMillis();
            Response response = Hydrogen.getOkHttpClient().newCall(builder.build()).execute();
            if (response.code() >= 500) {
                apiDown = true;
                lastAPIError = System.currentTimeMillis();
                return new RequestResponse(false, "Could not connect to API", null, builder);
            }
            apiDown = false;
            lastLatency = System.currentTimeMillis() - start;
            averageLatency += System.currentTimeMillis() - start;
            ++averageLatencyTicks;
            String body = response.body().string();
            try {
                JSONObject object = new JSONObject(new JSONTokener(body));
                if (object.has("success") && !object.getBoolean("success")) {
                    return new RequestResponse(false, object.getString("message"), body, builder);
                }
            }
            catch (JSONException jSONException) {
                // empty catch block
            }
            return new RequestResponse(true, null, body, builder);
        }
        catch (ConnectException | UnknownHostException e) {
            apiDown = true;
            lastAPIError = System.currentTimeMillis();
            return new RequestResponse(false, "Could not connect to API", null, builder);
        }
        catch (Exception e) {
            e.printStackTrace();
            lastAPIError = System.currentTimeMillis();
            return new RequestResponse(false, "Failed to get response from API", null, builder);
        }
    }

    public static long getLastAPIError() {
        return lastAPIError;
    }

    public static long getLastAPIRequest() {
        return lastAPIRequest;
    }

    public static boolean isApiDown() {
        return apiDown;
    }

    public static long getLastLatency() {
        return lastLatency;
    }

    static {
        averageLatencyTicks = 0;
    }
}

