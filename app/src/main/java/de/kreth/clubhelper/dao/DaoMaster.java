package de.kreth.clubhelper.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import de.greenrobot.dao.AbstractDaoMaster;
import de.greenrobot.dao.identityscope.IdentityScopeType;

import de.kreth.clubhelper.PersonType;
import de.kreth.clubhelper.dao.PersonDao;
import de.kreth.clubhelper.dao.ContactDao;
import de.kreth.clubhelper.dao.AttendanceDao;
import de.kreth.clubhelper.dao.AdressDao;
import de.kreth.clubhelper.dao.RelativeDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * Master of DAO (schema version 4): knows all DAOs.
*/
public class DaoMaster extends AbstractDaoMaster {
    public static final int SCHEMA_VERSION = 4;

    /** Creates underlying database table using DAOs. */
    public static void createAllTables(SQLiteDatabase db, boolean ifNotExists) {
        PersonDao.createTable(db, ifNotExists);
        ContactDao.createTable(db, ifNotExists);
        AttendanceDao.createTable(db, ifNotExists);
        AdressDao.createTable(db, ifNotExists);
        RelativeDao.createTable(db, ifNotExists);
    }
    
    /** Drops underlying database table using DAOs. */
    public static void dropAllTables(SQLiteDatabase db, boolean ifExists) {
        PersonDao.dropTable(db, ifExists);
        ContactDao.dropTable(db, ifExists);
        AttendanceDao.dropTable(db, ifExists);
        AdressDao.dropTable(db, ifExists);
        RelativeDao.dropTable(db, ifExists);
    }
    
    public static abstract class OpenHelper extends SQLiteOpenHelper {

        public OpenHelper(Context context, String name, CursorFactory factory) {
            super(context, name, factory, SCHEMA_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.i("greenDAO", "Creating tables for schema version " + SCHEMA_VERSION);
            createAllTables(db, false);
        }
    }

    /** WARNING: Drops all table on Upgrade! Use only during development. */
    public static class DevOpenHelper extends OpenHelper {
        public DevOpenHelper(Context context, String name, CursorFactory factory) {
            super(context, name, factory);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.i("greenDAO", "Upgrading schema from version " + oldVersion + " to " + newVersion + " by dropping all tables");
            for(int migrateVersion = oldVersion+1; migrateVersion<=newVersion; migrateVersion++) {
                switch (migrateVersion) {
                    case 4:
                        migrateToVersion4(db);
                        break;
                }
            }
        }

        private void migrateToVersion4(SQLiteDatabase db) {
            RuntimeException exception = null;
            try {
                db.beginTransaction();
                db.execSQL("UPDATE " + PersonDao.TABLENAME + " SET " +
                        PersonDao.Properties.Type.columnName + "='" + PersonType.ACTIVE.name() + "' " +
                        "WHERE " + PersonDao.Properties.Type.columnName + "='ACITVE'");

                Date created = new GregorianCalendar(2000, Calendar.JANUARY, 1).getTime();
                Date changed = new Date();

                updateTableTo4(db,
                        PersonDao.TABLENAME,
                        PersonDao.Properties.Created.columnName,
                        PersonDao.Properties.Changed.columnName,
                        created,
                        changed);

                updateTableTo4(db,
                        ContactDao.TABLENAME,
                        ContactDao.Properties.Created.columnName,
                        ContactDao.Properties.Changed.columnName,
                        created,
                        changed);

                updateTableTo4(db,
                        RelativeDao.TABLENAME,
                        RelativeDao.Properties.Created.columnName,
                        RelativeDao.Properties.Changed.columnName,
                        created,
                        changed);

                updateTableTo4(db,
                        AdressDao.TABLENAME,
                        AdressDao.Properties.Created.columnName,
                        AdressDao.Properties.Changed.columnName,
                        created,
                        changed);

                updateTableTo4(db,
                        AttendanceDao.TABLENAME,
                        AttendanceDao.Properties.Created.columnName,
                        AttendanceDao.Properties.Changed.columnName,
                        created,
                        changed);

                db.setTransactionSuccessful();
            } catch (RuntimeException e) {
                exception = e;
            } finally {
                db.endTransaction();
            }

            if(exception != null)
                throw exception;
        }

        private void updateTableTo4(SQLiteDatabase db, String tableName, String createColName, String changeColName, Date created, Date changed) {

            String sql = "ALTER TABLE " + tableName + " ADD COLUMN 'CHANGED' INTEGER NOT NULL DEFAULT 0";
            db.execSQL(sql);
            sql = "ALTER TABLE " + tableName + " ADD COLUMN 'CREATED' INTEGER NOT NULL DEFAULT 0";
            db.execSQL(sql);

            db.execSQL("UPDATE " + tableName + " SET " +
                    changeColName + "=" + changed.getTime() + ", " +
                    createColName + "=" + created.getTime() + " ");

        }

        @Override
        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            super.onDowngrade(db, oldVersion, newVersion);
        }
    }

    public DaoMaster(SQLiteDatabase db) {
        super(db, SCHEMA_VERSION);
        registerDaoClass(PersonDao.class);
        registerDaoClass(ContactDao.class);
        registerDaoClass(AttendanceDao.class);
        registerDaoClass(AdressDao.class);
        registerDaoClass(RelativeDao.class);
    }
    
    public DaoSession newSession() {
        return new DaoSession(db, IdentityScopeType.Session, daoConfigMap);
    }
    
    public DaoSession newSession(IdentityScopeType type) {
        return new DaoSession(db, type, daoConfigMap);
    }
    
}
