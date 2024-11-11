package ipp.estg.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class JsonConverter {
    private static final Gson gson = new Gson();

    public <T> String toJson(List<T> list) {
        return gson.toJson(list);
    }

    public <T> String toJson(T object) {
        return gson.toJson(object);
    }

    public <T> List<T> fromJson(String json, Class<T> classOfT) {
        return gson.fromJson(json,
                TypeToken.getParameterized(List.class, classOfT).getType());
    }

    public <T> T fromJson(String json, Type type) {
        return gson.fromJson(json, type);
    }


}