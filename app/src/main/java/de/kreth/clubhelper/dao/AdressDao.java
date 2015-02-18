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
import de.kreth.clubhelper.Adress;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * DAO for table ADRESS.
 */
public class AdressDao extends AbstractDao<Adress, Long> {

   public static final String TABLENAME = "ADRESS";
   private Query<Adress> person_AdressListQuery;

   public AdressDao(DaoConfig config) {
      super(config);
   }

   public AdressDao(DaoConfig config, DaoSession daoSession) {
      super(config, daoSession);
   }

   /**
    * Creates the underlying database table.
    */
   public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
      String constraint = ifNotExists ? "IF NOT EXISTS " : "";
      db.execSQL("CREATE TABLE " + constraint + "'ADRESS' (" + //
                         "'_id' INTEGER PRIMARY KEY ," + // 0: id
                         "'ADRESS1' TEXT," + // 1: adress1
                         "'ADRESS2' TEXT," + // 2: adress2
                         "'PLZ' TEXT," + // 3: plz
                         "'CITY' TEXT," + // 4: city
                         "'PERSON_ID' INTEGER NOT NULL );"); // 5: personId
   }

   /**
    * Drops the underlying database table.
    */
   public static void dropTable(SQLiteDatabase db, boolean ifExists) {
      String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'ADRESS'";
      db.execSQL(sql);
   }

   /**
    * @inheritdoc
    */
   @Override
   protected void bindValues(SQLiteStatement stmt, Adress entity) {
      stmt.clearBindings();

      Long id = entity.getId();
      if (id != null) {
         stmt.bindLong(1, id);
      }

      String adress1 = entity.getAdress1();
      if (adress1 != null) {
         stmt.bindString(2, adress1);
      }

      String adress2 = entity.getAdress2();
      if (adress2 != null) {
         stmt.bindString(3, adress2);
      }

      String plz = entity.getPlz();
      if (plz != null) {
         stmt.bindString(4, plz);
      }

      String city = entity.getCity();
      if (city != null) {
         stmt.bindString(5, city);
      }
      stmt.bindLong(6, entity.getPersonId());
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
   public Adress readEntity(Cursor cursor, int offset) {
      Adress entity = new Adress( //
                                  cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0),
                                  // id
                                  cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1),
                                  // adress1
                                  cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2),
                                  // adress2
                                  cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3),
                                  // plz
                                  cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4),
                                  // city
                                  cursor.getLong(offset + 5) // personId
      );
      return entity;
   }

   /**
    * @inheritdoc
    */
   @Override
   public void readEntity(Cursor cursor, Adress entity, int offset) {
      entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
      entity.setAdress1(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
      entity.setAdress2(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
      entity.setPlz(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
      entity.setCity(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
      entity.setPersonId(cursor.getLong(offset + 5));
   }

   /**
    * @inheritdoc
    */
   @Override
   protected Long updateKeyAfterInsert(Adress entity, long rowId) {
      entity.setId(rowId);
      return rowId;
   }

   /**
    * @inheritdoc
    */
   @Override
   public Long getKey(Adress entity) {
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
    * Internal query to resolve the "adressList" to-many relationship of Person.
    */
   public List<Adress> _queryPerson_AdressList(long personId) {
      synchronized (this) {
         if (person_AdressListQuery == null) {
            QueryBuilder<Adress> queryBuilder = queryBuilder();
            queryBuilder.where(Properties.PersonId.eq(null));
            person_AdressListQuery = queryBuilder.build();
         }
      }
      Query<Adress> query = person_AdressListQuery.forCurrentThread();
      query.setParameter(0, personId);
      return query.list();
   }

   /**
    * Properties of entity Adress.<br/>
    * Can be used for QueryBuilder and for referencing column names.
    */
   public static class Properties {
      public final static Property Id = new Property(0, Long.class, "id", true, "_id");
      public final static Property Adress1 = new Property(1, String.class, "adress1", false,
                                                          "ADRESS1");
      public final static Property Adress2 = new Property(2, String.class, "adress2", false,
                                                          "ADRESS2");
      public final static Property Plz = new Property(3, String.class, "plz", false, "PLZ");
      public final static Property City = new Property(4, String.class, "city", false, "CITY");
      public final static Property PersonId = new Property(5, long.class, "personId", false,
                                                           "PERSON_ID");
   }

}
