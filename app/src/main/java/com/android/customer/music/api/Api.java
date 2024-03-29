package com.android.customer.music.api;

import com.android.customer.music.model.LrcModel;
import com.android.customer.music.model.MusicModel;
import com.android.customer.music.model.PlayMusicModel;
import com.android.customer.music.model.RecommendMusicModel;
import com.android.customer.music.model.SearchMusicModel;
import com.android.customer.music.model.SingerInfoModel;

import java.util.Map;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

/**
 * 数据接口
 * Description: CustomerMusic
 * Created by gy(1984629668@qq.com)
 * Created Time on 2019/11/2 15:55
 */
public interface Api {

    /**
     * 获取歌曲列表
     * method=baidu.ting.billboard.billList&type=1&size=10&offset=0
     * 参数： type = 1-新歌榜,2-热歌榜,11-摇滚榜,12-爵士,16-流行,21-欧美金曲榜,22-经典老歌榜,23-情歌对唱榜,24-影视金曲榜,25-网络歌曲榜
     * size = 10 //返回条目数量
     * offset = 0 //获取偏移
     *
     * @param params 参数
     * @return
     */
    @GET("v1/restserver/ting")
    Observable<MusicModel> list(@QueryMap Map<String, Object> params);

    /**
     * 搜索
     * method=baidu.ting.search.catalogSug&query=海阔天空
     *
     * @param params 参数
     * @return
     */
    @GET("v1/restserver/ting")
    Observable<SearchMusicModel> search(@QueryMap Map<String, Object> params);


    /**
     * method=baidu.ting.song.play&songid=877578
     * 获取音乐播放地址
     *
     * @param params 参数
     * @return
     */
    @GET("v1/restserver/ting")
    Observable<PlayMusicModel> play(@QueryMap Map<String, Object> params);

    /**
     *method=baidu.ting.song.getRecommandSongList&song_id=877578&num=5
     * 参数： song_id = 877578
     * num = 5//返回条目数量
     * @param params 参数
     * @return
     */
    @GET("v1/restserver/ting")
    Observable<RecommendMusicModel> recommend(@QueryMap Map<String, Object> params);

    /**
     * method=baidu.ting.song.lry&songid=877578
     * @param params 参数
     * @return
     */
    @GET("v1/restserver/ting")
    Observable<LrcModel> lrc(@QueryMap Map<String, Object> params);

    /**
     * method=baidu.ting.artist.getInfo&tinguid=877578
     * @param params 参数
     * @return
     */
    @GET("v1/restserver/ting")
    Observable<SingerInfoModel> getInfo(@QueryMap Map<String, Object> params);
}
