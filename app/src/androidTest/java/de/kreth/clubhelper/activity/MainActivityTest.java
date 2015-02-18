package de.kreth.clubhelper.activity;

import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import com.robotium.solo.Solo;

import java.util.ArrayList;

import de.kreth.clubhelper.MainActivity;
import de.kreth.clubhelper.R;

/**
 * Created by markus on 09.02.15.
 */
public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity> {

   private final static String DBNAME = MainActivityTest.class.getSimpleName() + ".sqlite";
   private Solo solo;

   public MainActivityTest() {
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
      MainActivity.DBNAME = originalDbName;
      solo.finishOpenedActivities();
      getInstrumentation().getTargetContext().deleteDatabase(DBNAME);
      super.tearDown();
   }

   public void testFilterPersons() {

      solo.waitForActivity(MainActivity.class);
      solo.assertCurrentActivity("MainActivity not found!", MainActivity.class);
      ArrayList<ListView> currentViews = solo.getCurrentViews(ListView.class);
      assertEquals(1, currentViews.size());

      ListView listView = currentViews.get(0);
      assertEquals(0, listView.getAdapter().getCount());

      View actionAdd = solo.getView(R.id.action_addPerson);
      solo.clickOnView(actionAdd);
      solo.waitForDialogToOpen();

      solo.typeText(0, "Erste");
      solo.typeText(1, "Person");
      solo.clickOnButton("Speichern");
      assertTrue(solo.waitForDialogToClose());
      assertEquals(1, listView.getAdapter().getCount());

      solo.clickOnView(actionAdd);
      solo.waitForDialogToOpen();
      solo.typeText(0, "Zweite");
      solo.clickOnButton("Speichern");
      assertTrue(solo.waitForDialogToClose());
      assertEquals(2, listView.getAdapter().getCount());

      solo.clickOnView(actionAdd);
      solo.waitForDialogToOpen();
      solo.typeText(0, "Dritte");
      solo.typeText(1, "Testperson");
      solo.clickOnButton("Speichern");
      assertTrue(solo.waitForDialogToClose());
      assertEquals(3, listView.getAdapter().getCount());

      solo.clickOnView(actionAdd);
      solo.waitForDialogToOpen();
      solo.typeText(0, "Vierte");
      solo.typeText(1, "Person Test");
      solo.clickOnButton("Speichern");
      assertTrue(solo.waitForDialogToClose());
      assertEquals(4, listView.getAdapter().getCount());

      EditText searchEdit = solo.getEditText(0);
      solo.typeText(searchEdit, "Zweite");
      assertEquals(1, listView.getAdapter().getCount());
      solo.clearEditText(searchEdit);

      solo.typeText(searchEdit, "weite");
      assertEquals(1, listView.getAdapter().getCount());
      solo.clearEditText(searchEdit);

      solo.typeText(searchEdit, "erson");
      assertEquals(3, listView.getAdapter().getCount());
      solo.clearEditText(searchEdit);

      solo.typeText(searchEdit, "Test");
      assertEquals(2, listView.getAdapter().getCount());

      solo.clickOnView(actionAdd);
      solo.waitForDialogToOpen();
      solo.typeText(0, "Fünfte");
      solo.typeText(1, "Person Test");
      solo.clickOnButton("Speichern");
      assertTrue(solo.waitForDialogToClose());

      assertEquals(3, listView.getAdapter().getCount());
   }

   public void testInsertAndSelectPerson() {
      solo.waitForActivity(MainActivity.class);
      solo.assertCurrentActivity("MainActivity not found!", MainActivity.class);
      ArrayList<ListView> currentViews = solo.getCurrentViews(ListView.class);
      assertEquals(1, currentViews.size());

      ListView listView = currentViews.get(0);

      View actionAdd = solo.getView(R.id.action_addPerson);
      solo.clickOnView(actionAdd);
      solo.waitForDialogToOpen();

      solo.typeText(0, "Eine");
      solo.typeText(1, "Testperson");
      solo.clickOnButton("Speichern");
      assertTrue(solo.waitForDialogToClose());
      assertEquals(1, listView.getAdapter().getCount());
      solo.clickLongInList(1);
      solo.waitForDialogToOpen();
      solo.getEditText("Eine");

      solo.getEditText("Testperson");
      solo.goBack();
   }
}
