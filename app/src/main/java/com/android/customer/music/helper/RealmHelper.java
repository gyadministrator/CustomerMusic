package com.android.customer.music.helper;

import android.util.Log;

import com.android.customer.music.model.Music;

import java.util.Date;
import java.util.List;
import java.util.Map;

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
     * 保存实体
     *
     * @param music 实体
     */
    public void save(final Music music) {
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealmOrUpdate(music);
            }
        });
    }

    /**
     * 查询数据
     *
     * @param param 条件
     * @return
     */
    public List<Music> list(Map<String, Object> param) {
        RealmQuery<Music> realmQuery = mRealm.where(Music.class);
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
        RealmResults<Music> models = realmQuery.findAll();
        return models.subList(0, models.size());
    }

    /**
     * 查询第一条音乐
     *
     * @return
     */
    public Music getOne() {
        RealmResults<Music> results = mRealm.where(Music.class)
                .findAll();
        List<Music> list = results.subList(0, results.size());
        return list.get(0);
    }

    /**
     * 修改实体
     *
     * @param music 实体
     */
    public void update(final Music music) {
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Music mMusic = realm.where(Music.class).equalTo("songId", music.getSongId())
                        .findFirst();
                if (mMusic != null) {
                    mMusic.setAuthor(music.getAuthor());
                    mMusic.setTitle(music.getTitle());
                    mMusic.setPath(music.getPath());
                    mMusic.setImageUrl(music.getImageUrl());
                }
            }
        });
    }

    /**
     * 删除实体
     *
     * @param music 实体
     */
    public void delete(Music music) {
        final RealmResults<Music> results = mRealm.where(Music.class).equalTo("songId", music.getSongId())
                .findAll();
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                results.deleteAllFromRealm();
            }
        });
    }

    /**
     * 删除所有
     */
    public void deleteAll() {
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                mRealm.deleteAll();
            }
        });
    }

    /**
     * 关闭数据库连接
     */
    public void close() {
        if (mRealm != null) {
            mRealm.close();
        }
    }
}
