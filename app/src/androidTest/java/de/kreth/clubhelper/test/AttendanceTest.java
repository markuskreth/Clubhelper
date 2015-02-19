package de.kreth.clubhelper.test;

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
        return entity;
    }

}
