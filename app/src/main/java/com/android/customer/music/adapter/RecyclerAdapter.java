package com.android.customer.music.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.customer.music.R;
import com.android.customer.music.constant.Constants;
import com.android.customer.music.model.RecommendMusicModel;
import com.android.customer.music.view.WidthEqualHeightImageView;
import com.bumptech.glide.Glide;

import java.util.List;

/**
 * Description: CustomerMusic
 * Created by gy(1984629668@qq.com)
 * Created Time on 2019/10/31 9:13
 */
public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {
    private Context mContext;
    private List<RecommendMusicModel.ResultBean.ListBean> list;

    public RecyclerAdapter(Context mContext, List<RecommendMusicModel.ResultBean.ListBean> list) {
        this.mContext = mContext;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.flow_adapter, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RecommendMusicModel.ResultBean.ListBean bean = list.get(position);
        holder.tvName.setText(bean.getAlbum_title());
        String hot = bean.getHot();
        int num = Integer.parseInt(hot);
        if (num < 10000) {
            holder.tvNum.setText(hot);
        } else {
            holder.tvNum.setText((double) num / 10000 + "万");
        }
        Glide.with(mContext).load(bean.getPic_small()).into(holder.iv_album);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        WidthEqualHeightImageView iv_album;
        TextView tvName;
        TextView tvNum;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            iv_album = itemView.findViewById(R.id.iv_album);
            tvName = itemView.findViewById(R.id.tv_name);
            tvNum = itemView.findViewById(R.id.tv_num);
        }
    }
}
