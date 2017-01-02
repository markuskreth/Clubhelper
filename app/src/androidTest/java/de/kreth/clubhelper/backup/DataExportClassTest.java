package de.kreth.clubhelper.backup;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import de.kreth.clubhelper.data.Adress;
import de.kreth.clubhelper.data.Contact;
import de.kreth.clubhelper.data.Person;
import de.kreth.clubhelper.data.PersonType;
import de.kreth.clubhelper.data.Relative;
import de.kreth.clubhelper.data.SyncStatus;
import de.kreth.clubhelper.restclient.JsonMapper;

import static junit.framework.Assert.assertNull;

/**
 * Created by markus on 20.11.16.
 */
public class DataExportClassTest {

    private JsonMapper gson;
    private DataExportClass data;
    private final Date birth1 = new GregorianCalendar(1973, Calendar.AUGUST, 21).getTime();
    private final Date changed1 = new GregorianCalendar(2016, Calendar.AUGUST, 21, 19,1,1).getTime();
    private final Date created1 = new GregorianCalendar(2015, Calendar.AUGUST, 21, 19,1,1).getTime();

    @Before
    public void initData() {
        data = new DataExportClass();
    }

    @Before
    public void initGson() {
        gson = new JsonMapper();
    }

    @Test
    public void storeAndRestorePerson() {
        List<Person> persons = new ArrayList<>();
        Person expected = getTestPerson1();
        persons.add(expected);

        data.setPersons(persons);
        data.setAdresss(new ArrayList<Adress>());
        data.setContacts(new ArrayList<Contact>());
        data.setRelatives(new ArrayList<Relative>());
        String json = gson.toJson(data);
        DataExportClass result = gson.fromJson(json, DataExportClass.class);
        Person actual = result.getPersons().get(0);
        assertEquals(expected, actual);

    }

    @Test
    public void storeAndRestorePersonContactRelative() {

        Person expected1 = getTestPerson1();
        Person expected2 = getTestPerson2();

        Contact c1 = new Contact(1L, "Mobile", "05551234567", 1L, changed1, created1, SyncStatus.NEW);

        Relative rel = new Relative(1L, expected1.getId().longValue(), expected2.getId().longValue(), "Parent", "Child", changed1, created1, SyncStatus.NEW);

        data.setPersons(Arrays.asList(expected1, expected2));
        data.setContacts(Arrays.asList(c1));
        data.setRelatives(Arrays.asList(rel));

        String json = gson.toJson(data);
        DataExportClass result = gson.fromJson(json, DataExportClass.class);

        assertEquals(expected1, result.getPersons().get(0));
        assertEquals(expected2, result.getPersons().get(1));
        assertEquals(c1, result.getContacts().get(0));
        assertEquals(rel, result.getRelatives().get(0));
        assertNull(result.getPersons().get(0).getSyncStatus());
        assertNull(result.getPersons().get(1).getSyncStatus());
    }

    private void assertEquals(Relative r1, Relative r2) {
        Assert.assertEquals(r1.getId(), r2.getId());
        Assert.assertEquals(r1.getPerson1(), r2.getPerson1());
        Assert.assertEquals(r1.getPerson2(), r2.getPerson2());
        Assert.assertEquals(r1.getToPerson1Relation(), r2.getToPerson1Relation());
        Assert.assertEquals(r1.getToPerson2Relation(), r2.getToPerson2Relation());
        Assert.assertEquals(r1.getCreated(), r2.getCreated());
        Assert.assertEquals(r1.getChanged(), r2.getChanged());
    }

    private void assertEquals(Contact c1, Contact c2) {
        Assert.assertEquals(c1.getId(), c2.getId());
        Assert.assertEquals(c1.getPersonId(), c2.getPersonId());
        Assert.assertEquals(c1.getType(), c2.getType());
        Assert.assertEquals(c1.getValue(), c2.getValue());
        Assert.assertEquals(c1.getCreated(), c2.getCreated());
        Assert.assertEquals(c1.getChanged(), c2.getChanged());
    }

    private void assertEquals(Person p1, Person p2) {
        Assert.assertEquals(p1.getId(), p2.getId());
        Assert.assertEquals(p1.getPrename(), p2.getPrename());
        Assert.assertEquals(p1.getSurname(), p2.getSurname());
        Assert.assertEquals(p1.getBirth(), p2.getBirth());
        Assert.assertEquals(p1.getCreated(), p2.getCreated());
        Assert.assertEquals(p1.getChanged(), p2.getChanged());
        Assert.assertEquals(p1.getPersonType(), p2.getPersonType());
        Assert.assertEquals(p1.getType(), p2.getType());
    }

    private Person getTestPerson1() {
        return new Person(1L, "Eine", "Person", PersonType.ACTIVE.name(), birth1, created1, changed1, SyncStatus.NEW);
    }
    private Person getTestPerson2() {
        return new Person(2L, "Zweite", "Person", PersonType.RELATIVE.name(), birth1, created1, changed1, SyncStatus.NEW);
    }
}
