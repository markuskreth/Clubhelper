package de.kreth.clubhelper.backup;

import android.os.Environment;
import android.test.AndroidTestCase;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by markus on 30.03.15.
 */
public class BackupCleanerTest extends AndroidTestCase {

    private final static DateFormat df = new SimpleDateFormat("dd-MM-yy_HH-mm");

    private File backupDir;
    private List<String> stringList;
    private List<File> fileList;

    private TestBackupCleaner cleaner;
//
//    @Override
//    protected void setUp() throws Exception {
//        super.setUp();
//
//        this.backupDir = getContext().getExternalFilesDir(Environment.DIRECTORY_DCIM);
//        assertTrue(backupDir.exists());
//        assertTrue(backupDir.canWrite());
//
//        stringList = new ArrayList<>();
//        fileList = new ArrayList<>();
//
//        for(int year=2013; year<2015;year++) {      // 2 Jahre
//            for (int month= Calendar.JANUARY; month<Calendar.DECEMBER; month+=2) {      // 6 Monate
//
//                for(int day=1;day<29; day+=10) {        // 3 Tage pro Monat
//                    createFileForDate(new GregorianCalendar(year, month, day, 17, 20, 0));
//                    createFileForDate(new GregorianCalendar(year, month, day, 18, 20, 0));
//                }
//            }
//        }
//
//        assertEquals(2*3*6*2, stringList.size());       // 72
//
//    }
//
//    private void createFileForDate(Calendar createTime) throws IOException {
//
//        String fileName = "backup_" + df.format(createTime.getTime()) + ".bak";
//        File file = new File(backupDir, fileName);
//
//        if(!file.exists())
//            file.createNewFile();
//
//        file.setLastModified(createTime.getTimeInMillis());
//        stringList.add(fileName);
//        fileList.add(file);
//        assertTrue(file.exists());
//        assertEquals(createTime.getTimeInMillis(), file.lastModified());
//    }
//
//    @Override
//    protected void tearDown() throws Exception {
//        for (File f: fileList)
//            f.delete();
//        stringList.clear();
//        fileList.clear();
//
//        cleaner = null;
//        super.tearDown();
//    }
//
//    public void testCleanAllExceptOnePerMonth() {
//
//        Calendar now = new GregorianCalendar(2016, Calendar.DECEMBER, 29, 15,0,0);
//        setupCleaner(now);
//
//        assertEquals(0, cleaner.deleted.size());
//        cleaner.run();
//        assertEquals(stringList.size() - 6 * 2, cleaner.deleted.size());        // 60
//    }
//
//    public void testCleanAndIgnoreNewerThanNow() {
//
//        Calendar now = new GregorianCalendar(2013, Calendar.DECEMBER, 29, 15,0,0);
//        setupCleaner(now);
//
//        assertEquals(0, cleaner.deleted.size());
//        cleaner.run();
//        assertEquals(stringList.size() / 2 - 6, cleaner.deleted.size());  // half the defined (older than now) are ignored,
//    }
//
//    public void testCleanButLeaveUnchangedThisMonth() throws IOException {
//
//        String content = "Markus Kreth and other";
//
//        for(int i=2*3*5*2; i<fileList.size(); i++) {
//            File f = fileList.get(i);
//            long lastModified = f.lastModified();
//            FileWriter writer = new FileWriter(f);
//            writer.write(content);
//            writer.close();
//
//            f.setLastModified(lastModified);
//        }
//
//        Calendar now = new GregorianCalendar(2014, Calendar.DECEMBER, 29, 15,0,0);
//        setupCleaner(now);
//
//        assertEquals(0, cleaner.deleted.size());
//        cleaner.run();
//        assertEquals(stringList.size() - 2*6, cleaner.deleted.size());      // 60
//    }
//
//    public void testCleanKeepAnyChangeThisMonth() throws IOException {
//
//        String content = "Markus Kreth and other";
//        int count = 0;
//        int changed = 0;
//        long[] changeTimes = new long[3];
//        for(int i=fileList.size()-6; i<fileList.size(); i++) {
//            File f = fileList.get(i);
//            long lastModified = f.lastModified();
//            FileWriter writer = new FileWriter(f);
//            writer.write(content);
//            writer.close();
//            count++;
//            f.setLastModified(lastModified);
//            if(count%2==0) {
//                content += "\nLine " + count;
//                changeTimes[changed] = lastModified;
//                changed++;
//            }
//        }
//
//        assertEquals(6, count);
//        assertEquals(3, changed);
//
//        Calendar now = new GregorianCalendar(2014, Calendar.DECEMBER, 29, 15,0,0);
//        setupCleaner(now);
//
//        assertEquals(0, cleaner.deleted.size());
//        cleaner.run();
////        assertEquals(stringList.size() - 2*6 + 2, cleaner.deleted.size());
//        for(int i=0; i<3; i++) {
//            long lastModified = fileList.get(fileList.size() - 1 - i).lastModified();
//            assertEquals(lastModified, changeTimes[2-i]);
//        }
//    }
//
//    private void setupCleaner(Calendar now) {
//        cleaner = new TestBackupCleaner(backupDir, stringList.toArray(new String[0]), now);
//    }

    private class TestBackupCleaner extends BackupCleaner {

        public List<File> deleted = new ArrayList<>();
        private Calendar now;

        public TestBackupCleaner(File backupDir, String[] backup_s, Calendar now) {
            super(backupDir, backup_s);
            this.now = now;
        }

        @Override
        protected Date getNow() {
            return now.getTime();
        }

        @Override
        protected boolean delete(File f) {
            deleted.add(f);
            return true;
        }
    }
}
