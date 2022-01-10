/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  okhttp3.Request
 *  okhttp3.Request$Builder
 *  org.json.JSONObject
 *  org.json.JSONTokener
 */
package net.frozenorb.hydrogen.connection;

import okhttp3.Request;
import org.json.JSONObject;
import org.json.JSONTokener;

public class RequestResponse {
    private boolean successful;
    private String errorMessage;
    private String response;
    Request.Builder requestBuilder;

    public boolean wasSuccessful() {
        return this.successful;
    }

    public JSONObject asJSONObject() {
        return new JSONObject(new JSONTokener(this.response));
    }

    public Request rebuildRequest() {
        return this.requestBuilder.build();
    }

    public boolean couldNotConnect() {
        return this.getErrorMessage() != null && this.getErrorMessage().toLowerCase().contains("could not connect to api");
    }

    public RequestResponse(boolean successful, String errorMessage, String response, Request.Builder requestBuilder) {
        this.successful = successful;
        this.errorMessage = errorMessage;
        this.response = response;
        this.requestBuilder = requestBuilder;
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }

    public String getResponse() {
        return this.response;
    }

    public Request.Builder getRequestBuilder() {
        return this.requestBuilder;
    }
}

