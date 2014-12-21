package de.kreth.clubhelper.test;

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
        entity.setPerson1();
        entity.setPerson2();
        return entity;
    }

}
