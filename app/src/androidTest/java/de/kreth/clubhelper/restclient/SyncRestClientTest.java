package de.kreth.clubhelper.restclient;

import android.database.sqlite.SQLiteDatabase;
import android.test.suitebuilder.annotation.MediumTest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.identityscope.IdentityScopeType;
import de.greenrobot.dao.internal.DaoConfig;
import de.greenrobot.dao.query.Query;
import de.greenrobot.dao.query.QueryBuilder;
import de.greenrobot.dao.query.WhereCondition;
import de.kreth.clubhelper.data.Person;
import de.kreth.clubhelper.data.Synchronization;
import de.kreth.clubhelper.dao.DaoSession;
import de.kreth.clubhelper.dao.PersonDao;
import de.kreth.clubhelper.dao.SynchronizationDao;

import static junit.framework.TestCase.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

@MediumTest
@RunWith(MockitoJUnitRunner.class)
public class SyncRestClientTest {

    @Mock private SynchronizationDao syncDao;
    @Mock private QueryBuilder<Synchronization> buiderSynch;
    @Mock private Query<Synchronization> querySynch;

    private SyncRestClient syncClient;

    @Mock private MockDaoSession session;
    private Synchronization synch;
    @Mock private AbstractDao<Person, Long> personDao;
    private List<Person> queryPersons;

    @Before
    public void setup() throws IOException {

        MockitoAnnotations.initMocks(this);

        initSynchMock();
        initPersonMock();

        initMockDaoSession();
        String uri = "";

        syncClient = new SyncRestClient(session, uri);
    }

    private void initPersonMock() {

        queryPersons = new ArrayList<>();
        when(personDao.queryRaw(anyString())).thenReturn(queryPersons);
        when(personDao.getTablename()).thenReturn(PersonDao.TABLENAME);

    }

    private void initSynchMock() {

        synch = new Synchronization();

        when(buiderSynch.where(any(WhereCondition.class))).thenReturn(buiderSynch);
        when(buiderSynch.build()).thenReturn(querySynch);
        when(syncDao.queryBuilder()).thenReturn(buiderSynch);
        when(querySynch.unique()).thenReturn(synch);
    }

    private void initMockDaoSession() {
        when(session.getSynchronizationDao()).thenReturn(syncDao);
        when(session.getDao(any(Class.class))).thenCallRealMethod();
    }

    @Test
    public void testSynchMock() {
        synch.setId(20L);
        synch.setTable_name("table");

        final SynchronizationDao synchronizationDao = session.getSynchronizationDao();
        final QueryBuilder<Synchronization> synchronizationQueryBuilder = synchronizationDao.queryBuilder().where(SynchronizationDao.Properties.Table_name.eq(""));
        final Query<Synchronization> build = synchronizationQueryBuilder.build();
        final Synchronization synchronization = build.unique();
        assertEquals(20L, synchronization.getId().longValue());
        assertEquals("table", synchronization.getTable_name());
    }

    @Test
    public void testSyncClient() {
//        syncClient.updateData(Person.class, Person[].class);
    }

    private class MockDaoSession extends DaoSession {

        private Map<Class<?>, AbstractDao> classToDao;

        public MockDaoSession(SQLiteDatabase db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig> daoConfigMap) {
            super(db, type, daoConfigMap);
            classToDao = new HashMap<>();
        }

        public void put(Class<? extends Object> entityClass, AbstractDao<?, ?> dao) {
            classToDao.put(entityClass, dao);
        }

        @Override
        public AbstractDao<?, ?> getDao(Class<? extends Object> entityClass) {
            if(classToDao.containsKey(entityClass))
                return classToDao.get(entityClass);
            return super.getDao(entityClass);
        }
    }
}
