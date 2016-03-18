package de.kreth.clubhelper.restclient;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import de.kreth.clubhelper.Adress;
import de.kreth.clubhelper.Attendance;
import de.kreth.clubhelper.Contact;
import de.kreth.clubhelper.Data;
import de.kreth.clubhelper.Person;
import de.kreth.clubhelper.PersonType;
import de.kreth.clubhelper.Relative;
import de.kreth.clubhelper.dao.DaoSession;

/**
 * Created by markus on 25.06.15.
 */
public class RestClient implements Runnable {

    private DaoSession session;
    private String uri;

    public RestClient(DaoSession session, String uri) {
        this.session = session;
        this.uri = uri;
    }

    @Override
    public void run() {
        try {
            List<Person> data = session.getPersonDao().loadAll();
            fixPersons(data);
            send(data, "person", Person.class);
            List<Contact> contacts = session.getContactDao().loadAll();
            send(contacts, "contact", Contact.class);
            List<Adress> adresses = session.getAdressDao().loadAll();
            send(adresses, "adress", Adress.class);
            List<Relative> relatives = session.getRelativeDao().loadAll();
            send(relatives, "relative", Relative.class);
            List<Attendance> attendances = session.getAttendanceDao().loadAll();
            send(attendances, "attendance", Attendance.class);

        } catch (IOException e) {
            Log.e("", "Fehler beim Senden", e);
        } catch (NoSuchPaddingException e) {
            Log.e("", "Fehler beim Senden", e);
        } catch (NoSuchAlgorithmException e) {
            Log.e("", "Fehler beim Senden", e);
        } catch (InvalidKeyException e) {
            Log.e("", "Fehler beim Senden", e);
        } catch (IllegalBlockSizeException e) {
            Log.e("", "Fehler beim Senden", e);
        } catch (BadPaddingException e) {
            Log.e("", "Fehler beim Senden", e);
        }
    }

    private void fixPersons(List<Person> data) {
        for(Person p: data) {
            if(p.getType() == null || p.getType().isEmpty()) {
                p.setPersonType(PersonType.ACTIVE);
                p.update();
            }
        }
    }

    private <T extends Data> void send(List<T> data, String urlPath, Class<T> classOfT) throws IOException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {

        if(data.isEmpty())
            return;

        for(T p: data) {

            String path = urlPath + "/" + p.getId();

            URL url = new URL(uri + path);
            RestHttpConnection con = new RestHttpConnection(url, RestHttpConnection.HTTP_REQUEST_POST);
            T fromJson = con.send(p, classOfT);

            int responseCode = con.getResponseCode();
            if(responseCode == HttpURLConnection.HTTP_NOT_FOUND) {
                uri = uri.toLowerCase();
                url = new URL(uri + path);
                con = new RestHttpConnection(url, RestHttpConnection.HTTP_REQUEST_POST);
                fromJson = con.send(p, classOfT);
                responseCode = con.getResponseCode();
            }

            if( p.equals(fromJson))
                System.out.println("Result equal:" + p.equals(fromJson));
            else {
                if(fromJson != null) {
                    System.out.println("Id equal: " + (p.getId() == fromJson.getId()));
                    System.out.println(p.toString() + "\n" + fromJson.toString());
                } else {
                    System.out.println("Response Code = " + responseCode);
                    System.out.println("Response Object = " + fromJson);
                }
            }
            System.out.println(con.getResponse());
            con.close();
        }
    }
}
