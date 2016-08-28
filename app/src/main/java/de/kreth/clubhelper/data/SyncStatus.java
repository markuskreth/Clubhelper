package de.kreth.clubhelper.data;

import de.greenrobot.dao.converter.PropertyConverter;

/**
 * Created by markus on 06.03.16.
 */
public enum SyncStatus {
    /**
     * Clientside created, not synchronized
     */
    NEW(0),
    /**
     * Clientside changed since last synchronization
     */
    CHANGED(10),
    /**
     * Synchronized with server, no changes.
     */
    SYNCHRONIZED(100),
    /**
     * Deleted ClientSide.
     */
    DELETED(1000);


    private final int id;

    SyncStatus(int id) {
        this.id = id;
    }

    /**
     * Created by markus on 06.03.16.
     */
    public static class SyncStatusConverter implements PropertyConverter {

        @Override
        public SyncStatus convertToEntityProperty(Object databaseValue) {
            if(databaseValue instanceof Integer) {

                for (SyncStatus st :
                        SyncStatus.values()) {
                    if (databaseValue.equals(st.id))
                        return st;
                }
            }
            return NEW;
        }

        @Override
        public Long convertToDatabaseValue(Object entityProperty) {
            if(entityProperty instanceof SyncStatus) {
                final SyncStatus property = (SyncStatus) entityProperty;
                return Long.valueOf(property.id);
            }
            throw new IllegalArgumentException("Argument not of correct type!: " + entityProperty.getClass());
        }
    }
}
