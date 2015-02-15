package de.kreth.clubhelper.test;

import de.greenrobot.dao.test.AbstractDaoTestLongPk;
import de.kreth.clubhelper.Contact;
import de.kreth.clubhelper.dao.ContactDao;

public class ContactTest extends AbstractDaoTestLongPk<ContactDao, Contact> {

   public ContactTest() {
      super(ContactDao.class);
   }

   @Override
   protected Contact createEntity(Long key) {
      Contact entity = new Contact();
      entity.setId(key);
      return entity;
   }

}
