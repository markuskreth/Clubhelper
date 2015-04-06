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
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import de.kreth.clubhelper.Adress;
import de.kreth.clubhelper.Contact;
import de.kreth.clubhelper.Person;
import de.kreth.clubhelper.PersonType;
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
    private final static DateFormat df = new SimpleDateFormat("yyyy-MM-dd_HH-mm");

    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private DaoSession session;
    private File targetDir;

    public BackupRestoreHandler(DaoSession session, File targetDir) {
        this.session = session;
        this.targetDir = targetDir;
    }

    public void doBackup() throws IOException {
        File backupDir = new File(targetDir, EXPORT_DIR_NAME);
        if (! backupDir.exists()) {
            backupDir.mkdirs();
        }

        startBackupCleaner(backupDir);

        File exportFile = new File(backupDir, "backup_" + df.format(new java.util.Date()) + ".bak");

        String json;
        synchronized (session)  {

            session.clear();
            DataExportClass data = new DataExportClass();
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

    private void startBackupCleaner(File backupDir) {
//        File exportFile = new File(backupDir, "backup_" + df.format(new java.util.Date()) + ".bak");
//        String[] backup_s = backupDir.list(new FilenameFilter() {
//            @Override
//            public boolean accept(File dir, String filename) {
//                return filename.startsWith("backup_") && filename.startsWith(".bak");
//            }
//        });
//        ExecutorService exec = Executors.newSingleThreadExecutor();
//        exec.execute(new BackupCleaner(backupDir, backup_s));
//        exec.shutdown();
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
            if(person.getCreated() == null)
                person.setCreated(new GregorianCalendar(2000, Calendar.JANUARY, 1).getTime());
            if(person.getChanged() == null)
                person.setChanged(new Date());
            if(person.getType().matches("ACITVE"))
                person.setType(PersonType.ACTIVE.name());

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
