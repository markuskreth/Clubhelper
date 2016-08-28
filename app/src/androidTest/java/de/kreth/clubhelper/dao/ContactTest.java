package de.kreth.clubhelper.dao;

import java.util.Date;

import de.greenrobot.dao.test.AbstractDaoTestLongPk;

import de.kreth.clubhelper.data.Contact;

public class ContactTest extends AbstractDaoTestLongPk<ContactDao, Contact> {

    public ContactTest() {
        super(ContactDao.class);
    }

    @Override
    protected Contact createEntity(Long key) {
        Contact entity = new Contact();
        entity.setId(key);
        entity.setPersonId(1L);
        entity.setChanged(new Date());
        entity.setCreated(new Date());
        return entity;
    }

}
