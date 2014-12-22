package de.kreth.clubhelper.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import de.kreth.clubhelper.Relative;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table RELATIVE.
*/
public class RelativeDao extends AbstractDao<Relative, Long> {

    public static final String TABLENAME = "RELATIVE";

    /**
     * Properties of entity Relative.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Person1 = new Property(1, long.class, "person1", false, "PERSON1");
        public final static Property Person2 = new Property(2, long.class, "person2", false, "PERSON2");
        public final static Property ToPerson2Relation = new Property(3, String.class, "toPerson2Relation", false, "TO_PERSON2_RELATION");
        public final static Property ToPerson1Relation = new Property(4, String.class, "toPerson1Relation", false, "TO_PERSON1_RELATION");
    };


    public RelativeDao(DaoConfig config) {
        super(config);
    }
    
    public RelativeDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'RELATIVE' (" + //
                "'_id' INTEGER PRIMARY KEY ," + // 0: id
                "'PERSON1' INTEGER NOT NULL ," + // 1: person1
                "'PERSON2' INTEGER NOT NULL ," + // 2: person2
                "'TO_PERSON2_RELATION' TEXT," + // 3: toPerson2Relation
                "'TO_PERSON1_RELATION' TEXT);"); // 4: toPerson1Relation
        // Add Indexes
        db.execSQL("CREATE UNIQUE INDEX " + constraint + "IDX_RELATIVE_PERSON1_PERSON2 ON RELATIVE" +
                " (PERSON1,PERSON2);");
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'RELATIVE'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, Relative entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindLong(2, entity.getPerson1());
        stmt.bindLong(3, entity.getPerson2());
 
        String toPerson2Relation = entity.getToPerson2Relation();
        if (toPerson2Relation != null) {
            stmt.bindString(4, toPerson2Relation);
        }
 
        String toPerson1Relation = entity.getToPerson1Relation();
        if (toPerson1Relation != null) {
            stmt.bindString(5, toPerson1Relation);
        }
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public Relative readEntity(Cursor cursor, int offset) {
        Relative entity = new Relative( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.getLong(offset + 1), // person1
            cursor.getLong(offset + 2), // person2
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // toPerson2Relation
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4) // toPerson1Relation
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, Relative entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setPerson1(cursor.getLong(offset + 1));
        entity.setPerson2(cursor.getLong(offset + 2));
        entity.setToPerson2Relation(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setToPerson1Relation(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(Relative entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(Relative entity) {
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
