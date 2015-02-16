package de.kreth.clubhelper;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.ResourceBundle;

import de.kreth.clubhelper.activity.MainFragment;
import de.kreth.clubhelper.activity.PersonEditFragment;
import de.kreth.clubhelper.dao.DaoMaster;
import de.kreth.clubhelper.dao.DaoSession;
import de.kreth.clubhelper.dao.PersonDao;
import de.kreth.clubhelper.dao.RelativeDao;
import de.kreth.clubhelper.datahelper.SessionHolder;

public class MainActivity extends ActionBarActivity implements SessionHolder, MainFragment.OnMainFragmentEventListener {

   public static String DBNAME = "clubdatabase.db";
   public static final String PERSONID = "personId";

   private static DaoSession session = null;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      initDb();

//        insertDummyPerson();

      setContentView(R.layout.activity_main);
      if (savedInstanceState == null) {
         MainFragment fragment = new MainFragment();
         getSupportFragmentManager().beginTransaction()
                 .add(R.id.container, fragment)
                 .commit();
      }

   }

   public static SessionHolder getSessionHolder() {
      return new SessionHolder() {

         @Override
         public DaoSession getSession() {
            return session;
         }
      };
   }

   private void initDb() {
      SQLiteDatabase db = new DaoMaster.DevOpenHelper(this, DBNAME, null).getWritableDatabase();
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
      Person jb = new Person(null, "Jasmin", "Bergmann", PersonType.ACITVE.name(),
                             new GregorianCalendar(1986, Calendar.SEPTEMBER, 14).getTime());
      Person mk = new Person(null, "Markus", "Kreth", PersonType.STAFF.name(),
                             new GregorianCalendar(1973, Calendar.AUGUST, 21).getTime());
      PersonDao personDao = session.getPersonDao();
      personDao.insertOrReplace(jb);
      personDao.insertOrReplace(mk);

      RelativeDao relativeDao = session.getRelativeDao();
      Relative rel = new Relative(null, jb.getId(), mk.getId(), RelationType.RELATIONSHIP.name(),
                                  RelationType.RELATIONSHIP.name());

      relativeDao.insert(rel);
      personDao.update(jb);
      personDao.update(mk);
      Calendar calendar = Calendar.getInstance();
      calendar.setTimeInMillis(0);
      Person anna = new Person(null, "Anna", "Langenhagen", PersonType.ACITVE.name(),
                               new GregorianCalendar(2006, Calendar.APRIL, 28).getTime());
      Person birgitt = new Person(null, "Birgitt", "Langenhagen", PersonType.RELATIVE.name(),
                                  calendar.getTime());
      personDao.insert(anna);
      personDao.insert(birgitt);

      rel = new Relative(null, anna.getId(), birgitt.getId(), RelationType.MOTHER.name(),
                         RelationType.CHILD.name());
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

      switch (item.getItemId()) {
         case R.id.action_settings:
            return true;
         default:

      }
      return super.onOptionsItemSelected(item);
   }

   @Override
   public void onBackPressed() {
      FragmentManager fragmentManager = getSupportFragmentManager();
      int backStackEntryCount = fragmentManager.getBackStackEntryCount();
      if(backStackEntryCount>0)
         fragmentManager.popBackStack();
      else
         super.onBackPressed();
   }

   @Override
   public DaoSession getSession() {
      return session;
   }

   @Override
   public void editPerson(long personId) {

      PersonEditFragment personEditFragment = new PersonEditFragment();
      Bundle args = new Bundle();
      args.putLong(MainActivity.PERSONID, personId);
      personEditFragment.setArguments(args);

      FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
      tx.replace(R.id.container, personEditFragment, PersonEditFragment.TAG);
      tx.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);

      tx.addToBackStack(personEditFragment.getClass().getName());
      tx.commit();
   }
}
