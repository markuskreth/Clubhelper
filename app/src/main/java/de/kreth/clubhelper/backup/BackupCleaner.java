package de.kreth.clubhelper.backup;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by markus on 30.03.15.
 */
public class BackupCleaner implements Runnable {

    private final String[] backupFiles;
    private final File dir;

    public BackupCleaner(File backupDir, String[] backup_s) {
        this.dir = backupDir;
        this.backupFiles = backup_s;
    }

    @Override
    public void run() {

        Map<String, File> files = new HashMap<>();

        for (String fileName : backupFiles)
            files.put(fileName, new File (dir, fileName));

        List<File> fileList = new ArrayList<>(files.values());

        Collections.sort(fileList, new Comparator<File>() {
            @Override
            public int compare(File lhs, File rhs) {
                return new Long(lhs.lastModified()).compareTo(rhs.lastModified());
            }
        });

        Calendar now = new GregorianCalendar();
        now.setTime(getNow());

        Calendar prev = new GregorianCalendar();
        Calendar time = new GregorianCalendar();
        File prevFile;
        for (int i = 1; i < fileList.size(); i++) {

            prevFile = fileList.get(i - 1);
            prev.setTimeInMillis(prevFile.lastModified());
            File currentFile = fileList.get(i);

            time.setTimeInMillis(currentFile.lastModified());

            if (now.before(time))
                break;

            if (time.get(Calendar.YEAR) == prev.get(Calendar.YEAR) && time.get(Calendar.MONTH) == prev.get(Calendar.MONTH) ){

                File file = files.get(prevFile.getName());

                if(file.exists() && file.length() == currentFile.length()){
                    delete(file);
                    files.remove(prevFile.getName());
                }
            }
        }
    }

    protected boolean delete(File f) {
        return f.delete();
    }

    protected Date getNow() {
        return new Date();
    }
}
