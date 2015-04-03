package de.kreth.clubhelper.widgets;

import android.database.sqlite.SQLiteDatabase;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.ListView;
import android.support.v4.app.Fragment;

import com.robotium.solo.Condition;
import com.robotium.solo.Solo;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import de.kreth.clubhelper.Person;
import de.kreth.clubhelper.PersonType;
import de.kreth.clubhelper.TestActivity;
import de.kreth.clubhelper.dao.DaoMaster;
import de.kreth.clubhelper.dao.DaoSession;
import de.kreth.clubhelper.dao.PersonDao;

/**
 * Created by markus on 22.02.15.
 */
public class PersonSelectDialogTest extends ActivityInstrumentationTestCase2<TestActivity> {

    private String dbname;
    private PersonSelectDialog dlg;
    private DaoSession session;


    private Person mk;
    private Person jb;
    private TestActivity activity;
    private Solo solo;
    private Date now;

    public PersonSelectDialogTest() {
        super(TestActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        now = new GregorianCalendar(2014, Calendar.NOVEMBER, 1).getTime();

        dbname = PersonSelectDialogTest.class.getSimpleName() + ".sqlite";
        activity = getActivity();
        solo = new Solo(getInstrumentation(), activity);
        SQLiteDatabase db = new DaoMaster.DevOpenHelper(activity, dbname, null).getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        session = daoMaster.newSession();
        dlg = new PersonSelectDialog();
        dlg.setSession(session);
    }

    @Override
    protected void tearDown() throws Exception {
        activity.deleteDatabase(dbname);
        super.tearDown();
    }

    public void testDialogShowingUnfiltered() {
        PersonDao personDao = session.getPersonDao();
        mk = new Person(null, "Markus", "Kreth", PersonType.STAFF.name(), new GregorianCalendar(1973, Calendar.AUGUST, 21).getTime(), now, now);
        jb = new Person(null, "Test", "Person", PersonType.ACTIVE.name(), new GregorianCalendar(1986, Calendar.SEPTEMBER, 14).getTime(), now, now);
        personDao.insert(mk);
        personDao.insert(jb);
        personDao.insert(new Person(null, "Third", "Person", PersonType.ACTIVE.name(), new GregorianCalendar(1980, Calendar.JANUARY, 1).getTime(), now, now));
        personDao.insert(new Person(null, "Fourth", "Person", PersonType.ACTIVE.name(), new GregorianCalendar(1980, Calendar.JANUARY, 1).getTime(), now, now));
        personDao.insert(new Person(null, "Fifth", "Person", PersonType.ACTIVE.name(), new GregorianCalendar(1980, Calendar.JANUARY, 1).getTime(), now, now));


        final List<Person> selected = new ArrayList<>();

        PersonSelectDialog.DialogResultHandler handler = new PersonSelectDialog.DialogResultHandler() {
            @Override
            public void selectedPersons(Collection<Person> s) {
                selected.addAll(s);
            }
        };
        dlg.setResultHandler(handler);

        dlg.show(activity.getSupportFragmentManager(), PersonSelectDialog.TAG);
        getInstrumentation().waitForIdleSync();
        Fragment fragment = getActivity().getSupportFragmentManager().findFragmentByTag(PersonSelectDialog.TAG);
        assertTrue(fragment instanceof PersonSelectDialog);
        assertTrue(((PersonSelectDialog) fragment).getShowsDialog());
        ArrayList<ListView> currentViews = solo.getCurrentViews(ListView.class);
        assertEquals(1, currentViews.size());
        ListView listView = currentViews.get(0);

        assertEquals(5, listView.getAdapter().getCount());
        solo.clickInList(3);
        solo.waitForCondition(new Condition() {
            @Override
            public boolean isSatisfied() {
                return ! selected.isEmpty();
            }
        }, 500);
        Person p = selected.get(0);
        assertEquals("Fourth", p.getPrename());
    }

    public void testFilterAndSelect() {

        PersonDao personDao = session.getPersonDao();
        personDao.insert(new Person(null, "First", "Men", PersonType.STAFF.name(), new GregorianCalendar(1973, Calendar.AUGUST, 21).getTime(), now, now));
        personDao.insert(new Person(null, "Second", "Person", PersonType.ACTIVE.name(), new GregorianCalendar(1986, Calendar.SEPTEMBER, 14).getTime(), now, now));
        personDao.insert(new Person(null, "Third", "Men", PersonType.ACTIVE.name(), new GregorianCalendar(1980, Calendar.JANUARY, 1).getTime(), now, now));
        personDao.insert(new Person(null, "Fourth", "Person", PersonType.ACTIVE.name(), new GregorianCalendar(1980, Calendar.JANUARY, 1).getTime(), now, now));
        personDao.insert(new Person(null, "Fifth", "Person", PersonType.ACTIVE.name(), new GregorianCalendar(1980, Calendar.JANUARY, 1).getTime(), now, now));

        final List<Person> selected = new ArrayList<>();

        PersonSelectDialog.DialogResultHandler handler = new PersonSelectDialog.DialogResultHandler() {
            @Override
            public void selectedPersons(Collection<Person> s) {
                selected.addAll(s);
            }
        };
        dlg.setResultHandler(handler);

        dlg.show(activity.getSupportFragmentManager(), PersonSelectDialog.TAG);
        getInstrumentation().waitForIdleSync();
        solo.typeText(0, "ir");

        solo.clickInList(2);
        solo.waitForCondition(new Condition() {
            @Override
            public boolean isSatisfied() {
                return ! selected.isEmpty();
            }
        }, 500);
        Person p = selected.get(0);
        assertEquals("Third", p.getPrename());
    }

    public void testFilterAndExclude() {

        PersonDao personDao = session.getPersonDao();
        Person p1 = new Person(null, "First", "Men", PersonType.STAFF.name(), new GregorianCalendar(1973, Calendar.AUGUST, 21).getTime(), now, now);
        personDao.insert(p1);
        assertNotNull(p1.getId());

        personDao.insert(new Person(null, "Second", "Person", PersonType.ACTIVE.name(), new GregorianCalendar(1986, Calendar.SEPTEMBER, 14).getTime(), now, now));
        Person p3 = new Person(null, "Third", "Men", PersonType.ACTIVE.name(), new GregorianCalendar(1980, Calendar.JANUARY, 1).getTime(), now, now);
        personDao.insert(p3);
        assertNotNull(p3.getId());

        personDao.insert(new Person(null, "Fourth", "Person", PersonType.ACTIVE.name(), new GregorianCalendar(1980, Calendar.JANUARY, 1).getTime(), now, now));
        personDao.insert(new Person(null, "Fifth", "Person", PersonType.ACTIVE.name(), new GregorianCalendar(1980, Calendar.JANUARY, 1).getTime(), now, now));

        final List<Person> selected = new ArrayList<>();

        PersonSelectDialog.DialogResultHandler handler = new PersonSelectDialog.DialogResultHandler() {
            @Override
            public void selectedPersons(Collection<Person> s) {
                selected.addAll(s);
            }
        };
        dlg.setResultHandler(handler);
        dlg.addExcludePersonId(p1.getId());

        dlg.show(activity.getSupportFragmentManager(), PersonSelectDialog.TAG);
        getInstrumentation().waitForIdleSync();
        solo.typeText(0, "ir");

        solo.clickInList(1);
        solo.waitForCondition(new Condition() {
            @Override
            public boolean isSatisfied() {
                return ! selected.isEmpty();
            }
        }, 500);
        Person p = selected.get(0);
        assertEquals("Third", p.getPrename());
    }
}
