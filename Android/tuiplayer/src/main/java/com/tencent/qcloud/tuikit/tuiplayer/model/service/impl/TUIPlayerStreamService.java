package com.tencent.qcloud.tuikit.tuiplayer.model.service.impl;

import android.content.Context;
import android.os.Bundle;

import com.tencent.liteav.basic.log.TXCLog;
import com.tencent.live2.V2TXLiveDef;
import com.tencent.live2.V2TXLivePlayer;
import com.tencent.live2.V2TXLivePlayerObserver;
import com.tencent.live2.V2TXLivePusher;
import com.tencent.live2.V2TXLivePusherObserver;
import com.tencent.live2.impl.V2TXLivePlayerImpl;
import com.tencent.live2.impl.V2TXLivePusherImpl;
import com.tencent.qcloud.tuikit.tuiplayer.model.listener.ITUIPlayerStreamListener;
import com.tencent.qcloud.tuikit.tuiplayer.model.service.ITUIPlayerStreamService;
import com.tencent.rtmp.ui.TXCloudVideoView;

import static com.tencent.live2.V2TXLiveCode.V2TXLIVE_ERROR_DISCONNECTED;
import static com.tencent.qcloud.tuikit.tuiplayer.view.TUIPlayerView.PlayStatus.START_PLAY;
import static com.tencent.qcloud.tuikit.tuiplayer.view.TUIPlayerView.PlayStatus.STOP_PLAY;

public class TUIPlayerStreamService implements ITUIPlayerStreamService {
    private static final String TAG = TUIPlayerStreamService.class.getSimpleName();

    private ITUIPlayerStreamListener mListener;
    private V2TXLivePusher           mV2TXLivePusher;
    private V2TXLivePlayer           mV2TXLivePlayer;

    private TUIPlayerStreamService(Context context) {
        mV2TXLivePusher = new V2TXLivePusherImpl(context, V2TXLiveDef.V2TXLiveMode.TXLiveMode_RTC);
        mV2TXLivePusher.setObserver(new TUILivePusherObserver());
        mV2TXLivePlayer = new V2TXLivePlayerImpl(context);
        mV2TXLivePlayer.setObserver(new TUILivePlayerObserver());
    }

    private static volatile TUIPlayerStreamService instance;

    public static TUIPlayerStreamService getInstance(Context context) {
        if (instance == null) {
            synchronized (TUIPlayerStreamService.class) {
                if (instance == null) {
                    instance = new TUIPlayerStreamService(context);
                }
            }
        }
        return instance;
    }

    @Override
    public void setListener(ITUIPlayerStreamListener listener) {
        mListener = listener;
    }

    @Override
    public int startPlay(String url, TXCloudVideoView videoView) {
        String playUrl = url;
        mV2TXLivePlayer.setRenderView(videoView);
        mV2TXLivePlayer.setRenderFillMode(V2TXLiveDef.V2TXLiveFillMode.V2TXLiveFillModeFill);
        int ret = mV2TXLivePlayer.startPlay(playUrl);
        TXCLog.d(TAG, "startPlay: ret" + ret + ", playUrl:" + playUrl);
        return 0;
    }

    @Override
    public int stopPlay() {
        TXCLog.d(TAG, "stopPlay");
        if (mV2TXLivePlayer != null && mV2TXLivePlayer.isPlaying() == 1) {
            mV2TXLivePlayer.stopPlay();
        }
        return 0;
    }

    @Override
    public int startPush(String url, boolean isFront, TXCloudVideoView videoView) {
        TXCLog.d(TAG, "startPush url:" + url);
        mV2TXLivePusher.setRenderView(videoView);
        mV2TXLivePusher.startCamera(isFront);
        mV2TXLivePusher.startPush(url);
        mV2TXLivePusher.startMicrophone();
        return 0;
    }

    @Override
    public int stopPush() {
        TXCLog.d(TAG, "stopPush");
        mV2TXLivePlayer.stopPlay();
        mV2TXLivePusher.stopPush();
        return 0;
    }

    @Override
    public void destory() {
        TXCLog.d(TAG, "destory");
        if (mV2TXLivePusher == null) {
            return;
        }

        if (mV2TXLivePusher.isPushing() == 1) {
            mV2TXLivePusher.stopPush();
        }
        if (mV2TXLivePlayer.isPlaying() == 1) {
            mV2TXLivePlayer.stopPlay();
        }
    }

    class TUILivePusherObserver extends V2TXLivePusherObserver {

        @Override
        public void onSetMixTranscodingConfig(int code, String msg) {
            TXCLog.d(TAG, "TUILivePusherObserver onSetMixTranscodingConfig code:" + code + ", msg:" + msg);
        }

        @Override
        public void onError(int code, String msg, Bundle extraInfo) {
            TXCLog.d(TAG, "TUILivePusherObserver onError code:" + code + ", msg:" + msg);
        }

        @Override
        public void onWarning(int code, String msg, Bundle extraInfo) {
            TXCLog.d(TAG, "TUILivePusherObserver onWarning code:" + code + ", msg:" + msg);
        }

        @Override
        public void onCaptureFirstVideoFrame() {
            TXCLog.d(TAG, "TUILivePusherObserver onCaptureFirstVideoFrame");
            if (mListener != null) {
                mListener.onPushSuccess();
            }
        }
    }


    class TUILivePlayerObserver extends V2TXLivePlayerObserver {
        public void onError(V2TXLivePlayer player, int code, String msg, Bundle extraInfo) {
            TXCLog.d(TAG, "TUILivePlayerObserver onError code:" + code + ", msg:" + msg);
            if (code == V2TXLIVE_ERROR_DISCONNECTED) {
                mListener.onNotifyPlayStatus(STOP_PLAY);
            }
        }

        public void onWarning(V2TXLivePlayer player, int code, String msg, Bundle extraInfo) {
            TXCLog.d(TAG, "TUILivePlayerObserver onWarning code:" + code + ", msg:" + msg);
        }

        @Override
        public void onVideoPlaying(V2TXLivePlayer player, boolean firstPlay, Bundle extraInfo) {
            mListener.onNotifyPlayStatus(START_PLAY);
        }
    }

}
