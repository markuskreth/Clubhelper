package de.kreth.clubhelper.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.res.Resources;
import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import android.widget.ListView;

import com.robotium.solo.Solo;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import de.kreth.clubhelper.Contact;
import de.kreth.clubhelper.MainActivity;
import de.kreth.clubhelper.Person;
import de.kreth.clubhelper.R;
import de.kreth.clubhelper.RelationType;
import de.kreth.clubhelper.Relative;
import de.kreth.clubhelper.dao.ContactDao;
import de.kreth.clubhelper.dao.DaoSession;
import de.kreth.clubhelper.dao.PersonDao;

/**
 * Created by markus on 16.02.15.
 */
public class PersonEditFragmentTest extends ActivityInstrumentationTestCase2<MainActivity> {

    private final static String DBNAME = MainActivityTest.class.getSimpleName() + ".sqlite";
    private Solo solo;

    public PersonEditFragmentTest() {
        super(MainActivity.class);
    }

    String originalDbName;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        originalDbName = MainActivity.DBNAME;
        MainActivity.DBNAME = DBNAME;
        solo = new Solo(getInstrumentation(), getActivity());
    }

    @Override
    protected void tearDown() throws Exception {
        solo.finishOpenedActivities();
        boolean deleted = getInstrumentation().getTargetContext().deleteDatabase(DBNAME);
        deleted = getInstrumentation().getTargetContext().deleteDatabase("test.sqlite");
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
        contactDao.insert(ersteTel);

        Contact ersteEmail = new Contact();
        ersteEmail.setPersonId(persons.get(0).getId());
        ersteEmail.setType("Email");
        ersteEmail.setValue("test@test.com");
        contactDao.insert(ersteEmail);

        Relative relative = new Relative(null, persons.get(0).getId(), persons.get(1).getId(), RelationType.CHILD.toString(), RelationType.PARENT.toString());
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
        solo.clickOnImageButton(0);
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
        solo.clickOnButton("Speichern");
        assertTrue(solo.waitForDialogToClose());
        assertEquals(2, listView.getAdapter().getCount());

        solo.clickLongInList(2);
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
        assertEquals(3, currentLists.get(0).getAdapter().getCount());

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

}
