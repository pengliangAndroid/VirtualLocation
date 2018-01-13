package com.wstro.virtuallocation.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.wstro.app.common.data.db.UpgradeHelper;
import com.wstro.virtuallocation.data.db.AppInfoDao;

import org.greenrobot.greendao.database.Database;

/**
 * ClassName: CustomUpgradeHelper <br/>
 * Function: TODO ADD FUNCTION. <br/>
 *
 * @author pengl
 * @date 2017/10/7
 */
public class CustomUpgradeHelper extends UpgradeHelper {
    public CustomUpgradeHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int schemaVersion) {
        super(context, name, factory, schemaVersion);
    }

    @Override
    protected void createAllTables(Database db, boolean ifNotExists) {
        AppInfoDao.createTable(db, ifNotExists);
        super.createAllTables(db,ifNotExists);
    }

    @Override
    protected void dropAllTables(Database db, boolean ifNotExists) {
        super.dropAllTables(db,ifNotExists);
    }

    @Override
    public void onUpgrade(Database db, int oldVersion, int newVersion) {
        super.onUpgrade(db,oldVersion,newVersion);

        /*MigrationHelper.getInstance().migrate(this,db,
                LoginUserDao.class);*/
    }
}
