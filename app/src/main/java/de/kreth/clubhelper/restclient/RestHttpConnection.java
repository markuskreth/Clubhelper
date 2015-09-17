package de.kreth.clubhelper.restclient;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.URL;

import de.kreth.clubhelper.Data;

/**
 * Created by markus on 02.09.15.
 */
public class RestHttpConnection {

    /**
     * Create entity
     */
    public static final String HTTP_REQUEST_POST = "POST";
    /**
     * get entity
     */
    public static final String HTTP_REQUEST_GET = "GET";
    /**
     * delete entity
     */
    public static final String HTTP_REQUEST_DELETE = "DELETE";
    /**
     * Update entity
     */
    public static final String HTTP_REQUEST_PUT = "PUT";

    private final String USER_AGENT = "Mozilla/5.0";

    private final Gson gson = new GsonBuilder().setPrettyPrinting().setDateFormat("dd/MM/yyyy HH:mm:ss.SSS Z").create();
    private final String httpRequestType;
    private final URL url;

    private int responseCode = 0;
    private HttpURLConnection con = null;
    private String response = null;

    public RestHttpConnection(URL url, String httpRequestType) {
        this.url = url;
        this.httpRequestType = httpRequestType;
    }

    private HttpURLConnection getConnection() throws IOException {

        HttpURLConnection con = (HttpURLConnection) url.openConnection();

        //add request header
        con.setRequestMethod(httpRequestType);
        con.setRequestProperty("User-Agent", USER_AGENT);
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestProperty("Accept", "application/json");
        if(httpRequestType != HTTP_REQUEST_GET)
            con.setDoOutput(true);

        return con;
    }

    public void send(String json) throws IOException {
        if(con != null)
            throw new IllegalStateException("Connection was used before");

        con = getConnection();
        if(httpRequestType != HTTP_REQUEST_GET) {
            PrintStream str = new PrintStream(con.getOutputStream());
            str.print(json);
            str.flush();
            str.close();
        }
        responseCode = con.getResponseCode();
    }

    public <T extends Data> T send(T obj) throws IOException {
        T data = null;
        send(gson.toJson(obj));
        if(getResponseCode() == HttpURLConnection.HTTP_OK)
            data = (T) gson.fromJson(getResponse(), obj.getClass());
        return data;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public String getResponse() throws IOException {

        if(this.response == null) {
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }

            in.close();
            this.response = response.toString();
        }
        return this.response;
    }

    public void close() {
        con.disconnect();
    }

    public String getRequestMethod() {
        if(con != null)
            return con.getRequestMethod();
        else
            return httpRequestType;
    }
}
