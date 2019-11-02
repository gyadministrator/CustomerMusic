package com.android.customer.music.helper;

import com.android.customer.music.model.BaseModel;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import io.realm.ImportFlag;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

/**
 * Description: CustomerMusic
 * Created by gy(1984629668@qq.com)
 * Created Time on 2019/11/2 12:11
 */
public class RealmHelper {
    private Realm mRealm;
    private static RealmHelper instance;

    private RealmHelper() {
        mRealm = Realm.getDefaultInstance();
    }

    public static RealmHelper getInstance() {
        if (instance == null) {
            synchronized (RealmHelper.class) {
                if (instance == null) {
                    instance = new RealmHelper();
                }
            }
        }
        return instance;
    }

    /**
     * 获取唯一标识
     *
     * @return 字符串
     */
    private String uuid() {
        UUID uuid = UUID.randomUUID();
        String s = uuid.toString();
        return s.replaceAll("-", "");
    }


    /**
     * 保存实体
     *
     * @param baseModel 实体
     */
    public BaseModel saveObject(BaseModel baseModel) {
        mRealm.beginTransaction();
        baseModel.setId(uuid());
        baseModel.setCreateTime(new Date());
        mRealm.copyToRealm(baseModel);
        mRealm.commitTransaction();
        return mRealm.where(BaseModel.class).findFirstAsync();
    }

    /**
     * 查询数据
     *
     * @param param 条件
     * @return
     */
    public List<BaseModel> list(Map<String, Object> param) {
        mRealm.beginTransaction();
        RealmQuery<BaseModel> realmQuery = mRealm.where(BaseModel.class);
        for (String key : param.keySet()) {
            Object value = param.get(key);
            if (value instanceof Boolean) {
                realmQuery.equalTo(key, (Boolean) value);
            } else if (value instanceof Byte) {
                realmQuery.equalTo(key, (Byte) value);
            } else if (value instanceof Double) {
                realmQuery.equalTo(key, (Double) value);
            } else if (value instanceof Float) {
                realmQuery.equalTo(key, (Float) value);
            } else if (value instanceof Integer) {
                realmQuery.equalTo(key, (Integer) value);
            } else if (value instanceof Long) {
                realmQuery.equalTo(key, (Long) value);
            } else if (value instanceof Short) {
                realmQuery.equalTo(key, (Short) value);
            } else if (value instanceof String) {
                realmQuery.equalTo(key, (String) value);
            } else if (value instanceof Date) {
                realmQuery.equalTo(key, (Date) value);
            }
        }
        RealmResults<BaseModel> models = realmQuery.findAllAsync();
        mRealm.commitTransaction();
        return models.subList(0, models.size());
    }

    /**
     * 修改实体
     *
     * @param baseModel 实体
     */
    public void update(BaseModel baseModel) {
        mRealm.beginTransaction();
        mRealm.copyToRealmOrUpdate(baseModel);
        mRealm.commitTransaction();
    }

    /**
     * 删除实体
     *
     * @param baseModel 实体
     */
    public void delete(BaseModel baseModel) {
        mRealm.beginTransaction();
        final RealmResults<BaseModel> models = mRealm.where(BaseModel.class).
                equalTo("id", baseModel.getId()).
                findAllAsync();
        mRealm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                models.deleteAllFromRealm();
            }
        });
        mRealm.commitTransaction();
    }
}
