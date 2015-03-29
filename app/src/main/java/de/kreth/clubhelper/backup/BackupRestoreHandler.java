package de.kreth.clubhelper.backup;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import de.kreth.clubhelper.Adress;
import de.kreth.clubhelper.Contact;
import de.kreth.clubhelper.Person;
import de.kreth.clubhelper.Relative;
import de.kreth.clubhelper.dao.AdressDao;
import de.kreth.clubhelper.dao.ContactDao;
import de.kreth.clubhelper.dao.DaoSession;
import de.kreth.clubhelper.dao.PersonDao;
import de.kreth.clubhelper.dao.RelativeDao;

/**
 * Created by markus on 29.03.15.
 */
public class BackupRestoreHandler {

    public final static String EXPORT_DIR_NAME = "Clubhelper";
    private final static DateFormat df = new SimpleDateFormat("dd-MM-yy_HH-mm");

    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private DaoSession session;
    private File targetDir;

    public BackupRestoreHandler(DaoSession session, File targetDir) {
        this.session = session;
        this.targetDir = targetDir;
    }

    public void doBackup() throws IOException {
        File exportDir = new File(targetDir, EXPORT_DIR_NAME);
        if (! exportDir.exists()) {
            exportDir.mkdirs();
        }
        File exportFile = new File(exportDir, "backup_" + df.format(new java.util.Date()) + ".bak");
        DataExportClass data = new DataExportClass();
        String json;
        synchronized (session)  {

            session.clear();

            data.setPersons(session.getPersonDao().loadAll());
            data.setContacts(session.getContactDao().loadAll());
            data.setAdresses(session.getAdressDao().loadAll());
            data.setRelatives(session.getRelativeDao().loadAll());
            json = gson.toJson(data);

        }

        FileWriter writer = new FileWriter(exportFile);
        try {
            writer.write(json);
        } catch (IOException e) {
            writer.close();
            throw e;
        }
        writer.close();
    }

    public void doRestore(String backupFileName) throws IOException {

        File exportDir = new File(targetDir, EXPORT_DIR_NAME);
        File exportFile = new File(exportDir, backupFileName);
        BufferedReader r = new BufferedReader(new FileReader(exportFile));
        StringBuilder bld = new StringBuilder();
        String line;

        while ((line=r.readLine()) != null) {
            bld.append(line);
        }
        
        r.close();
        DataExportClass data = gson.fromJson(bld.toString(), DataExportClass.class);
        PersonDao personDao = session.getPersonDao();
        ContactDao contactDao = session.getContactDao();
        RelativeDao relativeDao = session.getRelativeDao();
        AdressDao adressDao = session.getAdressDao();

        for (Person person : data.getPersons()) {
            personDao.insertOrReplace(person);
        }
        for (Contact contact : data.getContacts()) {
            contactDao.insertOrReplace(contact);
        }
        for (Relative relative : data.getRelatives()) {
            relativeDao.insertOrReplace(relative);
        }
        for (Adress adress : data.getAdresses()) {
            adressDao.insertOrReplace(adress);
        }
    }

    public String[] listBackups() {

        File exportDir = new File(targetDir, EXPORT_DIR_NAME);
        String[] backups = exportDir.list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String filename) {
                return filename.startsWith("backup_") && filename.endsWith(".bak");
            }
        });
        return backups;
    }
}
