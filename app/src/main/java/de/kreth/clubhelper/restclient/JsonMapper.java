package de.kreth.clubhelper.restclient;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import de.kreth.clubhelper.Data;
import de.kreth.clubhelper.SyncStatus;

/**
 * Created by markus on 27.08.15.
 */
public class JsonMapper {

    private final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .setDateFormat("dd/MM/yyyy HH:mm:ss.SSS Z")
            .setExclusionStrategies(new SyncStatusExclusionStrategy())
            .create();

    public <T> String toJson(T src) {
        return gson.toJson(src);
    }

    public <T>  T fromJson(String json, Class<T> classOfT) throws JsonSyntaxException {
        return gson.fromJson(json, classOfT);
    }

    private class SyncStatusExclusionStrategy implements ExclusionStrategy {

        @Override
        public boolean shouldSkipField(FieldAttributes f) {
            return f.getDeclaredClass().equals(SyncStatus.class);
        }

        @Override
        public boolean shouldSkipClass(Class<?> clazz) {
            return false;
        }
    }
}
