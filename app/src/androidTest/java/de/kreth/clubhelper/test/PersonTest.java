package de.kreth.clubhelper.test;

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
        return entity;
    }

}
