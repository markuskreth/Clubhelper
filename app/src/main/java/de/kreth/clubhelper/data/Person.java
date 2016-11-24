package de.kreth.clubhelper.data;

import java.util.List;
import de.kreth.clubhelper.dao.DaoSession;
import de.greenrobot.dao.DaoException;

import de.kreth.clubhelper.dao.AdressDao;
import de.kreth.clubhelper.dao.AttendanceDao;
import de.kreth.clubhelper.dao.ContactDao;
import de.kreth.clubhelper.dao.PersonDao;
import de.kreth.clubhelper.dao.PersonGroupDao;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here
import java.util.ArrayList;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
// KEEP INCLUDES END
/**
 * Entity mapped to table "PERSON".
 */
public class Person implements Data {

    private Long id;
    private String prename;
    private String surname;
    private String type;
    private java.util.Date birth;
    /** Not-null value. */
    private java.util.Date changed;
    /** Not-null value. */
    private java.util.Date created;
    private SyncStatus syncStatus;

    /** Used to resolve relations */
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    private transient PersonDao myDao;

    private List<Contact> contactList;
    private List<Attendance> attendanceList;
    private List<Adress> adressList;
    private List<PersonGroup> personGroupList;

    // KEEP FIELDS - put your custom fields here
    // KEEP FIELDS END

    public Person() {
    }

    public Person(Long id) {
        this.id = id;
    }

    public Person(Long id, String prename, String surname, String type, java.util.Date birth, java.util.Date changed, java.util.Date created, SyncStatus syncStatus) {
        this.id = id;
        this.prename = prename;
        this.surname = surname;
        this.type = type;
        this.birth = birth;
        this.changed = changed;
        this.created = created;
        this.syncStatus = syncStatus;
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

    /** Not-null value. */
    public java.util.Date getChanged() {
        return changed;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setChanged(java.util.Date changed) {
        this.changed = changed;
    }

    /** Not-null value. */
    public java.util.Date getCreated() {
        return created;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setCreated(java.util.Date created) {
        this.created = created;
    }

    public SyncStatus getSyncStatus() {
        return syncStatus;
    }

    public void setSyncStatus(SyncStatus syncStatus) {
        this.syncStatus = syncStatus;
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

    /** To-many relationship, resolved on first access (and after reset). Changes to to-many relations are not persisted, make changes to the target entity. */
    public List<PersonGroup> getPersonGroupList() {
        if (personGroupList == null) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            PersonGroupDao targetDao = daoSession.getPersonGroupDao();
            List<PersonGroup> personGroupListNew = targetDao._queryPerson_PersonGroupList(id);
            synchronized (this) {
                if(personGroupList == null) {
                    personGroupList = personGroupListNew;
                }
            }
        }
        return personGroupList;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    public synchronized void resetPersonGroupList() {
        personGroupList = null;
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

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public List<Group> getGroups() {
        List<Group> result = new ArrayList<>();
        final List<PersonGroup> personGroupList = getPersonGroupList();
        for (PersonGroup pg :
                getPersonGroupList()) {
            result.addAll(pg.getGroupList());
        }
        return result;
    }

    public PersonType getPersonType() {
        try {
            return PersonType.valueOf(this.type);
        } catch (Exception e) {
            if(this.type == null || this.type.startsWith("AC")) {
                this.setType(PersonType.ACTIVE.name());
                this.update();
                return PersonType.ACTIVE;
            }
            else
                throw e;
        }
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Person person = (Person) o;

        if (id != null ? !id.equals(person.id) : person.id != null) return false;
        if (prename != null ? !prename.equals(person.prename) : person.prename != null)
            return false;
        if (surname != null ? !surname.equals(person.surname) : person.surname != null)
            return false;
        if (type != null ? !type.equals(person.type) : person.type != null) return false;
        if (birth != null ? !birth.equals(person.birth) : person.birth != null) return false;
        if (changed != null ? !changed.equals(person.changed) : person.changed != null)
            return false;
        if (created != null ? !created.equals(person.created) : person.created != null)
            return false;
        return syncStatus == person.syncStatus;

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (prename != null ? prename.hashCode() : 0);
        result = 31 * result + (surname != null ? surname.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (birth != null ? birth.hashCode() : 0);
        result = 31 * result + (changed != null ? changed.hashCode() : 0);
        result = 31 * result + (created != null ? created.hashCode() : 0);
        result = 31 * result + (syncStatus != null ? syncStatus.hashCode() : 0);
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
