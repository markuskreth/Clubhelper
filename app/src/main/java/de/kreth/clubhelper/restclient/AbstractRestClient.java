package de.kreth.clubhelper.restclient;

import android.os.AsyncTask;

import java.io.Serializable;

import de.greenrobot.dao.AbstractDao;

/**
 * Created by markus on 30.08.15.
 */
public class AbstractRestClient<T extends Serializable> extends AsyncTask<T, Void, T> {

    private final String USER_AGENT = "Mozilla/5.0";

    private static final String baseUrl = "http://10.0.2.2:8080/clubhelperbackend/";
    private static final String productiveUrl = "http://markuskreth.kreinacke.de:8080/ClubHelperBackend/";

    private final JsonMapper<T> gson;
    private AbstractDao<T, Long> dao;
    private Class<T> classType;

    public AbstractRestClient(AbstractDao<T, Long> dao, Class<T> classType) {
        this.dao = dao;
        this.classType = classType;
        gson = new JsonMapper<>();
    }

    @Override
    protected T doInBackground(T... params) {

        return null;
    }
}
