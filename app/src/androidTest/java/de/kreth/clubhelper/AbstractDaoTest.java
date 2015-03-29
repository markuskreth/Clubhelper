package de.kreth.clubhelper;

import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
import android.util.Log;

import junit.framework.TestCase;

import de.kreth.clubhelper.dao.DaoMaster;
import de.kreth.clubhelper.dao.DaoSession;

/**
 * Created by markus on 23.03.15.
 */
public class AbstractDaoTest extends AndroidTestCase {

    private static final String DBNAME = "daotestdb.sqlite";
    protected DaoSession session;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        SQLiteDatabase db = new DaoMaster.DevOpenHelper(getContext(), DBNAME, null).getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        session = daoMaster.newSession();
    }

    @Override
    protected void tearDown() throws Exception {
        new DbCleaner().cleanSession(session);
        super.tearDown();
    }
}
