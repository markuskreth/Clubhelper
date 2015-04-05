package de.kreth.clubhelper;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import de.kreth.clubhelper.dao.AdressDao;
import de.kreth.clubhelper.dao.AttendanceDao;
import de.kreth.clubhelper.dao.ContactDao;
import de.kreth.clubhelper.dao.DaoSession;
import de.kreth.clubhelper.dao.PersonDao;
import de.kreth.clubhelper.dao.RelativeDao;

/**
 * Created by markus on 28.03.15.
 */
public class DbCleaner {
    public void cleanSession(DaoSession session) {

        if(session == null || session.getDatabase() == null)
            return;
        session.clear();
        session.deleteAll(Attendance.class);
        session.deleteAll(Adress.class);
        session.deleteAll(Relative.class);
        session.deleteAll(Contact.class);
        session.deleteAll(Person.class);
//        SQLiteDatabase database = session.getDatabase();
//
//        try {
//            database.delete(AdressDao.TABLENAME, null, null);
//        } catch (Exception e) {
//            Log.e("", "", e);
//        }
//        try {
//            database.delete(RelativeDao.TABLENAME, null, null);
//        } catch (Exception e) {
//            Log.e("", "", e);
//        }
//        try {
//            database.delete(ContactDao.TABLENAME, null, null);
//        } catch (Exception e) {
//            Log.e("", "", e);
//        }
//        try {
//            database.delete(AttendanceDao.TABLENAME, null, null);
//        } catch (Exception e) {
//            Log.e("", "", e);
//        }
//        try {
//            database.delete(PersonDao.TABLENAME, null, null);
//        } catch (Exception e) {
//            Log.e("", "", e);
//        }
    }
}
