package de.kreth.clubhelper;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import de.kreth.clubhelper.activity.MainFragment;
import de.kreth.clubhelper.activity.PersonEditFragment;
import de.kreth.clubhelper.backup.BackupRestoreHandler;
import de.kreth.clubhelper.dao.ContactDao;
import de.kreth.clubhelper.dao.DaoMaster;
import de.kreth.clubhelper.dao.DaoSession;
import de.kreth.clubhelper.dao.PersonDao;
import de.kreth.clubhelper.dao.RelativeDao;
import de.kreth.clubhelper.datahelper.SessionHolder;
import de.kreth.clubhelper.imports.FileSelectDialogFragment;
import de.kreth.clubhelper.imports.ImportTask;

public class MainActivity extends ActionBarActivity implements SessionHolder, MainFragment.OnMainFragmentEventListener {

    public static String DBNAME = "clubdatabase.sqlite";
    public static final String PERSONID = "personId";

    private static DaoSession session = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            ResourceBundle application = ResourceBundle.getBundle("application");
            DBNAME = application.getString("dbname");
        } catch (Exception e) {
            Log.i("ch", "Datei nicht gefunden", e);
        }

        initDb();

//        insertDummyPerson();

        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            MainFragment fragment = new MainFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, fragment, MainFragment.TAG)
                    .commit();
        }

    }
// TODO Doppelte Verknüpfungen über Relation verhindern!!!
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

        try {
            File externalStorageDirectory = Environment.getExternalStorageDirectory();
            new BackupRestoreHandler(session, externalStorageDirectory).doBackup();
        } catch (IOException e) {
            Toast.makeText(this, "Backup fehlgeschlagen!" + e, Toast.LENGTH_LONG).show();
            Log.e(getClass().getSimpleName(), "Backup fehlgeschlagen!", e);
        }

        session.clear();
//        session.getDatabase().close();
        session = null;
        super.onDestroy();
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

        rel = new Relative(null, anna.getId(), birgitt.getId(), RelationType.PARENT.name(),
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
            case R.id.action_export:
                showExportOptions();
                return true;
            case R.id.action_restore:
                startRestore();
                return true;
            default:

        }
        return super.onOptionsItemSelected(item);
    }

    private void startRestore() {
        File externalStorageDirectory = Environment.getExternalStorageDirectory();
        final BackupRestoreHandler restoreHandler = new BackupRestoreHandler(session, externalStorageDirectory);
        final String[] strings = restoreHandler.listBackups();
        if(strings != null && strings.length>0) {

            new AlertDialog.Builder(this).setTitle("Welches Backup soll wieder eingespielt werden?").setNeutralButton(R.string.lblCancel, null).setItems(strings, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    try {
                        restoreHandler.doRestore(strings[which]);
                        FragmentManager supportFragmentManager = getSupportFragmentManager();
                        Fragment fragment = supportFragmentManager.getFragments().get(0);
                        if( ! fragment.isDetached()) {
                            supportFragmentManager.beginTransaction().detach(fragment).attach(fragment).commit();
                        }
                        Toast.makeText(MainActivity.this, "Wiederherstellen erfolgreich!", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Toast.makeText(MainActivity.this, "Wiederherstellen fehlgeschlagen!" + e, Toast.LENGTH_LONG).show();
                        Log.e(getClass().getSimpleName(), "Wiederherstellen fehlgeschlagen!", e);
                    }
                }
            }).show();
        } else
            Toast.makeText(MainActivity.this, "Keine Backup-Dateien gefunden!", Toast.LENGTH_SHORT).show();
    }

    private void showExportOptions() {
    }

    private void exportDbToSD() {

        File sourceFile = new File(session.getDatabase().getPath());
        File sd = Environment.getExternalStorageDirectory();
        File destinationFile= new File(sd, sourceFile.getName());

        FileChannel source=null;
        FileChannel destination=null;
        try {
            source = new FileInputStream(sourceFile).getChannel();
            destination = new FileOutputStream(destinationFile).getChannel();
            destination.transferFrom(source, 0, source.size());
            Toast.makeText(this, "DB Exported!", Toast.LENGTH_SHORT).show();
        } catch(IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Export: " + e.getMessage(), Toast.LENGTH_LONG);
        } finally {
            if(source != null){
                try {
                    source.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Close Source: " + e.getMessage(), Toast.LENGTH_LONG);
                }
            }

            if(destination != null){

                try {
                    destination.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Close Destination: " + e.getMessage(), Toast.LENGTH_SHORT);
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        int backStackEntryCount = fragmentManager.getBackStackEntryCount();
        if (backStackEntryCount > 0)
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
