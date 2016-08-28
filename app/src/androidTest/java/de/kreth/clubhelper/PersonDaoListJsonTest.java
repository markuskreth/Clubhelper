package de.kreth.clubhelper;

import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import de.kreth.clubhelper.dao.AdressDao;
import de.kreth.clubhelper.dao.ContactDao;
import de.kreth.clubhelper.dao.DaoMaster;
import de.kreth.clubhelper.dao.DaoSession;
import de.kreth.clubhelper.dao.PersonDao;
import de.kreth.clubhelper.data.Adress;
import de.kreth.clubhelper.data.Contact;
import de.kreth.clubhelper.data.Person;
import de.kreth.clubhelper.data.SyncStatus;

/**
 * Created by markus on 23.03.15.
 */
public class PersonDaoListJsonTest extends AndroidTestCase {

    private Gson gson;
    private Date now;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        now = new GregorianCalendar(2014, Calendar.NOVEMBER, 1).getTime();
        gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
    }

    public void testListOfPersons() {
        Person p1 = new Person(1L, "Markus", "Kreth", "Developer", new GregorianCalendar(1973, Calendar.AUGUST, 21).getTime(), now, now, SyncStatus.NEW);

        Person p2 = new Person(2L, "Second", "Person", "Active", new GregorianCalendar(1986, Calendar.APRIL, 16).getTime(), now, now, SyncStatus.NEW);

        Person[] all = new Person[2];
        all[0] = p1;
        all[1] = p2;

        List<Person> personList = new ArrayList<>();
        personList.add(p1);
        personList.add(p2);

        String jsonCollection = gson.toJson(personList);
        assertTrue(jsonCollection.startsWith("["));

        Person[] persons = gson.fromJson(jsonCollection, Person[].class);
        assertEquals(2, persons.length);
        Person mk = persons[0];
        assertEquals(p1.getId(), mk.getId());
        assertEquals(p1.getBirth(), mk.getBirth());
        assertEquals(p1.getPrename(), mk.getPrename());
        assertEquals(p1.getSurname(), mk.getSurname());
        assertEquals(p1.getType(), mk.getType());
    }

    public void testCombinedJson() {

        SQLiteDatabase db = new DaoMaster.DevOpenHelper(getContext(), getClass().getSimpleName()+ ".sqlite", null).getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        DaoSession session = daoMaster.newSession();
        PersonDao personDao = session.getPersonDao();

        Person p1 = new Person(null, "First", "Surname1", "Parent", new GregorianCalendar(1973, Calendar.AUGUST, 21).getTime(), now, now, SyncStatus.NEW);
        Person p2 = new Person(null, "Second", "Surname2", "Parent", new GregorianCalendar(1986, Calendar.MARCH, 21).getTime(), now, now, SyncStatus.NEW);
        personDao.insert(p1);
        personDao.insert(p2);
        Contact c1a = new Contact(null, "Phone", "555111111", p1.getId(), now, now, SyncStatus.NEW);
        Contact c1b = new Contact(null, "Email", "firstsurname@test.com", p1.getId(), now, now, SyncStatus.NEW);
        ContactDao contactDao = session.getContactDao();
        contactDao.insert(c1a);
        contactDao.insert(c1b);

        Contact c2a = new Contact(null, "Phone", "555222222", p2.getId(), now, now, SyncStatus.NEW);
        Contact c2b = new Contact(null, "Email", "secondsurname@test.com", p2.getId(), now, now, SyncStatus.NEW);

        contactDao.insert(c2a);
        contactDao.insert(c2b);

        Adress a1 = new Adress(null, "Streetname 1", "", "20123", "Berlin", p1.getId(), now, now, SyncStatus.NEW);
        AdressDao adressDao = session.getAdressDao();
        adressDao.insert(a1);

        p1.getAdressList();
        p1.getContactList();
        p2.getContactList();
        Person[] personArr = {p1, p2};
        String personsJson = gson.toJson(personArr);

        assertTrue(personsJson.contains("firstsurname@test.com"));
        assertTrue(personsJson.contains("Parent"));
        assertTrue(personsJson.contains("555111111"));
        assertTrue(personsJson.contains("Streetname 1"));
        assertTrue(personsJson.contains("Berlin"));
        assertTrue(personsJson.contains("secondsurname@test.com"));
        assertTrue(personsJson.contains("555222222"));
        Person[] persons = gson.fromJson(personsJson, Person[].class);
        assertEquals(2, persons.length);

        p1.resetAdressList();
        p1.resetContactList();
        p2.resetAdressList();
        p2.resetContactList();

        personsJson = gson.toJson(personDao.loadAll());

        assertFalse(personsJson.contains("firstsurname@test.com"));
        assertFalse(personsJson.contains("555111111"));
        assertFalse(personsJson.contains("Streetname 1"));
        assertFalse(personsJson.contains("Berlin"));
        assertFalse(personsJson.contains("secondsurname@test.com"));
        assertFalse(personsJson.contains("555222222"));
        new DbCleaner().cleanSession(session);
        db.close();
    }
}
