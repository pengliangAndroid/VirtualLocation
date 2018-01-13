package com.wstro.virtuallocation.data.db;

import com.wstro.app.common.data.db.UpgradeHelper;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.query.WhereCondition;

import java.util.List;

import rx.Observable;

/**
 * @author pengl
 */

public class CustomDataBaseHelper {
    private DaoMaster daoMaster;
    private DaoSession daoSession;

    public CustomDataBaseHelper(UpgradeHelper upgradeHelper) {
        this.daoMaster = new DaoMaster(upgradeHelper.getWritableDatabase());
        this.daoSession = this.daoMaster.newSession();
    }

    public DaoMaster getDaoMaster() {
        return daoMaster;
    }

    public DaoSession getDaoSession() {
        return daoSession;
    }

    public AppInfoDao getAppInfoDao(){
        return daoSession.getAppInfoDao();
    }

    public <T> Observable<T> saveRx(T obj, AbstractDao dao){
        return dao.rx().insertOrReplace(obj);
    }

    public <T> void save(T obj, AbstractDao dao) {
        dao.insertOrReplace(obj);
    }

    public <T> Observable<List<T>> saveListRx(List<T> list, AbstractDao dao) {
        return dao.rx().insertOrReplaceInTx(list);
    }

    public <T> void saveList(List<T> list, AbstractDao dao) {
        dao.insertOrReplaceInTx(list);
    }

    public <T> Observable<T> insert(T obj,AbstractDao dao){
        return dao.rx().insertOrReplace(obj);
    }

    public <T> void delete(T obj,AbstractDao dao){
        dao.delete(obj);
    }

    public void deleteAll(AbstractDao dao){
         dao.deleteAll();
    }

    public <T> Observable<T> updateRx(T obj,AbstractDao dao){
        return dao.rx().update(obj);
    }

    public <T> void update(T obj, AbstractDao dao) {
        dao.update(obj);
    }

    public <T> Observable<List<T>> queryAllRx(AbstractDao dao) {
        return dao.rx().loadAll();
    }

    public <T> List<T> queryAll(AbstractDao dao) {
        return dao.loadAll();
    }


    public <T> List<T> queryByColumn(AbstractDao dao,Property property, Object columnValue) {
        return dao.queryBuilder().where(property.eq(columnValue)).orderAsc().list();
    }

    public <T> List<T> queryByCondition(AbstractDao dao,WhereCondition cond,WhereCondition... condMore) {
        return dao.queryBuilder().where(cond,condMore).orderAsc().list();
    }

    public <T> Observable<List<T>> queryByConditionRx(AbstractDao dao,WhereCondition cond,WhereCondition... condMore) {
        return dao.queryBuilder().where(cond,condMore).orderAsc().rx().list();
    }


    public void destroy() {
        this.daoSession = null;
        this.daoMaster = null;
    }
}
