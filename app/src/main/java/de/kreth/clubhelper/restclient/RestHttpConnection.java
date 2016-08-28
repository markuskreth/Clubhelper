package de.kreth.clubhelper.restclient;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import de.kreth.clubhelper.data.Data;

/**
 * Connects to Server and sends Data objects.
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

    private final Encryptor encryptor;

    private final String httpRequestType;
    private final URL url;

    private int responseCode = 0;
    private HttpURLConnection con = null;
    private String response = null;

    public RestHttpConnection(URL url, String httpRequestType) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, UnsupportedEncodingException {
        this.url = url;
        if(httpRequestType== null)
            throw new NullPointerException("RequestType must not be null!");
        this.httpRequestType = httpRequestType;
        encryptor = new Encryptor();
    }

    private HttpURLConnection getConnection() throws IOException, BadPaddingException, InvalidKeyException, IllegalBlockSizeException {

        HttpURLConnection con = getConnection(url);

        con.setRequestMethod(httpRequestType);
        con.setRequestProperty("User-Agent", USER_AGENT);
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestProperty("Accept", "application/json");
        Date now = new Date();
        con.setRequestProperty("localtime", String.valueOf(now.getTime()));
        con.setRequestProperty("token", encryptor.encrypt(now, USER_AGENT));

        if (!httpRequestType.equals(HTTP_REQUEST_GET))
            con.setDoOutput(true);

        return con;
    }

    protected HttpURLConnection getConnection(URL url) throws IOException {
        return (HttpURLConnection) url.openConnection();
    }

    public void send(String json) throws IOException, BadPaddingException, InvalidKeyException, IllegalBlockSizeException {
        if (con != null)
            throw new IllegalStateException("Connection was used before");

        con = getConnection();
        if (!httpRequestType.equals(HTTP_REQUEST_GET)) {
            PrintStream str = new PrintStream(con.getOutputStream());
            str.print(json);
            str.flush();
            str.close();
        }
        responseCode = con.getResponseCode();
    }

    @SuppressWarnings("unchecked")
    public <T extends Data> T send(T obj, Class<T> classOfT) throws IOException, BadPaddingException, InvalidKeyException, IllegalBlockSizeException {
        JsonMapper gson = new JsonMapper();
        send(gson.toJson(obj));

        T data = null;
        if (getResponseCode() == HttpURLConnection.HTTP_OK) {
            data =gson.fromJson(getResponse(), classOfT);
        }

        return data;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public String getResponse() throws IOException {

        if (this.response == null) {
            final InputStream inputStream = con.getInputStream();
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(inputStream));
            String inputLine;
            StringBuilder response = new StringBuilder();

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
        if (con != null)
            return con.getRequestMethod();
        else
            return httpRequestType;
    }
}
