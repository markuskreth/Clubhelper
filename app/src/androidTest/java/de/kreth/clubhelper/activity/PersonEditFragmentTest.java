package de.kreth.clubhelper.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.res.Resources;
import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import com.robotium.solo.Solo;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import de.kreth.clubhelper.Adress;
import de.kreth.clubhelper.Attendance;
import de.kreth.clubhelper.Contact;
import de.kreth.clubhelper.DbCleaner;
import de.kreth.clubhelper.MainActivity;
import de.kreth.clubhelper.Person;
import de.kreth.clubhelper.PersonType;
import de.kreth.clubhelper.R;
import de.kreth.clubhelper.RelationType;
import de.kreth.clubhelper.Relative;
import de.kreth.clubhelper.dao.ContactDao;
import de.kreth.clubhelper.dao.DaoSession;
import de.kreth.clubhelper.dao.PersonDao;
import de.kreth.clubhelper.datahelper.SessionHolder;
import de.kreth.clubhelper.widgets.ContactTypeAdapter;

/**
 * Created by markus on 16.02.15.
 */
public class PersonEditFragmentTest extends ActivityInstrumentationTestCase2<MainActivity> {

    private final static String DBNAME = PersonEditFragmentTest.class.getSimpleName() + ".sqlite";
    private Solo solo;
    private Date now;

    public PersonEditFragmentTest() {
        super(MainActivity.class);
    }

