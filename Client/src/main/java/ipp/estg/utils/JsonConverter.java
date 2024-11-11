package ipp.estg.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

public class JsonConverter {
    private static final Gson gson = new Gson();

    public <T> String toJson(List<T> list) {
        return gson.toJson(list);
    }

    public <T> List<T> fromJson(String json, Class<T> classOfT) {
        return gson.fromJson(json,
                TypeToken.getParameterized(List.class, classOfT).getType());
    }
}