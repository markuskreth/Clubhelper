package de.kreth.clubhelper.test;

import de.greenrobot.dao.test.AbstractDaoTestLongPk;

import de.kreth.clubhelper.Adress;
import de.kreth.clubhelper.dao.AdressDao;

public class AdressTest extends AbstractDaoTestLongPk<AdressDao, Adress> {

    public AdressTest() {
        super(AdressDao.class);
    }

    @Override
    protected Adress createEntity(Long key) {

        Adress entity = new Adress();
        entity.setId(key);
        return entity;
    }

}
