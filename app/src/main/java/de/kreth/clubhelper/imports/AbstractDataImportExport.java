package de.kreth.clubhelper.imports;

import de.kreth.clubhelper.Person;
import de.kreth.clubhelper.dao.DaoSession;

/**
 * Created by markus on 20.03.15.
 */
public class AbstractDataImportExport {
    private DaoSession session;

    public AbstractDataImportExport(DaoSession session) {
        this.session = session;
    }

    public void importData(DataSource source) {

    }

    public void exportData(DataDestination destination) {

    }

    public static abstract class DataSource {

    }

    public static abstract class DataDestination {
    }

}
