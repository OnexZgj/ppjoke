package com.onexzgj.ppjoke.exoplayer;

import android.app.Application;
import android.view.LayoutInflater;
import android.view.View;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.google.android.exoplayer2.ui.PlayerView;
import com.mooc.libcommon.global.AppGlobals;
import com.onexzgj.ppjoke.R;

public class PageListPlay {

    public PlayerControlView controlView;
    public SimpleExoPlayer exoPlayer;

    public PlayerView playerView;
    public String playUrl;

    public PageListPlay() {
        Application application = AppGlobals.getApplication();

        exoPlayer = ExoPlayerFactory.newSimpleInstance(application,
                //视频每一这的画面如何渲染,实现默认的实现类
                new DefaultRenderersFactory(application),
                //视频的音视频轨道如何加载,使用默认的轨道选择器
                new DefaultTrackSelector(),
                //视频缓存控制逻辑,使用默认的即可
                new DefaultLoadControl());

        playerView = (PlayerView) LayoutInflater.from(application).inflate(R.layout.layout_exo_player_view, null, true);

        //加载咱们布局层级优化之后的视频播放控制器
        controlView = (PlayerControlView) LayoutInflater.from(application).inflate(R.layout.layout_exo_player_contorller_view, null, false);

        playerView.setPlayer(exoPlayer);
        controlView.setPlayer(exoPlayer);
    }

    public void release() {
        if (exoPlayer != null) {
            exoPlayer.setPlayWhenReady(false);
            exoPlayer.stop(true);
            exoPlayer.release();
            exoPlayer = null;
        }
        if (playerView!=null){
            playerView.setPlayer(null);
            playerView=null;
        }
        if (controlView != null) {
            controlView.setPlayer(null);
            controlView.setVisibilityListener(null);
            controlView = null;
        }

    }

    /**
     * 切换与播放器exoplayer 绑定的exoplayerView。用于页面切换视频无缝续播的场景
     *
     * @param newPlayerView
     * @param attach
     */
    public void switchPlayerView(PlayerView newPlayerView, boolean attach) {
        playerView.setPlayer(attach ? null : exoPlayer);
        newPlayerView.setPlayer(attach ? exoPlayer : null);
    }
}
