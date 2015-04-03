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

        List<File> iteratable = new ArrayList<>(files.values());

        Collections.sort(iteratable, new Comparator<File>() {
            @Override
            public int compare(File lhs, File rhs) {
                return new Long(lhs.lastModified()).compareTo(rhs.lastModified());
            }
        });

        int currentMonth = -1;
        Calendar now = new GregorianCalendar();
        now.setTime(getNow());

        for (int i = 1; i < iteratable.size(); i++) {
            Calendar prev = new GregorianCalendar();
            prev.setTimeInMillis(iteratable.get(i-1).lastModified());
            File currentFile = iteratable.get(i);
            Calendar time = new GregorianCalendar();
            time.setTimeInMillis(currentFile.lastModified());

            if (now.before(time))
                break;

            if (time.get(Calendar.YEAR) == prev.get(Calendar.YEAR) && time.get(Calendar.MONTH) == prev.get(Calendar.MONTH)){
                File file = files.get(iteratable.get(i - 1).getName());
                if(file.length() == currentFile.length())
                    delete(file);
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
