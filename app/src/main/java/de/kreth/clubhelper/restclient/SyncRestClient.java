package de.kreth.clubhelper.restclient;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import de.greenrobot.dao.AbstractDao;
import de.kreth.clubhelper.data.Adress;
import de.kreth.clubhelper.data.Attendance;
import de.kreth.clubhelper.data.Contact;
import de.kreth.clubhelper.data.Data;
import de.kreth.clubhelper.data.Person;
import de.kreth.clubhelper.data.Relative;
import de.kreth.clubhelper.data.SyncStatus;
import de.kreth.clubhelper.data.Synchronization;
import de.kreth.clubhelper.dao.DaoSession;
import de.kreth.clubhelper.dao.SynchronizationDao;

/**
 * {@link AsyncTask}, that synchronizes all date with REST server.
 * Created by markus on 30.08.15.
 */
public class SyncRestClient extends AsyncTask<SyncRestClient.ClassHolder, Void, Exception> {

    private final DaoSession session;
    private String uri;
    private final SyncFinishedListener listener;
    private final SynchronizationDao synchronizationDao;
    private final Date now;

    public SyncRestClient(DaoSession session, String uri) {
        this(session, uri, null);
    }

    public SyncRestClient(DaoSession session, String uri, SyncFinishedListener listener) {
        this.session = session;
        this.uri = uri;
        synchronizationDao = session.getSynchronizationDao();
        this.listener = listener;
        now = new Date();
    }

    @Override
    protected Exception doInBackground(ClassHolder... params) {
        try {

            if(params == null || params.length == 0) {
                updateData(new ClassHolder<>(Person.class, Person[].class));
                updateData(new ClassHolder<>(Contact.class, Contact[].class));
                updateData(new ClassHolder<>(Adress.class, Adress[].class));
                updateData(new ClassHolder<>(Relative.class, Relative[].class));
                updateData(new ClassHolder<>(Attendance.class, Attendance[].class));
            } else
                updateSinge(params);

        } catch (Exception e) {
            return e;
        }

        return null;
    }

    private void updateSinge(ClassHolder[] params) {
        for (ClassHolder holder : params) {
            updateData(holder);
        }
    }

    public <T extends Data> void updateData(ClassHolder<T> holder) {

        final AbstractDao<?, ?> dao1 = session.getDao(holder.classForType);
        if (dao1 != null) {
            final AbstractDao<T, Long> dao = (AbstractDao<T, Long>) dao1;
            final String simpleName = holder.classForType.getSimpleName();

            Synchronization synchronization = synchronizationDao.queryBuilder()
                    .where(SynchronizationDao.Properties.Table_name.eq(dao.getTablename()))
                    .build()
                    .unique();

            if(synchronization == null) {
                synchronization = new Synchronization();
                synchronization.setTable_name(dao.getTablename());

                final String sqlRaw = "WHERE CHANGED=(select max(CHANGED) from " + simpleName.toUpperCase() + ")";
                List<T> datas = dao.queryRaw(sqlRaw);

                if (datas.size() > 0) {
                    final Date time = new GregorianCalendar(2015, Calendar.AUGUST, 31, 22, 26, 59).getTime();   // Latest Server up/download on real system.
                    synchronization.setDownload_successful(time);
                    synchronization.setUpload_successful(time);
                }
                else {
                    synchronization.setDownload_successful(new Date(0));
                    synchronization.setUpload_successful(new Date(0));
                }

                synchronizationDao.insert(synchronization);
            }

            Date lastUpload = synchronization.getUpload_successful();
            Date lastDownload = synchronization.getDownload_successful();

            final List<T> toUpload = dao.queryRaw("WHERE CHANGED>" + lastUpload.getTime());

            final List<T> updated = Arrays.asList(loadUpdated(simpleName.toLowerCase(), lastDownload, holder.classForList));

            mergeChanges(toUpload, updated);

            for (T c : updated) {
                dao.insertOrReplace(c);
                Log.i(getClass().getSimpleName(), "Stored " + c);
            }

            synchronization.setDownload_successful(now);
            synchronizationDao.update(synchronization);

            try {
                for (T data : toUpload) {
                    String method = RestHttpConnection.HTTP_REQUEST_PUT;

                    if(data.getCreated().after(lastUpload))
                        method = RestHttpConnection.HTTP_REQUEST_POST;

                    T result = upload(simpleName.toLowerCase(), dao, data, method, holder.classForType);
                    if(result == null && method == RestHttpConnection.HTTP_REQUEST_POST)
                        upload(simpleName.toLowerCase(), dao, data, RestHttpConnection.HTTP_REQUEST_PUT, holder.classForType);
                    Log.i(getClass().getSimpleName(), "Uploaded " + data + " -- Result " + result);
                }
                synchronization.setUpload_successful(now);
                synchronizationDao.update(synchronization);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
    }

    private <T extends Data> void mergeChanges(List<T> toUpload, List<T> downloaded) {
        for (T serverSide : new ArrayList<>(downloaded)) {
            for (T local : new ArrayList<>(toUpload)) {
                if(local.getId() == serverSide.getId()) {
                    if(local.getCreated().equals(serverSide.getCreated())) {
                        if (local.getChanged().after(serverSide.getChanged())) {
                            downloaded.remove(serverSide);
                        } else {
                            toUpload.remove(local);
                        }
                    } else {
                        if(local.getId()>=0)
                            local.setId(local.getId()*-1);
                    }
                }
            }
        }
    }

    private <T extends Data> T upload(String typeUri, AbstractDao<T, Long> dao, T data, String method, Class<T> classOfT) throws IOException {
        T result = null;

            try {
                URL url = new URL(uri + typeUri + "/" + data.getId());
                RestHttpConnection con = new RestHttpConnection(url, method);
                result = con.send(data, classOfT);
                if(result != null && ! result.equals(data)) {
                    result.setSyncStatus(SyncStatus.SYNCHRONIZED);
                    dao.update(result);
                }

            } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException | IOException e) {
                throw new IOException("Upload failed", e);
            }

        return result;
    }

    private <T extends Data> T[] loadUpdated(String typeUri, Date lastChange, Class<T[]> classOf) {
        JsonMapper gson = new JsonMapper();
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
    private RestHttpConnection sendRequest(String type, Date lastChange) throws IOException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, SocketTimeoutException {
        URL url = new URL(uri + type + "/changed/" + lastChange.getTime());
        RestHttpConnection con = new RestHttpConnection(url, RestHttpConnection.HTTP_REQUEST_GET);
        con.send("");
        return con;
    }

    @Override
    protected void onPostExecute(Exception e) {

        if(listener != null)
            listener.syncFinished(e);

        super.onPostExecute(e);
    }

    public interface SyncFinishedListener {
        /**
         * Called when sync has finisched.
         * @param e null if sync was successfull
         */
        void syncFinished(Exception e);
    }

    public static class ClassHolder<T> {
        private final Class<T> classForType;
        private final Class<T[]> classForList;

        public ClassHolder(Class<T> classForType, Class<T[]> classForList) {
            this.classForType = classForType;
            this.classForList = classForList;
        }

        public Class<T> getClassForType() {
            return classForType;
        }

        public Class<T[]> getClassForList() {
            return classForList;
        }

        @Override
        public String toString() {
            return classForType.toString();
        }
    }
}
