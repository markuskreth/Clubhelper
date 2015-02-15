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
import de.kreth.clubhelper.Contact;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * DAO for table CONTACT.
 */
public class ContactDao extends AbstractDao<Contact, Long> {

   public static final String TABLENAME = "CONTACT";
   private Query<Contact> person_ContactListQuery;

   public ContactDao(DaoConfig config) {
      super(config);
   }

   public ContactDao(DaoConfig config, DaoSession daoSession) {
      super(config, daoSession);
   }

   /**
    * Creates the underlying database table.
    */
   public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
      String constraint = ifNotExists ? "IF NOT EXISTS " : "";
      db.execSQL("CREATE TABLE " + constraint + "'CONTACT' (" + //
                         "'_id' INTEGER PRIMARY KEY ," + // 0: id
                         "'TYPE' TEXT," + // 1: type
                         "'VALUE' TEXT," + // 2: value
                         "'PERSON_ID' INTEGER NOT NULL );"); // 3: personId
   }

   /**
    * Drops the underlying database table.
    */
   public static void dropTable(SQLiteDatabase db, boolean ifExists) {
      String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'CONTACT'";
      db.execSQL(sql);
   }

   /**
    * @inheritdoc
    */
   @Override
   protected void bindValues(SQLiteStatement stmt, Contact entity) {
      stmt.clearBindings();

      Long id = entity.getId();
      if (id != null) {
         stmt.bindLong(1, id);
      }

      String type = entity.getType();
      if (type != null) {
         stmt.bindString(2, type);
      }

      String value = entity.getValue();
      if (value != null) {
         stmt.bindString(3, value);
      }
      stmt.bindLong(4, entity.getPersonId());
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
   public Contact readEntity(Cursor cursor, int offset) {
      Contact entity = new Contact( //
                                    cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0),
                                    // id
                                    cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1),
                                    // type
                                    cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2),
                                    // value
                                    cursor.getLong(offset + 3) // personId
      );
      return entity;
   }

   /**
    * @inheritdoc
    */
   @Override
   public void readEntity(Cursor cursor, Contact entity, int offset) {
      entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
      entity.setType(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
      entity.setValue(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
      entity.setPersonId(cursor.getLong(offset + 3));
   }

   /**
    * @inheritdoc
    */
   @Override
   protected Long updateKeyAfterInsert(Contact entity, long rowId) {
      entity.setId(rowId);
      return rowId;
   }

   /**
    * @inheritdoc
    */
   @Override
   public Long getKey(Contact entity) {
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
    * Internal query to resolve the "contactList" to-many relationship of Person.
    */
   public List<Contact> _queryPerson_ContactList(long personId) {
      synchronized (this) {
         if (person_ContactListQuery == null) {
            QueryBuilder<Contact> queryBuilder = queryBuilder();
            queryBuilder.where(Properties.PersonId.eq(null));
            person_ContactListQuery = queryBuilder.build();
         }
      }
      Query<Contact> query = person_ContactListQuery.forCurrentThread();
      query.setParameter(0, personId);
      return query.list();
   }

   /**
    * Properties of entity Contact.<br/>
    * Can be used for QueryBuilder and for referencing column names.
    */
   public static class Properties {
      public final static Property Id = new Property(0, Long.class, "id", true, "_id");
      public final static Property Type = new Property(1, String.class, "type", false, "TYPE");
      public final static Property Value = new Property(2, String.class, "value", false, "VALUE");
      public final static Property PersonId = new Property(3, long.class, "personId", false,
                                                           "PERSON_ID");
   }

}
