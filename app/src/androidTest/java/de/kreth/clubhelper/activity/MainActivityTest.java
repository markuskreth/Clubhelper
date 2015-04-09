package de.kreth.clubhelper.activity;

import android.app.Activity;
import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import com.robotium.solo.Condition;
import com.robotium.solo.Solo;

import java.util.ArrayList;

import de.kreth.clubhelper.DbCleaner;
import de.kreth.clubhelper.MainActivity;
import de.kreth.clubhelper.R;
import de.kreth.clubhelper.dao.DaoSession;

/**
 * Created by markus on 09.02.15.
 */
public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity> {

    private Solo solo;
    private View actionAdd;

    public MainActivityTest() {
        super(MainActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        solo = new Solo(getInstrumentation(), getActivity());
    }

    @Override
    protected void tearDown() throws Exception {
        getInstrumentation().waitForIdleSync();
        Activity currentActivity = solo.getCurrentActivity();
        if( currentActivity instanceof MainActivity) {
            DaoSession session = ((MainActivity) currentActivity).getSession();
            if(session.getDatabase().isOpen() && ! session.getDatabase().isReadOnly())
                new DbCleaner().cleanSession(session);
        }
        solo.finishOpenedActivities();
        super.tearDown();
    }

    public void testFilterPersons() {

        solo.waitForActivity(MainActivity.class);
        solo.assertCurrentActivity("MainActivity not found!", MainActivity.class);
        ArrayList<ListView> currentViews = solo.getCurrentViews(ListView.class);
        assertEquals(1, currentViews.size());

        final ListView listView = currentViews.get(0);
        assertEquals(0, listView.getAdapter().getCount());

        addPerson("Erste", "Person");
        assertEquals(1, listView.getAdapter().getCount());

        addPerson("Zweite", "Person");
        assertEquals(2, listView.getAdapter().getCount());

        addPerson("Dritte", "Testperson");
        assertEquals(3, listView.getAdapter().getCount());

        addPerson("Vierte", "Person Test");
        assertEquals(4, listView.getAdapter().getCount());

        EditText searchEdit = solo.getEditText(0);
        solo.typeText(searchEdit, "Zweite");
        solo.waitForCondition(new Condition() {
            @Override
            public boolean isSatisfied() {
                return 1 == listView.getAdapter().getCount();
            }
        }, 100);

        solo.clearEditText(searchEdit);

        solo.typeText(searchEdit, "weite");
        solo.waitForCondition(new Condition() {
            @Override
            public boolean isSatisfied() {
                return 1 == listView.getAdapter().getCount();
            }
        }, 100);

        solo.clearEditText(searchEdit);

        solo.typeText(searchEdit, "erson");
        solo.waitForCondition(new Condition() {
            @Override
            public boolean isSatisfied() {
                return 3 == listView.getAdapter().getCount();
            }
        }, 100);

        solo.clearEditText(searchEdit);

        solo.typeText(searchEdit, "Test");
        solo.waitForCondition(new Condition() {
            @Override
            public boolean isSatisfied() {
                return 2 == listView.getAdapter().getCount();
            }
        }, 100);

        addPerson("Fünfte", "Person Test");
        assertEquals(3, listView.getAdapter().getCount());
    }

    public void testChangeOrientation() {

        solo.waitForActivity(MainActivity.class);
        solo.assertCurrentActivity("MainActivity not found!", MainActivity.class);
        ArrayList<ListView> allListViews = solo.getCurrentViews(ListView.class);
        assertEquals(1, allListViews.size());

        final ListView listView = allListViews.get(0);
        assertEquals(0, listView.getAdapter().getCount());

        addPerson("Erste", "Person");
        assertEquals(1, listView.getAdapter().getCount());

        addPerson("Zweite", "Speichern");
        assertEquals(2, listView.getAdapter().getCount());

        solo.setActivityOrientation(Solo.LANDSCAPE);
        solo.searchText("Erste", true);
        assertEquals(2, listView.getAdapter().getCount());

        solo.setActivityOrientation(Solo.PORTRAIT);
        solo.searchText("Zweite", true);
        assertEquals(2, listView.getAdapter().getCount());

    }

    private void addPerson(String preName, String surName) {
        if(actionAdd == null)
            actionAdd = solo.getView(R.id.action_addPerson);

        solo.clickOnView(actionAdd);
        solo.waitForDialogToOpen();

        solo.typeText(0, preName);
        solo.typeText(1, surName);
        solo.clickOnButton("Speichern");
        assertTrue(solo.waitForDialogToClose());
    }

    public void testInsertAndSelectPerson() {
        solo.waitForActivity(MainActivity.class);
        solo.assertCurrentActivity("MainActivity not found!", MainActivity.class);
        ArrayList<ListView> currentViews = solo.getCurrentViews(ListView.class);
        assertEquals(1, currentViews.size());

        ListView listView = currentViews.get(0);

        addPerson("Eine", "Testperson");
        assertEquals(1, listView.getAdapter().getCount());

        addPerson("Zweite", "Testperson");
        assertEquals(2, listView.getAdapter().getCount());

        solo.clickLongInList(1);
        solo.waitForDialogToOpen();
        solo.getEditText("Eine");
        solo.getEditText("Testperson");
        solo.goBack();

        assertTrue(solo.waitForFragmentByTag(MainFragment.TAG));
        solo.searchText("Zweite", true);
    }

    public void testContactDialogWithRelations() {

        solo.waitForActivity(MainActivity.class);
        solo.assertCurrentActivity("MainActivity not found!", MainActivity.class);
        addPerson("Erste", "Person");
        addPerson("Zweite", "Person");

        ArrayList<ListView> currentViews = solo.getCurrentViews(ListView.class);
        assertEquals(1, currentViews.size());

        final ListView listView = currentViews.get(0);
        assertEquals(2, listView.getAdapter().getCount());

        solo.clickLongInList(1);

        solo.clickOnView(actionAdd);
        solo.clickOnText("Beziehung");
        solo.waitForDialogToOpen();
        solo.clickInList(1);    // First Person
        solo.waitForDialogToOpen();
        solo.clickInList(1);    // First RelationType

        solo.clickOnView(actionAdd);
        solo.waitForDialogToOpen();
        solo.clickOnText("Kontakt");
        solo.waitForDialogToOpen();
        solo.enterText(0, "01742521286");
        solo.clickOnText("OK");

        solo.goBack();

        solo.clickLongInList(2);

        solo.clickOnView(actionAdd);
        solo.waitForDialogToOpen();
        solo.clickOnText("Kontakt");
        solo.waitForDialogToOpen();
        solo.enterText(0, "0555123456");
        solo.clickOnText("OK");

        solo.goBack();

        solo.searchText("Erste");
        solo.searchText("Zweite");

        solo.clickInList(1);

        assertTrue(solo.waitForDialogToOpen());
        solo.searchText("2521286");
        solo.searchText("123456");
        solo.searchText("Erste");
        solo.searchText("Zweite");
        solo.searchText("Elternteil");

    }
}
