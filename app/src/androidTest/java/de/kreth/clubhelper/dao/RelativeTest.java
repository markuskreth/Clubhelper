package de.kreth.clubhelper.dao;

import java.util.Date;

import de.greenrobot.dao.test.AbstractDaoTestLongPk;

import de.kreth.clubhelper.Relative;
import de.kreth.clubhelper.dao.RelativeDao;

public class RelativeTest extends AbstractDaoTestLongPk<RelativeDao, Relative> {

    public RelativeTest() {
        super(RelativeDao.class);
    }

    @Override
    protected Relative createEntity(Long key) {
        Relative entity = new Relative();
        entity.setId(key);
        entity.setPerson1(1L);
        entity.setPerson2(2L);
        entity.setChanged(new Date());
        entity.setCreated(new Date());
        return entity;
    }

}
