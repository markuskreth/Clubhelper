package de.kreth.clubhelper;

import java.util.List;
import de.kreth.clubhelper.dao.DaoSession;
import de.greenrobot.dao.DaoException;

import de.kreth.clubhelper.dao.AdressDao;
import de.kreth.clubhelper.dao.AttendanceDao;
import de.kreth.clubhelper.dao.ContactDao;
import de.kreth.clubhelper.dao.PersonDao;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.util.ArrayList;
import de.greenrobot.dao.AbstractDao;
// KEEP INCLUDES END
/**
 * Entity mapped to table PERSON.
 */
public class Person implements java.io.Serializable {

    private Long id;
    private String prename;
    private String surname;
    private String type;
    private java.util.Date birth;

    /** Used to resolve relations */
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    private transient PersonDao myDao;

    private List<Contact> contactList;
    private List<Attendance> attendanceList;
    private List<Adress> adressList;

    // KEEP FIELDS - put your custom fields here
    // KEEP FIELDS END

    public Person() {
    }

    public Person(Long id) {
        this.id = id;
    }

    public Person(Long id, String prename, String surname, String type, java.util.Date birth) {
        this.id = id;
        this.prename = prename;
        this.surname = surname;
        this.type = type;
        this.birth = birth;
    }

    /** called by internal mechanisms, do not call yourself. */
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getPersonDao() : null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPrename() {
        return prename;
    }

    public void setPrename(String prename) {
        this.prename = prename;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public java.util.Date getBirth() {
        return birth;
    }

    public void setBirth(java.util.Date birth) {
        this.birth = birth;
    }

    /** To-many relationship, resolved on first access (and after reset). Changes to to-many relations are not persisted, make changes to the target entity. */
    public List<Contact> getContactList() {
        if (contactList == null) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            ContactDao targetDao = daoSession.getContactDao();
            List<Contact> contactListNew = targetDao._queryPerson_ContactList(id);
            synchronized (this) {
                if(contactList == null) {
                    contactList = contactListNew;
                }
            }
        }
        return contactList;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    public synchronized void resetContactList() {
        contactList = null;
    }

    /** To-many relationship, resolved on first access (and after reset). Changes to to-many relations are not persisted, make changes to the target entity. */
    public List<Attendance> getAttendanceList() {
        if (attendanceList == null) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            AttendanceDao targetDao = daoSession.getAttendanceDao();
            List<Attendance> attendanceListNew = targetDao._queryPerson_AttendanceList(id);
            synchronized (this) {
                if(attendanceList == null) {
                    attendanceList = attendanceListNew;
                }
            }
        }
        return attendanceList;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    public synchronized void resetAttendanceList() {
        attendanceList = null;
    }

    /** To-many relationship, resolved on first access (and after reset). Changes to to-many relations are not persisted, make changes to the target entity. */
    public List<Adress> getAdressList() {
        if (adressList == null) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            AdressDao targetDao = daoSession.getAdressDao();
            List<Adress> adressListNew = targetDao._queryPerson_AdressList(id);
            synchronized (this) {
                if(adressList == null) {
                    adressList = adressListNew;
                }
            }
        }
        return adressList;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    public synchronized void resetAdressList() {
        adressList = null;
    }

    /** Convenient call for {@link AbstractDao#delete(Object)}. Entity must attached to an entity context. */
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.delete(this);
    }

    /** Convenient call for {@link AbstractDao#update(Object)}. Entity must attached to an entity context. */
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.update(this);
    }

    /** Convenient call for {@link AbstractDao#refresh(Object)}. Entity must attached to an entity context. */
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.refresh(this);
    }

    // KEEP METHODS - put your custom methods here

    public PersonType getPersonType() {
        return PersonType.valueOf(this.type);
    }

    public void setPersonType(PersonType type) {
        setType(type.name());
    }

    @Override
    public String toString() {
        return id + ": " + prename + " " + surname;
    }

    public List<RelativeType> getRelations() {
        SQLiteDatabase db = daoSession.getDatabase();

        String sql = "select RELATIVE.PERSON1 as PersonID, RELATIVE.TO_PERSON1_RELATION from RELATIVE WHERE RELATIVE.PERSON2=" + id +
                " UNION \n" +
                "select RELATIVE.PERSON2 AS PersonID, RELATIVE.TO_PERSON2_RELATION from RELATIVE WHERE RELATIVE.PERSON1=" + id;
        Cursor cursor = db.rawQuery(
                sql,
                null);

        List<RelativeType> result = new ArrayList<>();
        while (cursor.moveToNext()) {
            long relId = cursor.getLong(0);
            RelativeType t = new RelativeType();
            t.rel = myDao.load(relId);
            t.type = RelationType.valueOf(cursor.getString(1));
            result.add(t);
        }
        cursor.close();

        return result;
    }

    public class RelativeType {
        private RelationType type;
        private Person rel;

        private RelativeType() {
        }

        public Person getRel() {
            return rel;
        }

        public RelationType getType() {
            return type;
        }
    }
    // KEEP METHODS END

}
