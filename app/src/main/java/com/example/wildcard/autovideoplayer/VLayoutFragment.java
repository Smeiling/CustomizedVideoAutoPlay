package com.example.wildcard.autovideoplayer;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.alibaba.android.vlayout.DelegateAdapter;
import com.alibaba.android.vlayout.VirtualLayoutManager;
import com.alibaba.android.vlayout.layout.LinearLayoutHelper;

import java.util.ArrayList;
import java.util.List;

import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard;

/**
 * Created by Smeiling on 2018/5/1.
 */

public class VLayoutFragment extends Fragment {

    public static final String TAG = VLayoutFragment.class.getSimpleName();

    private RecyclerView rvList;

    private VirtualLayoutManager layoutManager;
    private DelegateAdapter delegateAdapter;
    private DelegateAdapter.Adapter videoListAdapter;

    /**
     * 列表标记项
     */
    private int[] tagPosition = new int[4];
    /**
     * 当前播放视频的位置
     */
    private int currentPlayingPosition = -1;
    /**
     * 状态栏高度
     */
    private int statusBarHeight = 0;
    /**
     * 屏幕高度
     */
    private int windowHeight = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_vlayout, container, false);

        return view;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvList = (RecyclerView) getView().findViewById(R.id.recycler_view);
        statusBarHeight = getStatusBarHeight();
        windowHeight = getWindowHeight();
        layoutManager = new VirtualLayoutManager(getActivity());
        rvList.setLayoutManager(layoutManager);
        delegateAdapter = new DelegateAdapter(layoutManager);

        LinearLayoutHelper linearLayoutHelper = new LinearLayoutHelper();
        linearLayoutHelper.setItemCount(15);
        linearLayoutHelper.setMarginBottom(100);

        List<DelegateAdapter.Adapter> adapters = new ArrayList<>();

        videoListAdapter = new VideoAdapter(getActivity(), linearLayoutHelper) {
            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                super.onBindViewHolder(holder, position);
            }
        };

        adapters.add(videoListAdapter);

        delegateAdapter.setAdapters(adapters);
        rvList.setAdapter(delegateAdapter);
        rvList.addOnScrollListener(new VideoListScrollListener());
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private class VideoListScrollListener extends RecyclerView.OnScrollListener {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            tagPosition[0] = layoutManager.findFirstVisibleItemPosition();
            tagPosition[1] = layoutManager.findFirstCompletelyVisibleItemPosition();
            tagPosition[2] = layoutManager.findLastVisibleItemPosition();
            tagPosition[3] = layoutManager.findLastCompletelyVisibleItemPosition();

            if (currentPlayingPosition != -1) {
                pausePlayer(currentPlayingPosition);
            }

        }

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);

            reverseTags();
        }
    }

    /**
     * 遍历列表项
     */
    private void reverseTags() {
        for (int i = 0; i < tagPosition.length; i++) {
            if (i >= 1 && tagPosition[i] == tagPosition[i - 1]) {
                continue;
            }
            if (autoPlay(tagPosition[i])) {
                break;
            }
        }
    }


    /**
     * 如果Item在有效播放屏幕内，则播放
     *
     * @param position
     * @return
     */
    private boolean autoPlay(int position) {
        View firstPartiallyVisibleView = layoutManager.findViewByPosition(position);
        if (isVideoItem(firstPartiallyVisibleView)) {
            JCVideoPlayerStandard videoPlayerStandard =
                    (JCVideoPlayerStandard) firstPartiallyVisibleView.findViewById(R.id.videoplayer);
            Rect rect = new Rect();
            videoPlayerStandard.getGlobalVisibleRect(rect);
            if (rect.bottom >= (videoPlayerStandard.getHeight() / 2 + statusBarHeight) && rect.top <= (windowHeight - videoPlayerStandard.getHeight() / 2)) {
                if (videoPlayerStandard.currentState == JCVideoPlayer.CURRENT_STATE_NORMAL || videoPlayerStandard.currentState == JCVideoPlayer.CURRENT_STATE_ERROR) {
                    Log.e("videoTest", videoPlayerStandard.currentState + "======================performClick======================");
                    videoPlayerStandard.startButton.performClick();
                    VPApplication.instance.VideoPlaying = videoPlayerStandard;
                }
                currentPlayingPosition = position;
                return true;
            }
        }
        return false;
    }

    /**
     * 当前播放视频滑出有效屏幕则暂停
     *
     * @param position
     * @return
     */
    private boolean pausePlayer(int position) {
        View currentPlayingView = layoutManager.findViewByPosition(position);
        if (currentPlayingView != null && currentPlayingView.findViewById(R.id.videoplayer) != null) {
            JCVideoPlayerStandard currentPlayerStandard =
                    (JCVideoPlayerStandard) currentPlayingView.findViewById(R.id.videoplayer);

            Rect rect = new Rect();
            currentPlayerStandard.getGlobalVisibleRect(rect);
            if (rect.bottom < (currentPlayerStandard.getHeight() / 2 + statusBarHeight) || rect.top > (1920 - currentPlayerStandard.getHeight() / 2)) {
                JCVideoPlayer.releaseAllVideos();
                VPApplication.instance.VideoPlaying = null;
                return true;
            }
        }
        return false;
    }

    /**
     * 当前Item是否包含视频判断
     *
     * @param view
     * @return
     */
    private boolean isVideoItem(View view) {
        if (view != null && view.findViewById(R.id.videoplayer) != null) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 获取屏幕高度
     *
     * @return
     */
    private int getWindowHeight() {
        WindowManager wm = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
        int height = wm.getDefaultDisplay().getHeight();
        Log.e(TAG, "window height:" + height);
        return height;
    }

    /**
     * 获取状态栏高度
     *
     * @return
     */
    private int getStatusBarHeight() {
        int statusBarHeight1 = -1;
        //获取status_bar_height资源的ID
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            //根据资源ID获取响应的尺寸值
            statusBarHeight1 = getResources().getDimensionPixelSize(resourceId);
        }
        Log.e(TAG, "status bar height:" + statusBarHeight1);
        return statusBarHeight1;
    }
}
