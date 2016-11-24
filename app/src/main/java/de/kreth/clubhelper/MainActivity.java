package de.kreth.clubhelper;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import de.kreth.clubhelper.activity.ClubView;
import de.kreth.clubhelper.activity.MainFragment;
import de.kreth.clubhelper.activity.PersonEditFragment;
import de.kreth.clubhelper.backup.BackupRestoreHandler;
import de.kreth.clubhelper.dao.DaoMaster;
import de.kreth.clubhelper.dao.DaoSession;
import de.kreth.clubhelper.dao.ProductiveOpenHelper;
import de.kreth.clubhelper.datahelper.SessionHolder;
import de.kreth.clubhelper.restclient.HostFilter;
import de.kreth.clubhelper.restclient.SyncRestClient;

public class MainActivity extends AppCompatActivity implements SessionHolder, MainFragment.OnMainFragmentEventListener {

    public static final String PERSONID = "personId";
    private static final int PERMISSIONS_REQUEST_READWRITE = 17;
    public static String DBNAME = "clubdatabase.sqlite";
    public static Map<String, String> restServers = new HashMap<>();
    public static String serverName;
    private DaoSession session = null;
    private String imeiNo;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            ResourceBundle application = ResourceBundle.getBundle("application");
            DBNAME = application.getString("dbname");
            for (String key : application.keySet()) {
                if (key.startsWith("restServer")) {
                    restServers.put(key.split("\\.")[1], application.getString(key));
                }
            }
            serverName = "Productive";

        } catch (Exception e) {
            Log.i("ch", "Datei nicht gefunden", e);
        }

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            imeiNo = "-1";

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSIONS_REQUEST_READWRITE);

        } else {
            setDefaultServer();
        }

        initDb();

        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            MainFragment fragment = new MainFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, fragment, MainFragment.TAG)
                    .commit();
        }

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for(int i=0; i<permissions.length; i++) {
            String perm = permissions[i];
            int result = grantResults[i];
            if(perm == Manifest.permission.READ_PHONE_STATE
                    && result == PackageManager.PERMISSION_GRANTED) {
                setDefaultServer();
            }
            if(perm == Manifest.permission.WRITE_EXTERNAL_STORAGE && result != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Es werden keine Backups der Daten angelegt. Bitte geben Sie der App Schreibrechte!", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void setDefaultServer() {

        TelephonyManager TM = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        // IMEI No.
        imeiNo = TM.getDeviceId();

        if (imeiNo.matches("0*"))
            serverName = "Emulator2";

    }

// TODO Doppelte Verknüpfungen über Relation verhindern!!!

    private void initDb() {
        SQLiteDatabase db = new ProductiveOpenHelper(this, DBNAME, null).getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        session = daoMaster.newSession();
    }

    @Override
    protected void onDestroy() {

        File externalStorageDirectory = Environment.getExternalStorageDirectory();
        if (externalStorageDirectory.exists() && externalStorageDirectory.canWrite()) {
            try {
                new BackupRestoreHandler(session, externalStorageDirectory).doBackup();
            } catch (IOException e) {
                Toast.makeText(this, "Backup fehlgeschlagen!" + e, Toast.LENGTH_LONG).show();
                Log.e(getClass().getSimpleName(), "Backup fehlgeschlagen!", e);
            }
        } else {
            Toast.makeText(this, "Backup fehlgeschlagen! Externer Speicher nicht beschreibbar!", Toast.LENGTH_LONG).show();
            Log.e(getClass().getSimpleName(), "Backup fehlgeschlagen! Externer Speicher nicht beschreibbar!");
        }
        session.clear();
        session = null;
        super.onDestroy();
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
            case R.id.action_servers:
                chooseServer();
                return true;
            case R.id.action_export:
                showExportOptions();
                return true;
            case R.id.action_toserver:
                sendToServer();
                return true;
            case R.id.action_restore:
                startRestore();
                return true;
            case R.id.action_groups:
                editGroups();
                return true;
            default:

        }
        return super.onOptionsItemSelected(item);
    }

    private void editGroups() {

    }

    private void chooseServer() {

        final List<CharSequence> values = new ArrayList<>();

        int index = 0;
        for (String val : restServers.keySet()) {

            if ((imeiNo.matches("0*") && !val.matches("Home")) || !imeiNo.matches("0*") && !val.matches("Emulator")) {
                values.add(val);
                if (val.equals(serverName))
                    index = values.size() - 1;
            }
        }

        CharSequence[] arr = new CharSequence[values.size()];
        boolean[] checked = new boolean[values.size()];
        checked[index] = true;

        DialogInterface.OnMultiChoiceClickListener li = new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                if (isChecked) {
                    serverName = values.get(which).toString();
                    dialog.dismiss();
                }
            }
        };
        new AlertDialog.Builder(this).setMultiChoiceItems(values.toArray(arr), checked, li).show();
    }

    private void sendToServer() {

        new AlertDialog.Builder(this).setPositiveButton(R.string.lblOK, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final SyncRestClient.SyncFinishedListener listener = new SyncRestClient.SyncFinishedListener() {
                    @Override
                    public void syncFinished(Exception e) {
                        Toast.makeText(MainActivity.this, "Sync beendet.", Toast.LENGTH_LONG).show();
                        refreshFragmentView();
                    }
                };


                final String uri = restServers.get(serverName);
                final List<String> result = new ArrayList<String>();

                HostFilter filter = new HostFilter(result) {
                    @Override
                    protected void onPostExecute(List<String> strings) {
                        super.onPostExecute(strings);

                        if (strings.contains(uri)) {

                            SyncRestClient client = null;
                            client = new SyncRestClient(session, uri, listener);
                            client.execute();
                        } else {
                            Toast.makeText(MainActivity.this, "Server nicht erreichbar: " + uri, Toast.LENGTH_SHORT).show();
                        }
                    }
                };

                filter.execute(uri);

            }
        }).setNegativeButton(R.string.lblCancel, null).setMessage("Server: " + restServers.get(serverName)).show();

    }

    private void startRestore() {
        File externalStorageDirectory = Environment.getExternalStorageDirectory();
        final BackupRestoreHandler restoreHandler = new BackupRestoreHandler(session, externalStorageDirectory);
        final String[] strings = restoreHandler.listBackups();
        if (strings != null && strings.length > 0) {

            new AlertDialog.Builder(this).setTitle("Welches Backup soll wieder eingespielt werden?").setNeutralButton(R.string.lblCancel, null).setItems(strings, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    try {
                        restoreHandler.doRestore(strings[which]);
                        refreshFragmentView();
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

    public void refreshFragmentView() {
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        Fragment fragment = supportFragmentManager.getFragments().get(0);
        if (!fragment.isDetached() && fragment instanceof ClubView) {
            ((ClubView) fragment).refreshView();
        }
    }

    private void showExportOptions() {
    }

    private void exportDbToSD() {

        File sourceFile = new File(session.getDatabase().getPath());
        File sd = Environment.getExternalStorageDirectory();
        File destinationFile = new File(sd, sourceFile.getName());

        FileChannel source = null;
        FileChannel destination = null;
        try {
            source = new FileInputStream(sourceFile).getChannel();
            destination = new FileOutputStream(destinationFile).getChannel();
            destination.transferFrom(source, 0, source.size());
            Toast.makeText(this, "DB Exported!", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Export: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            if (source != null) {
                try {
                    source.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Close Source: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }

            if (destination != null) {

                try {
                    destination.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Close Destination: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
    public String getRestServerAdress() {
        return restServers.get(serverName);
    }

    @Override
    public void editPerson(long personId) {

        PersonEditFragment personEditFragment = new PersonEditFragment();
        Bundle args = new Bundle();
        args.putLong(MainActivity.PERSONID, personId);
        personEditFragment.setArguments(args);

        FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
        tx.replace(R.id.container, personEditFragment, PersonEditFragment.TAG);
        tx.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);

        tx.addToBackStack(personEditFragment.getClass().getName());
        tx.commit();
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Main Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }
}
