package com.github.florent37.okgraphql;

import com.github.florent37.okgraphql.cache.Cache;
import com.github.florent37.okgraphql.converter.Converter;
import com.github.florent37.okgraphql.converter.GsonConverter;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by florentchampigny on 23/05/2017.
 */

public class OkGraphql {
    private String baseUrl;

    private OkHttpClient okHttpClient = new OkHttpClient();
    private Converter converter = new GsonConverter();
    private String acceptHeader = "application/json";
    private String contentTypeHeader = "application/json";
    private Cache cache;

    public Query<String> query(String query) {
        return new Query<>(this, query);
    }

    public Query<String> body(String query) {
        return new Query<>(this, null, query);
    }

    public Query<String> query(Field field) {
        final String query = field.toString();
        return new Query<>(this, query);
    }

    public Mutation<String> mutation(String query) {
        return new Mutation<>(this, query);
    }

    protected <T> void enqueue(final AbstractQuery abstractQuery) {
        try {
            Request.Builder builder = new Request.Builder()
                    .url(baseUrl)
                    .addHeader("accept", acceptHeader)
                    .addHeader("content-type", contentTypeHeader)
                    .post(RequestBody.create(MediaType.parse(contentTypeHeader), abstractQuery.getContent()));

            okHttpClient.newCall(builder.build())
                    .enqueue(new okhttp3.Callback() {
                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            final String json = response.body().string();
                            abstractQuery.onResponse(converter, json);
                        }

                        @Override
                        public void onFailure(Call call, IOException e) {
                            abstractQuery.onError(e);
                        }
                    });
        } catch (Exception e) {
            abstractQuery.onError(e);
        }
    }

    public static class Builder {

        private OkGraphql okGraphql;

        public Builder() {
            okGraphql = new OkGraphql();
        }

        public OkGraphql build() {
            return okGraphql;
        }

        public Builder converter(Converter converter) {
            okGraphql.converter = converter;
            return this;
        }

        @Deprecated
        public Builder cache(Cache cache) {
            okGraphql.cache = cache;
            return this;
        }

        public Builder okClient(OkHttpClient okHttpClient) {
            okGraphql.okHttpClient = okHttpClient;
            return this;
        }

        public Builder baseUrl(String baseUrl) {
            okGraphql.baseUrl = baseUrl;
            return this;
        }

        /**
         * Sets the Accept: header for all requests. Default is "application/json"
         * @param header Accept header, which defines data type for app to receive
         * @return Builder for chaining
         */
        public Builder acceptHeader(String header) {
            okGraphql.acceptHeader = header;
            return this;
        }

        /**
         * Sets the Content-Type: header for all requests. Default is "application/json"
         * @param header Content-Type header, which defines data type which app sends.
         * @return Builder for chaining
         */
        public Builder contentTypeHeader(String header) {
            okGraphql.contentTypeHeader = header;
            return this;
        }
    }
}
