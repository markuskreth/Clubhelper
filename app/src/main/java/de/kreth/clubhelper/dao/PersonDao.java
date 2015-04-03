package de.kreth.clubhelper.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import de.kreth.clubhelper.Person;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table PERSON.
*/
public class PersonDao extends AbstractDao<Person, Long> {

    public static final String TABLENAME = "PERSON";

    /**
     * Properties of entity Person.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Prename = new Property(1, String.class, "prename", false, "PRENAME");
        public final static Property Surname = new Property(2, String.class, "surname", false, "SURNAME");
        public final static Property Type = new Property(3, String.class, "type", false, "TYPE");
        public final static Property Birth = new Property(4, java.util.Date.class, "birth", false, "BIRTH");
        public final static Property Changed = new Property(5, java.util.Date.class, "changed", false, "CHANGED");
        public final static Property Created = new Property(6, java.util.Date.class, "created", false, "CREATED");
    };

    private DaoSession daoSession;


    public PersonDao(DaoConfig config) {
        super(config);
    }
    
    public PersonDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
        this.daoSession = daoSession;
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'PERSON' (" + //
                "'_id' INTEGER PRIMARY KEY ," + // 0: id
                "'PRENAME' TEXT," + // 1: prename
                "'SURNAME' TEXT," + // 2: surname
                "'TYPE' TEXT," + // 3: type
                "'BIRTH' INTEGER," + // 4: birth
                "'CHANGED' INTEGER NOT NULL ," + // 5: changed
                "'CREATED' INTEGER NOT NULL );"); // 6: created
        // Add Indexes
        db.execSQL("CREATE INDEX " + constraint + "idx_name ON PERSON" +
                " (PRENAME,SURNAME);");
        db.execSQL("CREATE INDEX " + constraint + "IDX_PERSON_TYPE ON PERSON" +
                " (TYPE);");
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'PERSON'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, Person entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String prename = entity.getPrename();
        if (prename != null) {
            stmt.bindString(2, prename);
        }
 
        String surname = entity.getSurname();
        if (surname != null) {
            stmt.bindString(3, surname);
        }
 
        String type = entity.getType();
        if (type != null) {
            stmt.bindString(4, type);
        }
 
        java.util.Date birth = entity.getBirth();
        if (birth != null) {
            stmt.bindLong(5, birth.getTime());
        }
        stmt.bindLong(6, entity.getChanged().getTime());
        stmt.bindLong(7, entity.getCreated().getTime());
    }

    @Override
    protected void attachEntity(Person entity) {
        super.attachEntity(entity);
        entity.__setDaoSession(daoSession);
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public Person readEntity(Cursor cursor, int offset) {
        Person entity = new Person( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // prename
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // surname
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // type
            cursor.isNull(offset + 4) ? null : new java.util.Date(cursor.getLong(offset + 4)), // birth
            new java.util.Date(cursor.getLong(offset + 5)), // changed
            new java.util.Date(cursor.getLong(offset + 6)) // created
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, Person entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setPrename(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setSurname(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setType(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setBirth(cursor.isNull(offset + 4) ? null : new java.util.Date(cursor.getLong(offset + 4)));
        entity.setChanged(new java.util.Date(cursor.getLong(offset + 5)));
        entity.setCreated(new java.util.Date(cursor.getLong(offset + 6)));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(Person entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(Person entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    /** @inheritdoc */
    @Override    
    protected boolean isEntityUpdateable() {
        return true;
    }
    
}
