package de.kreth.clubhelper.data;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by markus on 30.08.15.
 */
public interface Data extends Serializable, Cloneable {

    Long getId() ;

    void setId(Long id);

    Date getChanged();

    void setChanged(Date changed);

    Date getCreated();

    void setCreated(Date created);

    SyncStatus getSyncStatus();

    void setSyncStatus(SyncStatus syncStatus);
}
