package com.tencent.liteav.demo.services;

import android.content.Context;
import android.util.Log;

import com.tencent.liteav.demo.services.room.bean.RoomInfo;
import com.tencent.liteav.demo.services.room.callback.ActionCallback;
import com.tencent.liteav.demo.services.room.callback.CommonCallback;
import com.tencent.liteav.demo.services.room.callback.RoomInfoCallback;
import com.tencent.liteav.demo.services.room.http.impl.HttpRoomManager;
import com.tencent.liteav.demo.services.room.im.impl.IMRoomManager;

import java.util.ArrayList;
import java.util.List;

import static com.tencent.liteav.demo.services.room.http.impl.HttpRoomManager.TYPE_MLVB_SHOW_LIVE;

public class RoomService implements IRoomService {
    private static final String      TAG = RoomService.class.getSimpleName();
    private static       RoomService mInstance;
    private              Context     mContext;

    private RoomService(Context context) {
        mContext = context;
    }

    public static RoomService getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new RoomService(context);
        }
        return mInstance;
    }

    @Override
    public void createRoom(final String roomId, final String type, final String roomName, final String coverUrl, final CommonCallback callback) {
        Log.d(TAG, "createRoom roomId:" + roomId);
        //TODO 1、 IM创建群组
        IMRoomManager.getInstance().createRoom(roomId, roomName, coverUrl, new CommonCallback() {
            @Override
            public void onCallback(int code, String msg) {
                Log.d(TAG, " TXRoomService.getInstance().createRoom() code:" + code + ", message:" + msg);
                //TODO 2、服务器创建房间
                HttpRoomManager.getInstance().createRoom(roomId, type, new ActionCallback() {
                    @Override
                    public void onSuccess() {
                        Log.d(TAG, " HttpRoom2Manager.createRoom() success");
                        HttpRoomManager.getInstance().updateRoom(roomId, roomName, coverUrl, new ActionCallback() {
                            @Override
                            public void onSuccess() {
                                Log.d(TAG, " HttpRoom2Manager.updateRoom() success");
                                callback.onCallback(0, "createRoom success");
                            }

                            @Override
                            public void onFailed(int code, String msg) {
                                callback.onCallback(code, msg);
                            }
                        });

                    }

                    @Override
                    public void onFailed(int code, String msg) {
                        callback.onCallback(code, msg);
                    }
                });
            }
        });
    }

    @Override
    public void destroyRoom(String roomId, final String type, final CommonCallback callback) {
        //TODO 1、服务器删除房间
        HttpRoomManager.getInstance().destroyRoom(roomId, type, new ActionCallback() {
            @Override
            public void onSuccess() {
                callback.onCallback(0, "");
            }

            @Override
            public void onFailed(int code, String msg) {
                callback.onCallback(code, msg);
            }
        });

        //TODO 2、IM退出群组
        IMRoomManager.getInstance().destroyRoom(roomId, new CommonCallback() {
            @Override
            public void onCallback(int code, String msg) {

            }
        });
    }

    @Override
    public void enterRoom(final int roomId, final CommonCallback callback) {
        //TODO 加入群组
        IMRoomManager.getInstance().joinGroup(roomId + "", new CommonCallback() {
            @Override
            public void onCallback(int code, String msg) {
                Log.d(TAG, "enterRoom code:" + code + ", msg:" + msg);
                if (callback != null) {
                    callback.onCallback(code, msg);
                }
            }
        });
    }

    @Override
    public void exitRoom(int roomId, final CommonCallback callback) {
        //TODO 退出群组
        IMRoomManager.getInstance().quitGroup(roomId + "", new CommonCallback() {
            @Override
            public void onCallback(int code, String msg) {
                Log.d(TAG, "enterRoom code:" + code + ", msg:" + msg);
                if (callback != null) {
                    callback.onCallback(code, msg);
                }
            }
        });
    }

    @Override
    public void getRoomList(String type, final RoomInfoCallback callback) {
        HttpRoomManager.getInstance().getRoomList(TYPE_MLVB_SHOW_LIVE, new RoomInfoCallback() {
            @Override
            public void onCallback(int code, String msg, List<RoomInfo> list) {
                if (code == 0) {
                    if (list == null || list.size() <= 0) {
                        callback.onCallback(0, "success", new ArrayList<RoomInfo>());
                        return;
                    }
                    callback.onCallback(0, "success", list);
                } else {
                    callback.onCallback(code, msg, list);
                }
            }
        });
    }
}
