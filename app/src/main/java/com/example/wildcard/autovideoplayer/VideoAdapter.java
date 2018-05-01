package com.example.wildcard.autovideoplayer;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.alibaba.android.vlayout.DelegateAdapter;
import com.alibaba.android.vlayout.LayoutHelper;
import com.bumptech.glide.Glide;

import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard;

/**
 * Created by 24548 on 2018/5/1.
 */

public class VideoAdapter extends DelegateAdapter.Adapter<RecyclerView.ViewHolder> {

    int[] videoIndexs = {0, 0, 1, 1, 1, 0, 0, 1, 0, 0, 1, 1, 1, 0, 0};

    private Context mContext;
    private LayoutInflater mInflater;
    private LayoutHelper layoutHelper;

    public VideoAdapter(Context context, LayoutHelper layoutHelper) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        this.layoutHelper = layoutHelper;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 1) {
            View convertView = mInflater.inflate(R.layout.item_videoview, null);
            VideoViewHolder viewHolder = new VideoViewHolder(convertView);
            return viewHolder;
        } else {
            View convertView = mInflater.inflate(R.layout.item_textview, null);
            ImageViewHolder imageViewHolder = new ImageViewHolder(convertView);
            return imageViewHolder;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof VideoViewHolder) {
            boolean setUp = ((VideoViewHolder) holder).jcVideoPlayer.setUp(
                    "http://gslb.miaopai.com/stream/ed5HCfnhovu3tyIQAiv60Q__.mp4",
                    JCVideoPlayer.SCREEN_LAYOUT_LIST, "");
            if (setUp) {
                Glide.with(mContext)
                        .load("http://a4.att.hudong.com/05/71/01300000057455120185716259013.jpg")
                        .into(((VideoViewHolder) holder).jcVideoPlayer.thumbImageView);
            }
        } else {
            Glide.with(mContext)
                    .load("http://img04.tooopen.com/images/20131019/sy_43185978222.jpg")
                    .into(((ImageViewHolder) holder).imageView);

        }
    }

    @Override
    public int getItemViewType(int position) {
        if (videoIndexs[position] == 1) {
            return 1;
        } else {
            return 0;
        }
    }

    @Override
    public int getItemCount() {
        return videoIndexs.length;
    }

    @Override
    public LayoutHelper onCreateLayoutHelper() {
        return this.layoutHelper;
    }

    public class VideoViewHolder extends RecyclerView.ViewHolder {
        JCVideoPlayerStandard jcVideoPlayer;

        public VideoViewHolder(View itemView) {
            super(itemView);
            jcVideoPlayer = (JCVideoPlayerStandard) itemView.findViewById(R.id.videoplayer);
        }
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ImageViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.image_view);
        }
    }
}
