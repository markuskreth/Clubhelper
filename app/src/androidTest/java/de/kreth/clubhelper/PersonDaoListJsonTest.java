package de.kreth.clubhelper;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import de.kreth.clubhelper.dao.AdressDao;
import de.kreth.clubhelper.dao.ContactDao;
import de.kreth.clubhelper.dao.PersonDao;

/**
 * Created by markus on 23.03.15.
 */
public class PersonDaoListJsonTest extends AbstractDaoTest {

    private PersonDao personDao;
    private Gson gson;
    private Date now;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        now = new GregorianCalendar(2014, Calendar.NOVEMBER, 1).getTime();
        personDao = session.getPersonDao();
        gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
    }

    public void testListOfPersons() {
        Person p1 = new Person(null, "Markus", "Kreth", "Developer", new GregorianCalendar(1973, Calendar.AUGUST, 21).getTime(), now, now);
        personDao.insert(p1);
        Person p2 = new Person(null, "Second", "Person", "Active", new GregorianCalendar(1986, Calendar.APRIL, 16).getTime(), now, now);
        personDao.insert(p2);
        Person[] all = new Person[2];
        all[0] = p1;
        all[1] = p2;

        List<Person> personList = personDao.loadAll();
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
        Person p1 = new Person(null, "First", "Surname1", "Parent", new GregorianCalendar(1973, Calendar.AUGUST, 21).getTime(), now, now);
        Person p2 = new Person(null, "Second", "Surname2", "Parent", new GregorianCalendar(1986, Calendar.MARCH, 21).getTime(), now, now);
        personDao.insert(p1);
        personDao.insert(p2);
        Contact c1a = new Contact(null, "Phone", "555111111", p1.getId(), now, now);
        Contact c1b = new Contact(null, "Email", "firstsurname@test.com", p1.getId(), now, now);
        ContactDao contactDao = session.getContactDao();
        contactDao.insert(c1a);
        contactDao.insert(c1b);


        Contact c2a = new Contact(null, "Phone", "555222222", p2.getId(), now, now);
        Contact c2b = new Contact(null, "Email", "secondsurname@test.com", p2.getId(), now, now);

        contactDao.insert(c2a);
        contactDao.insert(c2b);

        Adress a1 = new Adress(null, "Streetname 1", "", "20123", "Berlin", p1.getId(), now, now);
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
    }
}
