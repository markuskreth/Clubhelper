package de.kreth.clubhelper.dao;

import android.database.sqlite.SQLiteDatabase;

import java.util.Map;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.AbstractDaoSession;
import de.greenrobot.dao.identityscope.IdentityScopeType;
import de.greenrobot.dao.internal.DaoConfig;

import de.kreth.clubhelper.data.Person;
import de.kreth.clubhelper.data.Contact;
import de.kreth.clubhelper.data.Attendance;
import de.kreth.clubhelper.data.Adress;
import de.kreth.clubhelper.data.Relative;
import de.kreth.clubhelper.data.Group;
import de.kreth.clubhelper.data.PersonGroup;
import de.kreth.clubhelper.data.Synchronization;
import de.kreth.clubhelper.data.DeletedEntries;

import de.kreth.clubhelper.dao.PersonDao;
import de.kreth.clubhelper.dao.ContactDao;
import de.kreth.clubhelper.dao.AttendanceDao;
import de.kreth.clubhelper.dao.AdressDao;
import de.kreth.clubhelper.dao.RelativeDao;
import de.kreth.clubhelper.dao.GroupDao;
import de.kreth.clubhelper.dao.PersonGroupDao;
import de.kreth.clubhelper.dao.SynchronizationDao;
import de.kreth.clubhelper.dao.DeletedEntriesDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see de.greenrobot.dao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig personDaoConfig;
    private final DaoConfig contactDaoConfig;
    private final DaoConfig attendanceDaoConfig;
    private final DaoConfig adressDaoConfig;
    private final DaoConfig relativeDaoConfig;
    private final DaoConfig groupDaoConfig;
    private final DaoConfig personGroupDaoConfig;
    private final DaoConfig synchronizationDaoConfig;
    private final DaoConfig deletedEntriesDaoConfig;

    private final PersonDao personDao;
    private final ContactDao contactDao;
    private final AttendanceDao attendanceDao;
    private final AdressDao adressDao;
    private final RelativeDao relativeDao;
    private final GroupDao groupDao;
    private final PersonGroupDao personGroupDao;
    private final SynchronizationDao synchronizationDao;
    private final DeletedEntriesDao deletedEntriesDao;

    public DaoSession(SQLiteDatabase db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        personDaoConfig = daoConfigMap.get(PersonDao.class).clone();
        personDaoConfig.initIdentityScope(type);

        contactDaoConfig = daoConfigMap.get(ContactDao.class).clone();
        contactDaoConfig.initIdentityScope(type);

        attendanceDaoConfig = daoConfigMap.get(AttendanceDao.class).clone();
        attendanceDaoConfig.initIdentityScope(type);

        adressDaoConfig = daoConfigMap.get(AdressDao.class).clone();
        adressDaoConfig.initIdentityScope(type);

        relativeDaoConfig = daoConfigMap.get(RelativeDao.class).clone();
        relativeDaoConfig.initIdentityScope(type);

        groupDaoConfig = daoConfigMap.get(GroupDao.class).clone();
        groupDaoConfig.initIdentityScope(type);

        personGroupDaoConfig = daoConfigMap.get(PersonGroupDao.class).clone();
        personGroupDaoConfig.initIdentityScope(type);

        synchronizationDaoConfig = daoConfigMap.get(SynchronizationDao.class).clone();
        synchronizationDaoConfig.initIdentityScope(type);

        deletedEntriesDaoConfig = daoConfigMap.get(DeletedEntriesDao.class).clone();
        deletedEntriesDaoConfig.initIdentityScope(type);

        personDao = new PersonDao(personDaoConfig, this);
        contactDao = new ContactDao(contactDaoConfig, this);
        attendanceDao = new AttendanceDao(attendanceDaoConfig, this);
        adressDao = new AdressDao(adressDaoConfig, this);
        relativeDao = new RelativeDao(relativeDaoConfig, this);
        groupDao = new GroupDao(groupDaoConfig, this);
        personGroupDao = new PersonGroupDao(personGroupDaoConfig, this);
        synchronizationDao = new SynchronizationDao(synchronizationDaoConfig, this);
        deletedEntriesDao = new DeletedEntriesDao(deletedEntriesDaoConfig, this);

        registerDao(Person.class, personDao);
        registerDao(Contact.class, contactDao);
        registerDao(Attendance.class, attendanceDao);
        registerDao(Adress.class, adressDao);
        registerDao(Relative.class, relativeDao);
        registerDao(Group.class, groupDao);
        registerDao(PersonGroup.class, personGroupDao);
        registerDao(Synchronization.class, synchronizationDao);
        registerDao(DeletedEntries.class, deletedEntriesDao);
    }
    
    public void clear() {
        personDaoConfig.getIdentityScope().clear();
        contactDaoConfig.getIdentityScope().clear();
        attendanceDaoConfig.getIdentityScope().clear();
        adressDaoConfig.getIdentityScope().clear();
        relativeDaoConfig.getIdentityScope().clear();
        groupDaoConfig.getIdentityScope().clear();
        personGroupDaoConfig.getIdentityScope().clear();
        synchronizationDaoConfig.getIdentityScope().clear();
        deletedEntriesDaoConfig.getIdentityScope().clear();
    }

    public PersonDao getPersonDao() {
        return personDao;
    }

    public ContactDao getContactDao() {
        return contactDao;
    }

    public AttendanceDao getAttendanceDao() {
        return attendanceDao;
    }

    public AdressDao getAdressDao() {
        return adressDao;
    }

    public RelativeDao getRelativeDao() {
        return relativeDao;
    }

    public GroupDao getGroupDao() {
        return groupDao;
    }

    public PersonGroupDao getPersonGroupDao() {
        return personGroupDao;
    }

    public SynchronizationDao getSynchronizationDao() {
        return synchronizationDao;
    }

    public DeletedEntriesDao getDeletedEntriesDao() {
        return deletedEntriesDao;
    }

}
