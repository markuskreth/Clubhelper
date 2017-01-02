package de.kreth.clubhelper.dao;

import android.database.sqlite.SQLiteDatabase;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import de.kreth.clubhelper.data.PersonType;


/**
 * Created by markus on 03.04.15.
 */
public class DaoMigrator {

    private final SQLiteDatabase db;

    public DaoMigrator(SQLiteDatabase db) {
        this.db = db;
    }

    public void start(int oldVersion, int newVersion) {

        try {
            db.beginTransaction();
            for(int migrateVersion = oldVersion+1; migrateVersion<=newVersion; migrateVersion++) {
                switch (migrateVersion) {
                    case 4:
                        migrateToVersion4();
                        break;
                    case 5:
                        migrateToVersion5();
                        break;
                    case 6:
                        migrateToVersion6();
                        break;
                    case 7:
                        migrateToVersion7();
                        break;
                    case 8:
                        migrateToVersion8();
                        break;
                }
            }

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    private void migrateToVersion8() {
        boolean ifNotExists = true;
        GroupDao.createTable(db, ifNotExists);
        PersonGroupDao.createTable(db, ifNotExists);
    }

    private void migrateToVersion7() {

        boolean ifNotExists = true;
        DeletedEntriesDao.createTable(db, ifNotExists);
    }

    private void migrateToVersion6() {
            addColumn(db,
                    PersonDao.TABLENAME,
                    PersonDao.Properties.SyncStatus.columnName, "INTEGER NOT NULL DEFAULT 0");
            addColumn(db,
                    ContactDao.TABLENAME,
                    ContactDao.Properties.SyncStatus.columnName, "INTEGER NOT NULL DEFAULT 0");
            addColumn(db,
                    RelativeDao.TABLENAME,
                    RelativeDao.Properties.SyncStatus.columnName, "INTEGER NOT NULL DEFAULT 0");
            addColumn(db,
                    AdressDao.TABLENAME,
                    AdressDao.Properties.SyncStatus.columnName, "INTEGER NOT NULL DEFAULT 0");
            addColumn(db,
                    AttendanceDao.TABLENAME,
                    AttendanceDao.Properties.SyncStatus.columnName, "INTEGER NOT NULL DEFAULT 0");

    }

    /**
     * Erzeugt sql Befehl, der der Tabelle die Column hinzufügt.
     * @param db    Database
     * @param tablename table to Alter
     * @param columnName    ColumnName to add
     * @param sqlType   type and modifiers for column
     */
    private void addColumn(SQLiteDatabase db, String tablename, String columnName, String sqlType) {
        String sql = "ALTER TABLE \"" + tablename + "\" ADD COLUMN \"" + columnName + "\" " + sqlType;
        db.execSQL(sql);
    }

    private void migrateToVersion5() {

        boolean ifNotExists = true;
        GroupDao.createTable(db, ifNotExists);
        PersonGroupDao.createTable(db, ifNotExists);
        SynchronizationDao.createTable(db, ifNotExists);

    }

    private void migrateToVersion4() {

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
