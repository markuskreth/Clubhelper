package de.kreth.clubhelper.restclient;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/**
 * Created by markus on 09.10.16.
 */

public class HostFilter extends AsyncTask<String, Void, List<String>> {

    private final List<String> validHosts;

    public HostFilter(List<String> validHosts) {
        this.validHosts = validHosts;
    }

    public boolean testUri(String uri) {
        try {
            Log.d(getClass().getName(), "Teste " + uri);
            URL url = new URL(uri);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            int responseCode = con.getResponseCode();

            if(responseCode != HttpURLConnection.HTTP_OK)
                return false;

        } catch (MalformedURLException e) {
            Log.w(getClass().getName(), "Fehler bei " + uri, e);
            return false;
        } catch (IOException e) {
            Log.w(getClass().getName(), "Fehler bei " + uri, e);
            return false;
        }

        return true;
    }

    @Override
    protected List<String> doInBackground(String... hosts) {
        for (String host: hosts) {
            if(testUri(host))
                validHosts.add(host);
            else {
                host = host.toLowerCase();
                if(testUri(host))
                    validHosts.add(host);
            }
        }
        return validHosts;
    }

}
