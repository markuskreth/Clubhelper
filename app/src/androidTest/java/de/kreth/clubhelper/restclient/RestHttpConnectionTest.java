package de.kreth.clubhelper.restclient;

import android.test.suitebuilder.annotation.MediumTest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import de.kreth.clubhelper.data.Person;
import de.kreth.clubhelper.data.PersonType;
import de.kreth.clubhelper.data.SyncStatus;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@MediumTest
@RunWith(MockitoJUnitRunner.class)
public class RestHttpConnectionTest {

    @Mock
    private HttpURLConnection connection;
    private ByteArrayOutputStream outputStream;
    private JsonMapper mapper;

    private Calendar created;
    private Calendar birth;
    private Calendar changed;
    private Person data;
    private URL url;

    @Before
    public void setup() throws IOException {
        MockitoAnnotations.initMocks(this);
        outputStream = new ByteArrayOutputStream();
        mapper = new JsonMapper();

        when(connection.getOutputStream()).thenReturn(outputStream);

        created = new GregorianCalendar(2015, Calendar.JANUARY, 1, 8, 0, 0);
        birth = new GregorianCalendar(1973, Calendar.AUGUST, 21, 8, 0, 0);
        changed = (Calendar) created.clone();
        changed.add(Calendar.HOUR_OF_DAY, 2);
        data = new Person(-1L, "Markus", "Kreth", PersonType.STAFF.name(), birth.getTime(), changed.getTime(), created.getTime(), SyncStatus.NEW);
        url = new URL("http://localhost");
    }

    @Test
    public void testSendPerson() throws IOException, BadPaddingException, InvalidKeyException, IllegalBlockSizeException, NoSuchAlgorithmException, NoSuchPaddingException {

        when(connection.getResponseCode()).thenReturn(HttpURLConnection.HTTP_OK);

        RestHttpConnection restHttpConnection = new MockRestHttpConnection(url, RestHttpConnection.HTTP_REQUEST_GET);

        Person src = new Person(1L, "Markus", "Kreth", PersonType.STAFF.name(), birth.getTime(), changed.getTime(), created.getTime(), SyncStatus.NEW);
        String srcJson = mapper.toJson(src);

        ByteArrayInputStream inputStream = new ByteArrayInputStream(srcJson.getBytes());
        when(connection.getInputStream()).thenReturn(inputStream);
        Person result = restHttpConnection.send(data, Person.class);

        verify(connection).setRequestMethod(RestHttpConnection.HTTP_REQUEST_GET);
        verify(connection).setRequestProperty("User-Agent", "Mozilla/5.0");
        verify(connection).setRequestProperty("Content-Type", "application/json");
        verify(connection).setRequestProperty("Accept", "application/json");

    }

    @Test
    public void testSendPostPerson() throws IOException, BadPaddingException, InvalidKeyException, IllegalBlockSizeException, NoSuchAlgorithmException, NoSuchPaddingException {

        when(connection.getResponseCode()).thenReturn(HttpURLConnection.HTTP_OK);

        RestHttpConnection restHttpConnection = new MockRestHttpConnection(url, RestHttpConnection.HTTP_REQUEST_POST);

        Person src = new Person(1L, "Markus", "Kreth", PersonType.STAFF.name(), birth.getTime(), changed.getTime(), created.getTime(), SyncStatus.NEW);
        String srcJson = mapper.toJson(src);

        ByteArrayInputStream inputStream = new ByteArrayInputStream(srcJson.getBytes());
        when(connection.getInputStream()).thenReturn(inputStream);
        Person result = restHttpConnection.send(data, Person.class);

        verify(connection).setRequestMethod(RestHttpConnection.HTTP_REQUEST_POST);
        verify(connection).setRequestProperty("User-Agent", "Mozilla/5.0");
        verify(connection).setRequestProperty("Content-Type", "application/json");
        verify(connection).setRequestProperty("Accept", "application/json");
        verify(connection).setDoOutput(true);

        assertEquals(src.getId(), result.getId());
        assertEquals(src.getBirth(), result.getBirth());
        assertEquals(src.getChanged(), result.getChanged());
        assertEquals(src.getCreated(), result.getCreated());
        assertEquals(src.getPersonType(), result.getPersonType());
        assertEquals(src.getPrename(), result.getPrename());
        assertEquals(src.getSurname(), result.getSurname());
        assertEquals(src.getType(), result.getType());

        String json = outputStream.toString();
        final Person sendPerson = mapper.fromJson(json, Person.class);

        assertEquals(-1L, sendPerson.getId().longValue());
        assertEquals(src.getBirth(), sendPerson.getBirth());
        assertEquals(src.getChanged(), sendPerson.getChanged());
        assertEquals(src.getCreated(), sendPerson.getCreated());
        assertEquals(src.getPersonType(), sendPerson.getPersonType());
        assertEquals(src.getPrename(), sendPerson.getPrename());
        assertEquals(src.getSurname(), sendPerson.getSurname());
        assertEquals(src.getType(), sendPerson.getType());
    }

    private class MockRestHttpConnection extends RestHttpConnection {

        public MockRestHttpConnection(URL url, String httpRequestType) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, UnsupportedEncodingException {
            super(url, httpRequestType);
        }

        @Override
        protected HttpURLConnection getConnection(URL url) throws IOException {
            return connection;
        }
    }
}
