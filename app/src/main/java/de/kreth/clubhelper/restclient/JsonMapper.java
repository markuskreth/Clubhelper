package de.kreth.clubhelper.restclient;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

/**
 * Created by markus on 27.08.15.
 */
public class JsonMapper<T> {
    private final Gson gson = new GsonBuilder().setPrettyPrinting().setDateFormat("dd/MM/yyyy HH:mm:ss.SSS Z").create();

    public String toJson(T src) {
        return gson.toJson(src);
    }

    public T fromJson(String json, Class<T> classOfT) throws JsonSyntaxException {
        return gson.fromJson(json, classOfT);
    }
}
