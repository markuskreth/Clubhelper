package de.kreth.clubhelper.activity;

import android.test.ActivityInstrumentationTestCase2;
import android.view.View;

import com.robotium.solo.Solo;

import de.kreth.clubhelper.MainActivity;
import de.kreth.clubhelper.R;

/**
 * Created by markus on 09.02.15.
 */
public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity> {

    private Solo solo;

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
        solo.finishOpenedActivities();
        super.tearDown();
    }

    public void testAppStartSimple() {
        solo.waitForActivity(MainActivity.class);
        solo.assertCurrentActivity("MainActivity not found!", MainActivity.class);
        solo.clickLongInList(1);
        solo.waitForDialogToOpen();
        solo.clickOnButton("Abbrechen");
        View actionAdd = solo.getView(R.id.action_addPerson);
        solo.clickOnView(actionAdd);
        solo.waitForDialogToOpen();

        solo.typeText(0, "Eine");
        solo.typeText(1, "Testperson");
        solo.clickOnButton("Abbrechen");
    }
}
