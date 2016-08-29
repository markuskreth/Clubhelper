package de.kreth.clubhelper.datahelper;

import de.kreth.clubhelper.dao.DaoSession;

/**
 * Can deliver the current DaoSession object.
 * Created by markus on 15.02.15.
 */
public interface SessionHolder {
    DaoSession getSession();
    String getRestServerAdress();
}
