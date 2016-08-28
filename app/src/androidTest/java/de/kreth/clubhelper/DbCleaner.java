package de.kreth.clubhelper;

import de.kreth.clubhelper.dao.DaoSession;
import de.kreth.clubhelper.data.Adress;
import de.kreth.clubhelper.data.Attendance;
import de.kreth.clubhelper.data.Contact;
import de.kreth.clubhelper.data.Person;
import de.kreth.clubhelper.data.Relative;

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
