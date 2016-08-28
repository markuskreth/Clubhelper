package de.kreth.clubhelper.dao;

import java.util.Date;

import de.greenrobot.dao.test.AbstractDaoTestLongPk;

import de.kreth.clubhelper.data.Relative;

public class RelativeTest extends AbstractDaoTestLongPk<RelativeDao, Relative> {

    private int count = 0;
    public RelativeTest() {
        super(RelativeDao.class);
    }

    @Override
    protected Relative createEntity(Long key) {
        Relative entity = new Relative();
        entity.setId(key);
        entity.setPerson1(1L + count);
        entity.setPerson2(1000L + count);
        entity.setChanged(new Date());
        entity.setCreated(new Date());
        count++;
        return entity;
    }

}
