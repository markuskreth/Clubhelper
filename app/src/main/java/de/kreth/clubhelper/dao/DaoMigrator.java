package de.kreth.clubhelper.dao;

import android.database.sqlite.SQLiteDatabase;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import de.kreth.clubhelper.PersonType;

/**
 * Created by markus on 03.04.15.
 */
public class DaoMigrator {

    private final SQLiteDatabase db;

    public DaoMigrator(SQLiteDatabase db) {
        this.db = db;
    }

    public void start(int oldVersion, int newVersion) {

        for(int migrateVersion = oldVersion+1; migrateVersion<=newVersion; migrateVersion++) {
            switch (migrateVersion) {
                case 4:
                    migrateToVersion4();
                    break;
                case 5:
                    migrateToVersion5();
                    break;
            }
        }
    }

    private void migrateToVersion5() {
        try {
            db.beginTransaction();

            boolean ifNotExists = true;
            GroupDao.createTable(db, ifNotExists);
            PersonGroupDao.createTable(db, ifNotExists);
            SynchronizationDao.createTable(db, ifNotExists);

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    private void migrateToVersion4() {
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

}
