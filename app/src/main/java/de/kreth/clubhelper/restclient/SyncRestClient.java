package de.kreth.clubhelper.restclient;

import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.List;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.query.QueryBuilder;
import de.greenrobot.dao.query.WhereCondition;
import de.kreth.clubhelper.Adress;
import de.kreth.clubhelper.Attendance;
import de.kreth.clubhelper.Contact;
import de.kreth.clubhelper.Data;
import de.kreth.clubhelper.Person;
import de.kreth.clubhelper.Relative;
import de.kreth.clubhelper.dao.AdressDao;
import de.kreth.clubhelper.dao.ContactDao;
import de.kreth.clubhelper.dao.DaoSession;
import de.kreth.clubhelper.dao.PersonDao;
import de.kreth.clubhelper.dao.RelativeDao;

/**
 * Created by markus on 30.08.15.
 */
public class SyncRestClient extends AsyncTask<Void, Void, Void> {

    private final Gson gson = new GsonBuilder().setPrettyPrinting().setDateFormat("dd/MM/yyyy HH:mm:ss.SSS Z").create();

    private String uri;
    private final DaoSession session;
    private SyncFinishedListener listener = null;

    public SyncRestClient(DaoSession session, String uri) {
        this.session = session;
        this.uri = uri;
    }

    public SyncRestClient(DaoSession session, String uri, SyncFinishedListener listener) {
        this(session, uri);
        this.listener = listener;
    }

    @Override
    protected Void doInBackground(Void... params) {
        updateData(Person.class, Person[].class);
        updateData(Contact.class, Contact[].class);
        updateData(Adress.class, Adress[].class);
        updateData(Relative.class, Relative[].class);
        updateData(Attendance.class, Attendance[].class);
        return null;
    }

    private <T extends Data> void updateData(Class<T> classForType, Class<T[]> classForList) {

        AbstractDao<T, Long> dao = (AbstractDao<T, Long>) session.getDao(classForType);
        final String simpleName = classForType.getSimpleName();
        final String sqlRaw = "WHERE CHANGED=(select max(CHANGED) from " + simpleName.toUpperCase() + ")";
        List<T> datas = dao.queryRaw(sqlRaw);

        Date lastChange;
        if(datas.size()>0)
            lastChange = datas.get(0).getChanged();
        else
            lastChange = new Date(0L);

        T[] updated = loadUpdated(simpleName.toLowerCase(), lastChange, classForList);
        for (T c : updated) {
            dao.insertOrReplace(c);
        }
    }

    private <T extends Data> T[] loadUpdated(String typeUri, Date lastChange, Class<T[]> classOf) {

        T[] result = null;
        try {
            RestHttpConnection con = sendRequest(typeUri, lastChange);

            if(con.getResponseCode() == HttpURLConnection.HTTP_NOT_FOUND) {
                uri = uri.toLowerCase();
                con = sendRequest(typeUri, lastChange);
            }
            if(con.getResponseCode() == HttpURLConnection.HTTP_OK) {
                String response = con.getResponse();
                result = gson.fromJson(response, classOf);
            } else {
                System.out.println("Request Method: " + con.getRequestMethod());
                System.out.println("Response Code: " + con.getResponseCode());
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    @NonNull
    private RestHttpConnection sendRequest(String type, Date lastChange) throws IOException {
        URL url = new URL(uri + type + "/changed/" + lastChange.getTime());
        RestHttpConnection con = new RestHttpConnection(url, RestHttpConnection.HTTP_REQUEST_GET);
        con.send("");
        return con;
    }

    private <T extends Data> void doUpdate() {

    }

    @Override
    protected void onPostExecute(Void aVoid) {
        if(listener != null)
            listener.syncFinished();
        super.onPostExecute(aVoid);
    }

    public interface SyncFinishedListener {
        void syncFinished();
    }
}
