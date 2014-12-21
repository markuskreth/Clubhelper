package de.kreth.clubhelper;

import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.ListView;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import de.greenrobot.dao.identityscope.IdentityScopeType;
import de.kreth.clubhelper.dao.DaoMaster;
import de.kreth.clubhelper.dao.DaoSession;
import de.kreth.clubhelper.dao.PersonDao;
import de.kreth.clubhelper.widgets.PersonAdapter;


public class MainActivity extends ActionBarActivity {

    private DaoSession session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SQLiteDatabase db = new DaoMaster.DevOpenHelper(this, "clubdatabase.db", null).getWritableDatabase();

        session = new DaoMaster(db).newSession(IdentityScopeType.Session);

        insertDummyPerson();

        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }

    private void insertDummyPerson() {
        Person mk = new Person(null, "Markus", "Kreth", PersonType.STAFF.name(), new GregorianCalendar(1973, Calendar.AUGUST, 21).getTime());
        PersonDao personDao = session.getPersonDao();
        personDao.insertOrReplace(mk);
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        private DaoSession session;
        private List<Person> persons;
        private PersonAdapter adapter;

        public PlaceholderFragment() {

        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            PersonDao personDao = session.getPersonDao();
            persons = personDao.loadAll();

            adapter = new PersonAdapter(getActivity(), persons);

            ListView listView = (ListView)rootView.findViewById(R.id.listView);
            listView.setAdapter(adapter);
            return rootView;
        }
    }
}