    String originalDbName;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        originalDbName = MainActivity.DBNAME;
        MainActivity.DBNAME = DBNAME;
        now = new GregorianCalendar(2014, Calendar.NOVEMBER, 1).getTime();
        solo = new Solo(getInstrumentation(), getActivity());
    }

    @Override
    protected void tearDown() throws Exception {
        Activity currentActivity = solo.getCurrentActivity();
        if(currentActivity instanceof SessionHolder) {
            DaoSession session = ((SessionHolder) currentActivity).getSession();
            new DbCleaner().cleanSession(session);
        }

        solo.finishOpenedActivities();
        boolean deleted = getInstrumentation().getTargetContext().deleteDatabase(DBNAME);
        deleted = getInstrumentation().getTargetContext().deleteDatabase(DBNAME);
        MainActivity.DBNAME = originalDbName;
        super.tearDown();
    }

    public void testShowContactsAndRelatives() {

        solo.waitForActivity(MainActivity.class);
        solo.assertCurrentActivity("MainActivity not found!", MainActivity.class);

        ArrayList<ListView> currentViews = solo.getCurrentViews(ListView.class);
        assertEquals(1, currentViews.size());

        ListView listView = currentViews.get(0);
        assertEquals(0, listView.getAdapter().getCount());

        View actionAdd = solo.getView(R.id.action_addPerson);
        solo.clickOnView(actionAdd);
        solo.waitForDialogToOpen();

        solo.typeText(0, "Eine");
        solo.typeText(1, "Testperson");
        solo.clickOnButton("Speichern");
        assertTrue(solo.waitForDialogToClose());
        assertEquals(1, listView.getAdapter().getCount());

        solo.clickOnView(actionAdd);
        solo.waitForDialogToOpen();

        solo.typeText(0, "Zweite");
        solo.typeText(1, "Testperson");
        solo.clickOnButton("Speichern");
        assertTrue(solo.waitForDialogToClose());
        assertEquals(2, listView.getAdapter().getCount());

        DaoSession session = ((MainActivity) solo.getCurrentActivity()).getSession();
        PersonDao personDao = session.getPersonDao();
        List<Person> persons = personDao.loadAll();
        assertEquals(2, persons.size());

        ContactDao contactDao = session.getContactDao();
        Contact ersteTel = new Contact();
        ersteTel.setPersonId(persons.get(0).getId());
        ersteTel.setType("Telefon");
        ersteTel.setValue("0511-555 555 555");
        ersteTel.setCreated(new Date());
        ersteTel.setChanged(new Date());
        contactDao.insert(ersteTel);

        Contact ersteEmail = new Contact();
        ersteEmail.setPersonId(persons.get(0).getId());
        ersteEmail.setType("Email");
        ersteEmail.setValue("test@test.com");
        ersteEmail.setCreated(new Date());
        ersteEmail.setChanged(new Date());
        contactDao.insert(ersteEmail);

        Relative relative = new Relative(null, persons.get(0).getId(), persons.get(1).getId(), RelationType.CHILD.toString(), RelationType.PARENT.toString(), now, now);
        session.getRelativeDao().insert(relative);
        assertNotNull(relative.getId());

        persons.get(0).refresh();

        solo.clickLongInList(1);
        solo.searchText(persons.get(0).getPrename());
        solo.searchText(persons.get(0).getSurname());

        solo.clickOnText("Beziehungen");
        solo.searchText("Eltern", true);
        solo.searchText(persons.get(0).getPrename(), true);
        solo.searchText(persons.get(0).getSurname(), true);
        solo.clickOnText("Kontakte");

    }

    public void testSetBirthdate() {

        solo.waitForActivity(MainActivity.class);
        solo.assertCurrentActivity("MainActivity not found!", MainActivity.class);

        ArrayList<ListView> currentViews = solo.getCurrentViews(ListView.class);
        assertEquals(1, currentViews.size());

        ListView listView = currentViews.get(0);
        assertEquals(0, listView.getAdapter().getCount());

        View actionAdd = solo.getView(R.id.action_addPerson);
        solo.clickOnView(actionAdd);
        solo.waitForDialogToOpen();

        solo.typeText(0, "Eine");
        solo.typeText(1, "Testperson");
        solo.clickOnButton("Speichern");
        assertTrue(solo.waitForDialogToClose());
        assertEquals(1, listView.getAdapter().getCount());

        solo.clickLongInList(1);
        assertTrue(solo.waitForFragmentByTag(PersonEditFragment.TAG));
        solo.clickOnText("2000");
        solo.waitForDialogToOpen();
        solo.setDatePicker(0, 1973, Calendar.AUGUST, 21);
        solo.clickOnButton(0);
        solo.waitForDialogToClose();
        solo.searchText("1973");
        solo.searchText("21");

    }

    public void testChangeOrientation() {

        solo.waitForActivity(MainActivity.class);
        solo.assertCurrentActivity("MainActivity not found!", MainActivity.class);

        ArrayList<ListView> currentViews = solo.getCurrentViews(ListView.class);
        assertEquals(1, currentViews.size());

        ListView listView = currentViews.get(0);
        assertEquals(0, listView.getAdapter().getCount());

        View actionAdd = solo.getView(R.id.action_addPerson);
        solo.clickOnView(actionAdd);
        solo.waitForDialogToOpen();

        solo.typeText(0, "Eine");
        solo.typeText(1, "Testperson");
        solo.clickOnButton("Speichern");
        assertTrue(solo.waitForDialogToClose());
        assertEquals(1, listView.getAdapter().getCount());

        solo.clickLongInList(1);
        assertTrue(solo.waitForFragmentByTag(PersonEditFragment.TAG));

        actionAdd = solo.getView(R.id.action_addPerson);
        solo.clickOnView(actionAdd);
        solo.waitForDialogToOpen();

        assertTrue(solo.searchText("Kontakt", true));
        assertTrue(solo.searchText("Beziehung", true));
        solo.clickInList(1);

        solo.waitForDialogToOpen();
        solo.pressSpinnerItem(0,1);
        solo.typeText(0, "555-55 55 55");
        solo.clickOnText("OK");

        solo.searchText("555-55 55 55", true);
        solo.searchText("Telefon", true);

        solo.clickOnView(actionAdd);
        solo.waitForDialogToOpen();

        assertTrue(solo.searchText("Kontakt", true));
        assertTrue(solo.searchText("Beziehung", true));
        solo.clickInList(1);

        solo.waitForDialogToOpen();
        solo.pressSpinnerItem(0,2);
        solo.typeText(0, "test@testdomain.com");
        solo.clickOnText("OK");

        solo.setActivityOrientation(Solo.LANDSCAPE);
        solo.searchText("test@testdomain.com", true);
        solo.searchText("555-55 55 55", true);

        solo.setActivityOrientation(Solo.PORTRAIT);
        solo.searchText("test@testdomain.com", true);
        solo.searchText("555-55 55 55", true);

    }

    public void testAddContact() {

        solo.waitForActivity(MainActivity.class);
        solo.assertCurrentActivity("MainActivity not found!", MainActivity.class);

        ArrayList<ListView> currentViews = solo.getCurrentViews(ListView.class);
        assertEquals(1, currentViews.size());

        ListView listView = currentViews.get(0);
        assertEquals(0, listView.getAdapter().getCount());

        View actionAdd = solo.getView(R.id.action_addPerson);
        solo.clickOnView(actionAdd);
        solo.waitForDialogToOpen();

        solo.typeText(0, "Eine");
        solo.typeText(1, "Testperson");
        solo.clickOnButton("Speichern");
        assertTrue(solo.waitForDialogToClose());
        assertEquals(1, listView.getAdapter().getCount());

        solo.clickLongInList(1);
        assertTrue(solo.waitForFragmentByTag(PersonEditFragment.TAG));

        actionAdd = solo.getView(R.id.action_addPerson);
        solo.clickOnView(actionAdd);
        solo.waitForDialogToOpen();

        assertTrue(solo.searchText("Kontakt", true));
        assertTrue(solo.searchText("Beziehung", true));
        solo.clickInList(1);

        solo.waitForDialogToOpen();
        solo.pressSpinnerItem(0,1);
        solo.typeText(0, "0555-55 55 55");
        solo.clickOnText("OK");

        assertTrue(solo.waitForDialogToClose());
        solo.waitForFragmentByTag(PersonEditFragment.TAG);

        solo.searchText("05555 555", true);
        solo.searchText("Telefon", true);

        solo.clickOnView(actionAdd);
        solo.waitForDialogToOpen();

        assertTrue(solo.searchText("Kontakt", true));
        assertTrue(solo.searchText("Beziehung", true));
        solo.clickInList(1);

        solo.waitForDialogToOpen();
        solo.pressSpinnerItem(0,2);
        solo.typeText(0, "test@testdomain.com");
        solo.clickOnText("OK");

        assertTrue(solo.waitForDialogToClose());
        solo.waitForFragmentByTag(PersonEditFragment.TAG);

        solo.searchText("test@testdomain.com", true);
        solo.searchText("Email", true);
    }

    public void testAddAdress() {

        solo.waitForActivity(MainActivity.class);
        solo.assertCurrentActivity("MainActivity not found!", MainActivity.class);
        MainActivity main = (MainActivity) solo.getCurrentActivity();
        PersonDao personDao = main.getSession().getPersonDao();
        Person p1 = new Person(null, "Prename", "Surname", PersonType.ACTIVE.name(), new GregorianCalendar(1973, Calendar.AUGUST, 21).getTime(), now,now );
        personDao.insert(p1);
        solo.setActivityOrientation(Solo.LANDSCAPE);
        solo.setActivityOrientation(Solo.PORTRAIT);
        solo.clickLongOnText("Prename");

        assertTrue(solo.waitForFragmentByTag(PersonEditFragment.TAG));

        View actionAdd = solo.getView(R.id.action_addPerson);
        solo.clickOnView(actionAdd);
        solo.waitForDialogToOpen();

        assertTrue(solo.searchText("Kontakt", true));
        assertTrue(solo.searchText("Beziehung", true));
        assertTrue(solo.searchText("Adresse", true));
        solo.clickInList(3);

        assertTrue(solo.waitForDialogToOpen());
        solo.clickOnText("Abbr");
        assertTrue(solo.waitForDialogToClose());
    }

    public void testDetailItemsValues() {
        String[] stringArray = getActivity().getResources().getStringArray(R.array.person_detail_items);

        assertEquals("Kontakt", stringArray[0]);
        assertEquals("Beziehung", stringArray[1]);
        assertEquals("Adresse", stringArray[2]);
    }

    public void testContactTypeValues() {

        String[] stringArray = getActivity().getResources().getStringArray(R.array.contact_type_values);
        assertEquals("Mobile", stringArray[0]);
        assertEquals("Telefon", stringArray[1]);
        assertEquals("Email", stringArray[2]);
    }

    public void testRelationTypeToString() {
        Resources resources = getActivity().getResources();
        assertEquals("Kind", RelationType.CHILD.toString(resources));
        assertEquals("Elternteil", RelationType.PARENT.toString(resources));
        assertEquals("Freund(-in)", RelationType.RELATIONSHIP.toString(resources));
    }

    public void testAddRelation() {

        solo.waitForActivity(MainActivity.class);
        solo.assertCurrentActivity("MainActivity not found!", MainActivity.class);

        MainActivity mainActivity = (MainActivity) solo.getCurrentActivity();
        DaoSession session = mainActivity.getSession();

        ArrayList<ListView> currentViews = solo.getCurrentViews(ListView.class);
        assertEquals(1, currentViews.size());

        ListView listView = currentViews.get(0);
        assertEquals(0, listView.getAdapter().getCount());

        View actionAdd = solo.getView(R.id.action_addPerson);
        solo.clickOnView(actionAdd);
        solo.waitForDialogToOpen();

        solo.typeText(0, "Eine");
        solo.typeText(1, "Testperson");
        solo.clickOnButton("Speichern");
        assertTrue(solo.waitForDialogToClose());
        assertEquals(1, listView.getAdapter().getCount());

        solo.clickOnView(actionAdd);
        solo.waitForDialogToOpen();
        solo.typeText(0, "Zweite");
        solo.typeText(1, "Person");
        solo.pressSpinnerItem(0,2);

        solo.clickOnButton("Speichern");
        assertTrue(solo.waitForDialogToClose());
        assertEquals(2, listView.getAdapter().getCount());

        solo.clickLongInList(1);
        assertTrue(solo.waitForFragmentByTag(PersonEditFragment.TAG));

        solo.clickOnText("Bezieh");
        actionAdd = solo.getView(R.id.action_addPerson);
        solo.clickOnView(actionAdd);
        solo.waitForDialogToOpen();

        assertTrue(solo.searchText("Kontakt", true));
        assertTrue(solo.searchText("Beziehung", true));
        solo.clickInList(2);

        assertTrue("Neuer Dialog nicht geöffnet", solo.waitForDialogToOpen());

        ArrayList<ListView> currentLists = solo.getCurrentViews(ListView.class);
        assertEquals(1, currentLists.size());
        ListView persons = currentLists.get(0);
        assertEquals(1, persons.getCount());
        assertTrue(solo.searchText("Eine", true));
        assertTrue(solo.searchText("Testperson", true));
        solo.clickOnText("Testperson");

        assertTrue("Neuer Dialog nicht geöffnet", solo.waitForDialogToOpen());
        currentLists = solo.getCurrentViews(ListView.class);
        assertEquals(1, currentLists.size());

        assertTrue(solo.searchText("Eltern", true));
        assertTrue(solo.searchText("Kind", true));
        assertTrue(solo.searchText("Freund", true));
        solo.clickOnText("Kind");
        solo.waitForDialogToClose();
        assertTrue(solo.waitForLogMessage("created"));

        Person zweite = null;
        Person testperson = null;
        for(Person p : session.getPersonDao().loadAll()) {
            if(p.getPrename().matches("Zweite"))
                zweite = p;
            else if (p.getPrename().matches("Eine"))
                testperson = p;
        }
        assertNotNull(zweite);
        assertNotNull(testperson);

        List<Person.RelativeType> relations = zweite.getRelations();
        assertEquals(1, relations.size());
        assertEquals(testperson.getId(), relations.get(0).getRel().getId());
        assertEquals(RelationType.CHILD, relations.get(0).getType());

        assertEquals(1, testperson.getRelations().size());
        assertEquals(RelationType.PARENT, testperson.getRelations().get(0).getType());

        assertTrue(solo.searchText("Eine", true));
        assertTrue(solo.searchText("Testperson", true));
        assertTrue(solo.searchText("Kind", true));

    }

    public void testChangeName() {

        solo.waitForActivity(MainActivity.class);
        solo.assertCurrentActivity("MainActivity not found!", MainActivity.class);

        MainActivity mainActivity = (MainActivity) solo.getCurrentActivity();
        DaoSession session = mainActivity.getSession();
        Person mk = new Person(null, "Marku", "Kret", PersonType.STAFF.name(), new GregorianCalendar(1973, Calendar.AUGUST, 21).getTime(), new GregorianCalendar(2015, Calendar.AUGUST, 21).getTime(), new GregorianCalendar(1973, Calendar.AUGUST, 21).getTime());
        session.getPersonDao().insert(mk);

        Contact con = new Contact(null, "Email", "mk@test.de", mk.getId(), new Date(), new Date());
        session.getContactDao().insert(con);

        mainActivity.refreshFragmentView();
        assertTrue(solo.waitForText("Marku"));
        solo.clickLongOnText("Marku");

        assertTrue(solo.waitForFragmentByTag(PersonEditFragment.TAG));
        EditText prenameEdt = solo.getEditText("Marku");
        EditText surnameEdt = solo.getEditText("Kret");
        solo.enterText(prenameEdt, "s");
        solo.enterText(surnameEdt, "h");
        solo.goBack();

        assertTrue(solo.waitForFragmentByTag(MainFragment.TAG));

        assertTrue(solo.searchText("Markus"));
        assertTrue(solo.searchText("Kreth"));

        assertEquals("Markus", mk.getPrename());
        assertEquals("Kreth", mk.getSurname());
    }

}
