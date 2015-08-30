package de.kreth.clubhelper.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by markus on 15.06.15.
 */
public class ProductiveOpenHelper extends DaoMaster.OpenHelper {

    public ProductiveOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory) {
        super(context, name, factory);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i("greenDAO", "Upgrading schema from version " + oldVersion + " to " + newVersion + " by " + DaoMigrator.class.getSimpleName());
        DaoMigrator migrator = new DaoMigrator(db);
        migrator.start(oldVersion, newVersion);

    }
}
