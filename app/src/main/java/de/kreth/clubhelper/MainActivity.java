package de.kreth.clubhelper;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import de.greenrobot.dao.identityscope.IdentityScopeType;
import de.kreth.clubhelper.dao.DaoMaster;
import de.kreth.clubhelper.dao.DaoSession;
import de.kreth.clubhelper.dao.PersonDao;
import de.kreth.clubhelper.dao.RelativeDao;
import de.kreth.clubhelper.dialogs.PersonDialog;
import de.kreth.clubhelper.widgets.PersonAdapter;


public class MainActivity extends ActionBarActivity {

    public static DaoSession session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initDb();

//        insertDummyPerson();

        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment().setSession(session))
                    .commit();
        }
    }

    private void initDb() {
        SQLiteDatabase db = new DaoMaster.DevOpenHelper(this, "clubdatabase.db", null).getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        session = daoMaster.newSession();

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
        jb.getRelativeList().add(rel);
        mk.getRelativeList().add(rel);
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
        anna.getRelativeList().add(rel);
        birgitt.getRelativeList().add(rel);
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

        private List<Person> persons;
        private PersonAdapter adapter;
        private DaoSession session;

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            final PersonDao personDao = session.getPersonDao();
            persons = personDao.loadAll();

            adapter = new PersonAdapter(getActivity(), persons);

            ListView listView = (ListView)rootView.findViewById(R.id.listView);
            listView.setAdapter(adapter);

            listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

                @Override
                public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {

                    final Person person = persons.get(position);

                    AlertDialog.Builder dlg = new AlertDialog.Builder(getActivity());

                    ViewGroup view1 = (ViewGroup) getActivity().getLayoutInflater().inflate(R.layout.person_complete, null);
                    dlg.setView(view1);
                    dlg.setNegativeButton(R.string.lblCancel, null);

                    final PersonDialog p = new PersonDialog(view1, person);

                    dlg.setPositiveButton(R.string.lblSave, new AlertDialog.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            person.setPrename(p.getPrename().toString());
                            person.setSurname(p.getTxtSurname().toString());
                            person.getBirth().setTime(p.getBirthday().getTimeInMillis());
                            personDao.update(person);
                            adapter.notifyDataSetChanged();
                        }
                    });

                    dlg.show();

                    return true;
                }
            });
            return rootView;
        }

        public PlaceholderFragment setSession(DaoSession session) {
            this.session = session;
            return this;
        }

    }
}
