package com.exemplo.vizinhoajuda;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public final class SupabaseClient {

    public static final OkHttpClient HTTP_CLIENT = new OkHttpClient();
    public static final String BASE_URL = BuildConfig.SUPABASE_URL;
    public static final String ANON_KEY = BuildConfig.SUPABASE_ANON_KEY;

    private SupabaseClient() {
    }

    public static Request.Builder requestBuilder(String tablePath) {
        return new Request.Builder()
                .url(BASE_URL + "/rest/v1/" + tablePath)
                .addHeader("apikey", ANON_KEY)
                .addHeader("Authorization", "Bearer " + ANON_KEY)
                .addHeader("Accept", "application/json")
                .addHeader("Content-Type", "application/json");
    }

    public static RequestBody jsonBody(String json) {
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        return RequestBody.create(json, mediaType);
    }
}
