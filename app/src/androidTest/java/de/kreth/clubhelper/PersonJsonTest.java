package de.kreth.clubhelper;

import android.test.AndroidTestCase;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import de.kreth.clubhelper.backup.DataExportClass;
import de.kreth.clubhelper.data.Adress;
import de.kreth.clubhelper.data.Contact;
import de.kreth.clubhelper.data.Person;
import de.kreth.clubhelper.data.PersonType;
import de.kreth.clubhelper.data.SyncStatus;
import de.kreth.clubhelper.restclient.JsonMapper;

public class PersonJsonTest extends AndroidTestCase {

    private Date now;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        now = new GregorianCalendar(2014, Calendar.NOVEMBER, 1).getTime();
    }

    public void testToJson() throws Exception {
        JsonMapper gson = new JsonMapper();
        Person markus = new Person(1L, "Markus", "Kreth", PersonType.STAFF.name(), new GregorianCalendar(1973, Calendar.AUGUST, 21).getTime(), now, now, SyncStatus.NEW);

        String markusJson = gson.toJson(markus);

        Person deserialized = gson.fromJson(markusJson, Person.class);
        assertEquals(1L, deserialized.getId().longValue());
        assertEquals("Markus", deserialized.getPrename());
        assertEquals("Kreth", deserialized.getSurname());
        assertEquals("STAFF", deserialized.getType());
        assertEquals(now, deserialized.getChanged());
        assertEquals(now, deserialized.getCreated());

        Date birth = new GregorianCalendar(1973, Calendar.AUGUST, 21).getTime();
        assertEquals(birth, deserialized.getBirth());
    }

    public void testFromJson() {
        String input = "{\"id\":1,\"prename\":\"Markus\",\"surname\":\"Kreth\",\"type\":\"Trainer\",\"birth\":\"Aug 21, 1973 12:00:00 AM\"}";
        Gson gson = new Gson();

        Person deserialized = gson.fromJson(input, Person.class);
        assertEquals(1L, deserialized.getId().longValue());
        assertEquals("Markus", deserialized.getPrename());
        assertEquals("Kreth", deserialized.getSurname());
        assertEquals("Trainer", deserialized.getType());
        assertNull(deserialized.getChanged());
        assertNull(deserialized.getCreated());

        Date birth = new GregorianCalendar(1973, Calendar.AUGUST, 21).getTime();
        assertEquals(birth, deserialized.getBirth());
    }

    public void testNamedJson() {

        DataExportClass data = new DataExportClass();

        List<Person> list = new ArrayList<>();
        list.add(new Person(1L, "Markus", "Kreth", "Trainer", new GregorianCalendar(1973, Calendar.AUGUST, 21).getTime(), now, now, SyncStatus.NEW));
        list.add(new Person(2L, "Test", "Person", "Aktive", new GregorianCalendar(1989, Calendar.AUGUST, 1).getTime(), now, now, SyncStatus.NEW));
        data.setPersons(list);

        List<Contact> contacts = new ArrayList<>();
        contacts.add(new Contact(1L, "Email", "markus.kr@web.de", 1L, now, now, SyncStatus.NEW));
        contacts.add(new Contact(2L, "Mobile", "0174555555", 1L, now, now, SyncStatus.NEW));
        contacts.add(new Contact(3L, "Email", "Test.kr@web.de", 2L, now, now, SyncStatus.NEW));
        data.setContacts(contacts);

        List<Adress> adresses = new ArrayList<>();
        adresses.add(new Adress(1L, "Markus Straße", "", "30555", "Hannover", 1L, now, now, SyncStatus.NEW));
        data.setAdresses(adresses);

        JsonMapper gson = new JsonMapper();

        String json = gson.toJson(data);

        assertTrue(json.contains("Markus"));
        assertTrue(json.contains("Test.kr@web.de"));
        assertTrue(json.contains("Trainer"));
        assertTrue(json.contains("Aktive"));
        assertTrue(json.contains("0174555555"));
        assertTrue(json.contains("Hannover"));
        assertTrue(json.contains("30555"));
        assertFalse(json.toLowerCase().contains("syncstatus"));
        assertFalse(json.contains("NEW"));

        DataExportClass deserialized = gson.fromJson(json, DataExportClass.class);
        List<Person> persons = deserialized.getPersons();
        assertEquals(2, persons.size());
        Person mk = persons.get(0);
        Person test = persons.get(1);

        if(mk.getId() != 1L) {
            test = persons.get(0);
            mk = persons.get(1);
        }

        assertEquals("Markus", mk.getPrename());
        assertEquals("Kreth", mk.getSurname());
        List<Contact> contacts1 = deserialized.getContacts();
        assertEquals(3, contacts1.size());

    }
}