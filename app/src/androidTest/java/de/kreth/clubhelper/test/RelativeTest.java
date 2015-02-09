package de.kreth.clubhelper.test;

import java.util.Calendar;
import java.util.GregorianCalendar;

import de.greenrobot.dao.test.AbstractDaoTestLongPk;

import de.kreth.clubhelper.Relative;
import de.kreth.clubhelper.Person;
import de.kreth.clubhelper.dao.RelativeDao;

public class RelativeTest extends AbstractDaoTestLongPk<RelativeDao, Relative> {

    public RelativeTest() {
        super(RelativeDao.class);
    }

    @Override
    protected Relative createEntity(Long key) {
        Person  p1 = new Person(1L, "Markus", "Kreth", "type", new GregorianCalendar(1973, Calendar.AUGUST, 21).getTime());
        Person  p2 = new Person(2L, "Ursula", "Kreth", "type", new GregorianCalendar(1946, Calendar.MARCH, 31).getTime());
        Relative entity = new Relative();
        entity.setId(key);
        entity.setPerson1(1);
        entity.setPerson2(2);
        entity.setToPerson1Relation("Kind");
        entity.setToPerson2Relation("Mutter");
        return entity;
    }

}
