package ipp.estg.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

public class JsonConverter {
    private static final Gson gson = new Gson();

    public <T> String toJson(List<T> list) {
        return gson.toJson(list);
    }

    public <T> List<T> fromJsonToList(String json, Class<T> classOfT) {
        if (json == null || json.isEmpty() || json.equals("[]")) {
            return List.of();
        }
        return gson.fromJson(json,
                TypeToken.getParameterized(List.class, classOfT).getType());
    }

    public <T> T fromJsonToObject(String json, Class<T> classOfT) {
        if (json == null || json.isEmpty()) {
            return null;
        }
        return gson.fromJson(json, classOfT);
    }
}