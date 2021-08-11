package com.k.calendar;


import java.util.List;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmModel;
import io.realm.RealmObject;
import io.realm.RealmQuery;

/**
 * @Author: k
 * @Date: 2018/8/17 10:11
 */
public class DatabaseUtil {

    public static Realm getRealm() {
        return Realm.getDefaultInstance();
    }

    /**
     * 循环插入的时候获取realm并手动关闭数据库
     *
     * @param bean 保存
     */
    public static void saveBean(Realm realm, RealmObject bean) {
        beginTransaction(realm);
        realm.copyToRealm(bean);
        realm.commitTransaction();
    }

    /**
     * 只插入一次,操作完直接关闭数据库
     *
     * @param bean 保存或更新
     */
    public static void saveOrUpdateOnce(RealmObject bean) {
        if (bean == null) {
            return;
        }
        Realm realm = getRealm();
        beginTransaction(realm);
        realm.copyToRealmOrUpdate(bean);
        realm.commitTransaction();
        realm.close();
    }

    /**
     * 查询关闭realm会获取不到值
     *
     * @return 获取唯一实体
     */
    public static <T extends RealmModel> T getReamObject(Class<T> clazz) {
        Realm realm = getRealm();
        return realm.where(clazz).findFirst();
    }

    /**
     * 根据主键获取唯一实体
     *
     * @param primaryKey 主键名
     * @param value      键值
     * @param clazz      类型
     */
    public static <T extends RealmModel> T queryByPrimaryKey(String primaryKey, String value,
                                                             Class<T> clazz) {
        return queryByPrimaryKey(getRealm(), primaryKey, value, clazz);
    }

    /**
     * 根据主键获取唯一实体
     *
     * @param primaryKey 主键名
     * @param value      键值
     * @param clazz      类型
     */
    public static <T extends RealmModel> T queryByPrimaryKey(Realm realm, String primaryKey,
                                                             String value, Class<T> clazz) {
        return realm.where(clazz).equalTo(primaryKey, value).findFirst();
    }

    /**
     * 条件查询
     *
     * @param keySets 查询条件key-value
     * @param clazz   返回类型
     * @param <T>     T
     * @return 结果
     */
    public static <T extends RealmModel> T queryModel(Map<String, String> keySets, Class<T> clazz) {
        Realm realm = DatabaseUtil.getRealm();
        RealmQuery<T> query = realm.where(clazz);
        for (Map.Entry<String, String> entry : keySets.entrySet()) {
            query.equalTo(entry.getKey(), entry.getValue());
        }
        return query.findFirst();
    }

    public static <T extends RealmModel> List<T> queryAll(Map<String, String> keySets, Class<T>
            clazz) {
        Realm realm = DatabaseUtil.getRealm();
        RealmQuery<T> query = realm.where(clazz);
        if (keySets != null) {
            for (Map.Entry<String, String> entry : keySets.entrySet()) {
                query.equalTo(entry.getKey(), entry.getValue());
            }
        }
        return realm.copyFromRealm(query.findAll());
    }

    /**
     * 清空表
     *
     * @param clazz 实体
     */
    public static <T extends RealmModel> void clearTable(Class<T> clazz) {
        Realm realm = getRealm();
        beginTransaction(realm);
        realm.delete(clazz);
        realm.commitTransaction();
        realm.close();
    }

    /**
     * 提供realm,不关闭数据库
     */
    public static <T extends RealmModel> void clearTable(Realm realm, Class<T> clazz) {
        beginTransaction(realm);
        realm.delete(clazz);
        realm.commitTransaction();
    }

    private static void beginTransaction(Realm realm) {
        if (!realm.isInTransaction()) {
            realm.beginTransaction();
        }
    }
}
