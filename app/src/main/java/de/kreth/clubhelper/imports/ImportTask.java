package de.kreth.clubhelper.imports;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.kreth.clubhelper.Contact;
import de.kreth.clubhelper.MainActivity;
import de.kreth.clubhelper.Person;
import de.kreth.clubhelper.PersonType;

/**
 * Created by markus on 08.03.15.
 */
public class ImportTask extends AsyncTask<File, Void, Void> {

    private static final String sourceDbName = "sourcedb.sqlite";
    private static final String[] contactTypes = {"Mobile", "Telefon", "Email"};

    private MainActivity context;
    private Result result;
    private File sourceDb;
    private Map<Long, Person> persons = new HashMap<>();
    private Map<Long, List<Contact>> contacts = new HashMap<>();

    public ImportTask(MainActivity context, Result result) {
        this.context = context;
        this.result = result;
    }

    @Override
    protected Void doInBackground(File... params) {
        copyFileToDB(params[0]);
        StringBuilder str = new StringBuilder();
        fillPersons();
        return null;
    }

    private void fillPersons() {
        SQLiteDatabase database = getOpenedDb();
        Cursor cursor = database.rawQuery("Select _id, prename, surname, birthdate from PERSON", null);

        while (cursor.moveToNext()) {
            Person p = new Person();
            p.setPrename(cursor.getString(cursor.getColumnIndex("prename")));
            p.setSurname(cursor.getString(cursor.getColumnIndex("surname")));
            Date d = new Date(cursor.getLong(cursor.getColumnIndex("birthdate")));
            p.setBirth(d);
            p.setType(PersonType.ACITVE.name());
            long id = cursor.getLong(cursor.getColumnIndex("_id"));
            persons.put(id, p);
        }

        cursor.close();
        cursor = database.rawQuery("Select personid, type, value from CONTACT order by personid", null);

        List<Contact> contactList = new ArrayList<>();
        long currentPersonId = -1;
        while (cursor.moveToNext()) {
            Contact c = new Contact();
            c.setValue(cursor.getString(cursor.getColumnIndex("value")));
            wrapType(c, cursor.getString(cursor.getColumnIndex("type")));

            long personid = cursor.getLong(cursor.getColumnIndex("personid"));

            if(currentPersonId<0)
                currentPersonId = personid;

            if(currentPersonId != personid) {
                contacts.put(currentPersonId, contactList);
                contactList = new ArrayList<>();
                currentPersonId = personid;
            }
            contactList.add(c);
        }
        database.close();
        if(result != null)
            result.loaded(persons, contacts);
    }

    private void wrapType(Contact c, String type) {
        if(type.toLowerCase(Locale.getDefault()).startsWith("mobi"))
            c.setType(contactTypes[0]);
        else if(type.toLowerCase(Locale.getDefault()).startsWith("tele"))
            c.setType(contactTypes[1]);
        else //if(type.toLowerCase(Locale.getDefault()).matches("email"))   // Email as Default
            c.setType(contactTypes[2]);
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        if(sourceDb != null)
            sourceDb.delete();
    }

    private SQLiteDatabase getOpenedDb() {
        return new SQLiteOpenHelper(context, sourceDbName, null, 1){
            @Override
            public void onCreate(SQLiteDatabase db) {}

            @Override
            public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}

            @Override
            public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
        }.getWritableDatabase();
    }

    private void copyFileToDB(File source) {

        sourceDb = context.getDatabasePath(sourceDbName);

        FileInputStream fileInputStream = null;
        FileOutputStream fileOutputStream = null;

        try {
            fileInputStream = new FileInputStream(source);
            fileOutputStream = new FileOutputStream(sourceDb);
            FileChannel src = fileInputStream.getChannel();
            FileChannel dst = fileOutputStream.getChannel();
            dst.transferFrom(src, 0, src.size());
            src.close();
            dst.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            if(fileInputStream != null)
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            if(fileOutputStream != null)
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }

    }

    public static interface Result {
        void loaded(Map<Long, Person> persons, Map<Long, List<Contact>> contacts);
    }
}
