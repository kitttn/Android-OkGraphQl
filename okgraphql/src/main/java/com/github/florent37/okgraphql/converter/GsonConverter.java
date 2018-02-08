package com.github.florent37.okgraphql.converter;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GsonConverter implements Converter {

    private final Gson gson;

    public GsonConverter(Gson gson) {
        this.gson = gson;
    }

    public GsonConverter() {
        this.gson = new Gson();
    }

    @Override
    public <T> BodyConverter<T> bodyConverter() {
        return new GsonBodyConverter<T>(gson);
    }

    public static class GsonBodyConverter<T> implements Converter.BodyConverter<T> {

        private final Gson gson;

        public GsonBodyConverter(Gson gson) {
            this.gson = gson;
        }

        @Override
        public T convert(String json, Class<T> classToCast, boolean toList) throws Exception {
            String dataJSon = null;
            try {
                JSONObject toJson = new JSONObject(json);
                JSONObject data = toJson.getJSONObject("data");

                boolean hasErrors = toJson.has("errors");
                if (hasErrors) {
                    JSONArray error = toJson.getJSONArray("errors");
                    String message = error.getJSONObject(0).getString("message");
                    throw new Exception(message);
                }
                dataJSon = data.toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (toList) {
                return null;
            } else {
                try {
                    return (T) gson.fromJson(dataJSon, classToCast);
                } catch (Exception e) {
                    throw new ClassCastException(e.getLocalizedMessage());
                }
            }
        }

    }
}
