package de.kreth.clubhelper.dao;

import java.util.Date;

import de.greenrobot.dao.test.AbstractDaoTestLongPk;

import de.kreth.clubhelper.Group;
import de.kreth.clubhelper.dao.GroupDao;

public class GroupTest extends AbstractDaoTestLongPk<GroupDao, Group> {

    public GroupTest() {
        super(GroupDao.class);
    }

    @Override
    protected Group createEntity(Long key) {
        Group entity = new Group();
        entity.setId(key);
        entity.setName("Name");
        entity.setChanged(new Date());
        entity.setCreated(new Date());
        return entity;
    }

}
