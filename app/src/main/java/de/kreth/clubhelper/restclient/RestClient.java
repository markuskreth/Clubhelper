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
import java.text.DateFormat;
import java.util.List;

import de.kreth.clubhelper.Adress;
import de.kreth.clubhelper.Attendance;
import de.kreth.clubhelper.Contact;
import de.kreth.clubhelper.Data;
import de.kreth.clubhelper.Person;
import de.kreth.clubhelper.Relative;
import de.kreth.clubhelper.dao.DaoSession;

/**
 * Created by markus on 25.06.15.
 */
public class RestClient implements Runnable {

    private final String USER_AGENT = "Mozilla/5.0";

    private static final String baseUrl = "http://10.0.2.2:8080/clubhelperbackend/";
    private static final String productiveUrl = "http://markuskreth.kreinacke.de:8080/ClubHelperBackend/";

    private final Gson gson = new GsonBuilder().setPrettyPrinting().setDateFormat("dd/MM/yyyy HH:mm:ss.SSS Z").create();
    private DaoSession session;

    public RestClient(DaoSession session) {
        this.session = session;
    }

    @Override
    public void run() {
        try {
            List<Person> data = session.getPersonDao().loadAll();
            send(data, "person", Person.class);
            List<Contact> contacts = session.getContactDao().loadAll();
            send(contacts, "contact", Contact.class);
            List<Adress> adresses = session.getAdressDao().loadAll();
            send(adresses, "adress", Adress.class);
            List<Relative> relatives = session.getRelativeDao().loadAll();
            send(relatives, "relative", Relative.class);
            List<Attendance> attendances = session.getAttendanceDao().loadAll();
            send(attendances, "attendance", Attendance.class);

        } catch (MalformedURLException e) {
            Log.e("", "Fehler beim Senden, e");
        } catch (IOException e) {
            Log.e("", "Fehler beim Senden", e);
        }
    }

    private <T extends Data> void send(List<T> data, String urlPath, Class<T> classOfT) throws IOException {

        if(data.isEmpty())
            return;

        for(T p: data) {

            String json = gson.toJson(p);

            String path = urlPath + "/" + p.getId();
            URL url = new URL(baseUrl + path);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            //add reuqest header
            con.setRequestMethod("POST");
            con.setRequestProperty("User-Agent", USER_AGENT);
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("Accept", "application/json");
            con.setDoOutput(true);

            PrintStream str = new PrintStream(con.getOutputStream());
            str.print(json);
            str.flush();
            str.close();

            int responseCode = con.getResponseCode();
            System.out.println("\nSending 'POST' request to URL : " + url);
            System.out.println(p);
            System.out.println("Response Code : " + responseCode);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }

            con.disconnect();
            in.close();

            //print result
            T fromJson = gson.fromJson(response.toString(), classOfT);

            if( p.equals(fromJson))
                System.out.println("Result equal:" + p.equals(fromJson));
            else {
                System.out.println("Id equal: " + (p.getId() == fromJson.getId()));
                System.out.println(p.toString() + "\n" + fromJson.toString());
            }
            System.out.println(response.toString());
        }
    }
}
