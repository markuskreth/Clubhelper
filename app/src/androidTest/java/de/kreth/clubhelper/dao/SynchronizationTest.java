package de.kreth.clubhelper.dao;

import de.greenrobot.dao.test.AbstractDaoTestLongPk;

import de.kreth.clubhelper.data.Synchronization;

public class SynchronizationTest extends AbstractDaoTestLongPk<SynchronizationDao, Synchronization> {

    public SynchronizationTest() {
        super(SynchronizationDao.class);
    }

    @Override
    protected Synchronization createEntity(Long key) {
        Synchronization entity = new Synchronization();
        entity.setId(key);
        return entity;
    }

}
