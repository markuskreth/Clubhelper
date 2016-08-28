package de.kreth.clubhelper.dao;

import java.util.Date;

import de.greenrobot.dao.test.AbstractDaoTestLongPk;

import de.kreth.clubhelper.data.Adress;

public class AdressTest extends AbstractDaoTestLongPk<AdressDao, Adress> {

    public AdressTest() {
        super(AdressDao.class);
    }

    @Override
    protected Adress createEntity(Long key) {
        Adress entity = new Adress();
        entity.setId(key);
        entity.setPersonId(1L);
        entity.setChanged(new Date());
        entity.setCreated(new Date());
        return entity;
    }

}
