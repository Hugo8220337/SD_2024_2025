package ipp.estg.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Utility class for converting objects to and from JSON format.
 * This class uses the Gson library to handle the conversion of objects and lists to JSON and vice versa.
 */
public class JsonConverter {

    /**
     * The Gson object used for JSON serialization and deserialization.
     */
    private static final Gson gson = new Gson();

    /**
     * Converts a list of objects to its JSON representation.
     *
     * @param <T> The type of objects in the list.
     * @param list The list of objects to be converted to JSON.
     * @return A JSON string representing the list of objects.
     */
    public <T> String toJson(List<T> list) {
        return gson.toJson(list);
    }

    /**
     * Converts an object to its JSON representation.
     *
     * @param <T> The type of the object.
     * @param object The object to be converted to JSON.
     * @return A JSON string representing the object.
     */
    public <T> String toJson(T object) {
        return gson.toJson(object);
    }

    /**
     * Converts a JSON string to a list of objects of the specified type.
     *
     * @param <T> The type of the objects in the resulting list.
     * @param json The JSON string to be deserialized into a list.
     * @param classOfT The class of the type that the JSON string will be deserialized into.
     * @return A list of objects of type T represented by the JSON string.
     */
    public <T> List<T> fromJson(String json, Class<T> classOfT) {
        return gson.fromJson(json,
                TypeToken.getParameterized(List.class, classOfT).getType());
    }

    /**
     * Converts a JSON string to an object of the specified type.
     *
     * @param <T> The type of the object to be deserialized.
     * @param json The JSON string to be deserialized into an object.
     * @param type The type of the object to be deserialized.
     * @return An object of type T represented by the JSON string.
     */
    public <T> T fromJson(String json, Type type) {
        return gson.fromJson(json, type);
    }
}