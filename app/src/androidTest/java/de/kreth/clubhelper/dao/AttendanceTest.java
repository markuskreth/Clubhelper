package de.kreth.clubhelper.dao;

import java.util.Date;

import de.greenrobot.dao.test.AbstractDaoTestLongPk;

import de.kreth.clubhelper.Attendance;
import de.kreth.clubhelper.dao.AttendanceDao;

public class AttendanceTest extends AbstractDaoTestLongPk<AttendanceDao, Attendance> {

    public AttendanceTest() {
        super(AttendanceDao.class);
    }

    @Override
    protected Attendance createEntity(Long key) {
        Attendance entity = new Attendance();
        entity.setId(key);
        entity.setPersonId(1L);
        entity.setChanged(new Date());
        entity.setCreated(new Date());
        return entity;
    }

}
