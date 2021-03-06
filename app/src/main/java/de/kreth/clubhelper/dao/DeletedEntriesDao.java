package de.kreth.clubhelper.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import de.kreth.clubhelper.data.SyncStatus;
import de.kreth.clubhelper.data.SyncStatus.SyncStatusConverter;

import de.kreth.clubhelper.data.DeletedEntries;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "DELETED_ENTRIES".
*/
public class DeletedEntriesDao extends AbstractDao<DeletedEntries, Long> {

    public static final String TABLENAME = "DELETED_ENTRIES";

    /**
     * Properties of entity DeletedEntries.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Tablename = new Property(1, String.class, "tablename", false, "TABLENAME");
        public final static Property EntryId = new Property(2, Long.class, "entryId", false, "ENTRY_ID");
        public final static Property Changed = new Property(3, java.util.Date.class, "changed", false, "CHANGED");
        public final static Property Created = new Property(4, java.util.Date.class, "created", false, "CREATED");
        public final static Property SyncStatus = new Property(5, Integer.class, "syncStatus", false, "SYNC_STATUS");
    };

    private final SyncStatusConverter syncStatusConverter = new SyncStatusConverter();

    public DeletedEntriesDao(DaoConfig config) {
        super(config);
    }
    
    public DeletedEntriesDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"DELETED_ENTRIES\" (" + //
                "\"_id\" INTEGER PRIMARY KEY ," + // 0: id
                "\"TABLENAME\" TEXT," + // 1: tablename
                "\"ENTRY_ID\" INTEGER," + // 2: entryId
                "\"CHANGED\" INTEGER NOT NULL ," + // 3: changed
                "\"CREATED\" INTEGER NOT NULL ," + // 4: created
                "\"SYNC_STATUS\" INTEGER);"); // 5: syncStatus
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"DELETED_ENTRIES\"";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, DeletedEntries entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String tablename = entity.getTablename();
        if (tablename != null) {
            stmt.bindString(2, tablename);
        }
 
        Long entryId = entity.getEntryId();
        if (entryId != null) {
            stmt.bindLong(3, entryId);
        }
        stmt.bindLong(4, entity.getChanged().getTime());
        stmt.bindLong(5, entity.getCreated().getTime());
 
        SyncStatus syncStatus = entity.getSyncStatus();
        if (syncStatus != null) {
            stmt.bindLong(6, syncStatusConverter.convertToDatabaseValue(syncStatus));
        }
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public DeletedEntries readEntity(Cursor cursor, int offset) {
        DeletedEntries entity = new DeletedEntries( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // tablename
            cursor.isNull(offset + 2) ? null : cursor.getLong(offset + 2), // entryId
            new java.util.Date(cursor.getLong(offset + 3)), // changed
            new java.util.Date(cursor.getLong(offset + 4)), // created
            cursor.isNull(offset + 5) ? null : syncStatusConverter.convertToEntityProperty(cursor.getInt(offset + 5)) // syncStatus
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, DeletedEntries entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setTablename(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setEntryId(cursor.isNull(offset + 2) ? null : cursor.getLong(offset + 2));
        entity.setChanged(new java.util.Date(cursor.getLong(offset + 3)));
        entity.setCreated(new java.util.Date(cursor.getLong(offset + 4)));
        entity.setSyncStatus(cursor.isNull(offset + 5) ? null : syncStatusConverter.convertToEntityProperty(cursor.getInt(offset + 5)));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(DeletedEntries entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(DeletedEntries entity) {
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
