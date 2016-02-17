package de.kreth.clubhelper.restclient;

import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import de.greenrobot.dao.AbstractDao;
import de.kreth.clubhelper.Adress;
import de.kreth.clubhelper.Attendance;
import de.kreth.clubhelper.Contact;
import de.kreth.clubhelper.Data;
import de.kreth.clubhelper.Person;
import de.kreth.clubhelper.Relative;
import de.kreth.clubhelper.Synchronization;
import de.kreth.clubhelper.dao.DaoSession;
import de.kreth.clubhelper.dao.SynchronizationDao;

/**
 * {@link AsyncTask}, that synchronizes all date with REST server.
 * Created by markus on 30.08.15.
 */
public class SyncRestClient extends AsyncTask<Void, Void, Void> {

    private final Gson gson = new GsonBuilder().setPrettyPrinting().setDateFormat("dd/MM/yyyy HH:mm:ss.SSS Z").create();
    private final DaoSession session;
    private String uri;
    private SyncFinishedListener listener = null;
    private SynchronizationDao synchronizationDao;

    public SyncRestClient(DaoSession session, String uri) {
        this.session = session;
        this.uri = uri;
        synchronizationDao = session.getSynchronizationDao();

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

        final AbstractDao<?, ?> dao1 = session.getDao(classForType);
        if (dao1 != null) {
            AbstractDao<T, Long> dao = (AbstractDao<T, Long>) dao1;
            final String simpleName = classForType.getSimpleName();

            Synchronization synchronization = synchronizationDao.queryBuilder().where(SynchronizationDao.Properties.Table_name.eq(dao.getTablename())).build().unique();

            if(synchronization == null) {
                synchronization = new Synchronization();
                synchronization.setTable_name(dao.getTablename());

                final String sqlRaw = "WHERE CHANGED=(select max(CHANGED) from " + simpleName.toUpperCase() + ")";
                List<T> datas = dao.queryRaw(sqlRaw);

                if (datas.size() > 0)
                    synchronization.setDownload_successful(datas.get(0).getChanged());
                else
                    synchronization.setDownload_successful(new Date(0));

                synchronization.setUpload_successful(new Date(0));
            }

            Date downloadSuccessful = synchronization.getDownload_successful();

            T[] updated = loadUpdated(simpleName.toLowerCase(), downloadSuccessful, classForList);
            for (T c : updated) {
                dao.insertOrReplace(c);
            }

            Date uploadSuccessful = synchronization.getUpload_successful();
            boolean succsess = upload(simpleName.toLowerCase(), dao, uploadSuccessful);
        }
    }

    private <T extends Data> boolean upload(String typeUri, AbstractDao<T, Long> dao, Date uploadSuccessful) {

        final List<T> toUpload = dao.queryRaw("WHERE CHANGED>" + uploadSuccessful.getTime());
        boolean success = false;
        for (T data : toUpload) {

            URL url = null;
            try {
                url = new URL(uri + typeUri + "/" + data.getId());
                RestHttpConnection con = new RestHttpConnection(url, RestHttpConnection.HTTP_REQUEST_POST);
                T result = con.send(data);
                if(! result.equals(data)) {
                    dao.update(result);
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (NoSuchPaddingException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (InvalidKeyException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IllegalBlockSizeException e) {
                e.printStackTrace();
            } catch (BadPaddingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return success;
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

        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        return result;
    }

    @NonNull
    private RestHttpConnection sendRequest(String type, Date lastChange) throws IOException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        URL url = new URL(uri + type + "/changed/" + lastChange.getTime());
        RestHttpConnection con = new RestHttpConnection(url, RestHttpConnection.HTTP_REQUEST_GET);
        con.send("");
        return con;
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
