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
import de.kreth.clubhelper.Contact;
import de.kreth.clubhelper.Data;
import de.kreth.clubhelper.dao.DaoSession;

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
        List<Contact> contacts = session.getContactDao().queryRaw("WHERE CHANGED=(select max(CHANGED) from CONTACT)");
        Date lastChange;
        if(contacts.size()>0)
            lastChange = contacts.get(0).getChanged();
        else
            lastChange = new Date(0L);
        try {
            RestHttpConnection con = sendRequest(lastChange);
            URL url;
            if(con.getResponseCode() == HttpURLConnection.HTTP_NOT_FOUND) {
                uri = uri.toLowerCase();
                con = sendRequest(lastChange);
            }
            if(con.getResponseCode() == HttpURLConnection.HTTP_OK) {
                String response = con.getResponse();
                Contact[] result = gson.fromJson(response, Contact[].class);
                for (Contact c : result) {
                    session.getContactDao().update(c);
                }
            } else {
                System.out.println("Request Method: " + con.getRequestMethod());
                System.out.println("Response Code: " + con.getResponseCode());
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @NonNull
    private RestHttpConnection sendRequest(Date lastChange) throws IOException {
        URL url = new URL(uri + "contact/changed/" + lastChange.getTime());
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
