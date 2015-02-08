package de.kreth.clubhelper;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;

import java.util.Calendar;
import java.util.GregorianCalendar;


import de.kreth.clubhelper.activity.*;
import de.kreth.clubhelper.dao.DaoMaster;
import de.kreth.clubhelper.dao.DaoSession;
import de.kreth.clubhelper.dao.PersonDao;
import de.kreth.clubhelper.dao.RelativeDao;
import de.kreth.clubhelper.dialogs.PersonDialog;


public class MainActivity extends ActionBarActivity {

    public static DaoSession session;
    private MainFragment frgmt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initDb();

//        insertDummyPerson();

        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            frgmt = new MainFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, frgmt.setSession(session))
                    .commit();
        }
    }

    private void initDb() {
        SQLiteDatabase db = new DaoMaster.DevOpenHelper(this, "clubdatabase.db", null).getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        session = daoMaster.newSession();

    }

    @Override
    protected void onResume() {
        super.onResume();
//        Enumeration<Driver> drivers = DriverManager.getDrivers();
//
//        boolean hasSqlite = false;
//        String dbPath = "jdbc:sqlite:" + session.getDatabase().getPath();
//        while(drivers.hasMoreElements()){
//            try {
//                if(drivers.nextElement().acceptsURL(dbPath)) {
//                    hasSqlite = true;
//                    break;
//                }
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
//        }
//        new AlertDialog.Builder(this).setMessage("Driver für "+dbPath+" in Drivermanager gefunden=" + hasSqlite).setNeutralButton("OK", null).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        session.clear();
        session.getDatabase().close();
        session = null;
    }

    private void insertDummyPerson() {
        Person jb = new Person(null, "Jasmin", "Bergmann", PersonType.ACITVE.name(), new GregorianCalendar(1986, Calendar.SEPTEMBER, 14).getTime());
        Person mk = new Person(null, "Markus", "Kreth", PersonType.STAFF.name(), new GregorianCalendar(1973, Calendar.AUGUST, 21).getTime());
        PersonDao personDao = session.getPersonDao();
        personDao.insertOrReplace(jb);
        personDao.insertOrReplace(mk);

        RelativeDao relativeDao = session.getRelativeDao();
        Relative rel = new Relative(null, jb.getId(), mk.getId(), RelationType.RELATIONSHIP.name(), RelationType.RELATIONSHIP.name());

        relativeDao.insert(rel);
        personDao.update(jb);
        personDao.update(mk);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(0);
        Person anna = new Person(null, "Anna", "Langenhagen", PersonType.ACITVE.name(), new GregorianCalendar(2006, Calendar.APRIL, 28).getTime());
        Person birgitt = new Person(null, "Birgitt", "Langenhagen", PersonType.RELATIVE.name(), calendar.getTime());
        personDao.insert(anna);
        personDao.insert(birgitt);

        rel = new Relative(null, anna.getId(), birgitt.getId(), RelationType.MOTHER.name(), RelationType.CHILD.name());
        relativeDao.insert(rel);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_settings:
                return true;
            case R.id.action_addPerson:
                frgmt.createNewPerson();
                return true;
            default:

        }
        return super.onOptionsItemSelected(item);
    }

}
