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
      boolean deleted = getInstrumentation().getTargetContext().deleteDatabase(DBNAME);
      super.tearDown();
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
