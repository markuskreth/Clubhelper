package de.kreth.clubhelper.dao;

import java.util.Date;

import de.greenrobot.dao.test.AbstractDaoTestLongPk;

import de.kreth.clubhelper.Person;
import de.kreth.clubhelper.dao.PersonDao;

public class PersonTest extends AbstractDaoTestLongPk<PersonDao, Person> {

    public PersonTest() {
        super(PersonDao.class);
    }

    @Override
    protected Person createEntity(Long key) {
        Person entity = new Person();
        entity.setId(key);
        entity.setCreated(new Date());
        entity.setChanged(new Date());
        return entity;
    }

}
