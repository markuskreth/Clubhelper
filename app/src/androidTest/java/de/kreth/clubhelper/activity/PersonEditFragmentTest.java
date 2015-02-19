package de.kreth.clubhelper.activity;

import android.app.Activity;
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

        Relative relative = new Relative(null, persons.get(0).getId(), persons.get(1).getId(), RelationType.CHILD.toString(), RelationType.MOTHER.toString());
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

        solo.setActivityOrientation(Solo.LANDSCAPE);
        solo.searchText("test@test.com", true);
        solo.searchText("0511-555 555 555", true);
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

        solo.searchText("test@testdomain.com", true);
        solo.searchText("Email", true);
    }


}
