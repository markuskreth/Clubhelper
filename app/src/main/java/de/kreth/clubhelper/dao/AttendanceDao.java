package de.kreth.clubhelper.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import java.util.List;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;
import de.greenrobot.dao.query.Query;
import de.greenrobot.dao.query.QueryBuilder;
import de.kreth.clubhelper.Attendance;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * DAO for table ATTENDANCE.
 */
public class AttendanceDao extends AbstractDao<Attendance, Long> {

   public static final String TABLENAME = "ATTENDANCE";
   private Query<Attendance> person_AttendanceListQuery;

   public AttendanceDao(DaoConfig config) {
      super(config);
   }

   public AttendanceDao(DaoConfig config, DaoSession daoSession) {
      super(config, daoSession);
   }

   /**
    * Creates the underlying database table.
    */
   public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
      String constraint = ifNotExists ? "IF NOT EXISTS " : "";
      db.execSQL("CREATE TABLE " + constraint + "'ATTENDANCE' (" + //
                         "'_id' INTEGER PRIMARY KEY ," + // 0: id
                         "'ON_DATE' INTEGER," + // 1: onDate
                         "'PERSON_ID' INTEGER NOT NULL );"); // 2: personId
      // Add Indexes
      db.execSQL("CREATE INDEX " + constraint + "idxAttendance ON ATTENDANCE" +
                         " (ON_DATE,PERSON_ID);");
   }

   /**
    * Drops the underlying database table.
    */
   public static void dropTable(SQLiteDatabase db, boolean ifExists) {
      String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'ATTENDANCE'";
      db.execSQL(sql);
   }

   /**
    * @inheritdoc
    */
   @Override
   protected void bindValues(SQLiteStatement stmt, Attendance entity) {
      stmt.clearBindings();

      Long id = entity.getId();
      if (id != null) {
         stmt.bindLong(1, id);
      }

      java.util.Date onDate = entity.getOnDate();
      if (onDate != null) {
         stmt.bindLong(2, onDate.getTime());
      }
      stmt.bindLong(3, entity.getPersonId());
   }

   /**
    * @inheritdoc
    */
   @Override
   public Long readKey(Cursor cursor, int offset) {
      return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
   }

   /**
    * @inheritdoc
    */
   @Override
   public Attendance readEntity(Cursor cursor, int offset) {
      Attendance entity = new Attendance( //
                                          cursor.isNull(offset + 0) ? null : cursor.getLong(
                                                  offset + 0), // id
                                          cursor.isNull(offset + 1) ? null : new java.util.Date(
                                                  cursor.getLong(offset + 1)), // onDate
                                          cursor.getLong(offset + 2) // personId
      );
      return entity;
   }

   /**
    * @inheritdoc
    */
   @Override
   public void readEntity(Cursor cursor, Attendance entity, int offset) {
      entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
      entity.setOnDate(
              cursor.isNull(offset + 1) ? null : new java.util.Date(cursor.getLong(offset + 1)));
      entity.setPersonId(cursor.getLong(offset + 2));
   }

   /**
    * @inheritdoc
    */
   @Override
   protected Long updateKeyAfterInsert(Attendance entity, long rowId) {
      entity.setId(rowId);
      return rowId;
   }

   /**
    * @inheritdoc
    */
   @Override
   public Long getKey(Attendance entity) {
      if (entity != null) {
         return entity.getId();
      } else {
         return null;
      }
   }

   /**
    * @inheritdoc
    */
   @Override
   protected boolean isEntityUpdateable() {
      return true;
   }

   /**
    * Internal query to resolve the "attendanceList" to-many relationship of Person.
    */
   public List<Attendance> _queryPerson_AttendanceList(long personId) {
      synchronized (this) {
         if (person_AttendanceListQuery == null) {
            QueryBuilder<Attendance> queryBuilder = queryBuilder();
            queryBuilder.where(Properties.PersonId.eq(null));
            person_AttendanceListQuery = queryBuilder.build();
         }
      }
      Query<Attendance> query = person_AttendanceListQuery.forCurrentThread();
      query.setParameter(0, personId);
      return query.list();
   }

   /**
    * Properties of entity Attendance.<br/>
    * Can be used for QueryBuilder and for referencing column names.
    */
   public static class Properties {
      public final static Property Id = new Property(0, Long.class, "id", true, "_id");
      public final static Property OnDate = new Property(1, java.util.Date.class, "onDate", false,
                                                         "ON_DATE");
      public final static Property PersonId = new Property(2, long.class, "personId", false,
                                                           "PERSON_ID");
   }

}
