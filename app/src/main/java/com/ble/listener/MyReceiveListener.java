package com.ble.listener;


import com.ble.util.ConstantEvent;
import com.ble.util.EventBusInfo;
import com.constants.Constants;
import com.htstart.htsdk.HTBleSdk;
import com.htstart.htsdk.bluetooth.HTBleData;
import com.htstart.htsdk.minterface.IReceiveListener;
import com.messaging.logistic.R;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MyReceiveListener implements IReceiveListener {


    /**
     * 连接超时
     *
     * @param address
     */
    @Override
    public void onConnectTimeout(@Nullable String address) {
        EventBus.getDefault().post(new EventBusInfo(ConstantEvent.ACTION_CONNECT_TIMEOUT, address));
    }

    /**
     * 连接成功
     *
     * @param address
     */
    @Override
    public void onConnected(@Nullable String address) {
        EventBus.getDefault().post(new EventBusInfo(ConstantEvent.ACTION_GATT_CONNECTED, address));
    }

    /**
     * 连接出错
     *
     * @param address
     * @param i
     * @param i1
     */
    @Override
    public void onConnectionError(@NotNull String address, int i, int i1) {
        EventBus.getDefault().post(new EventBusInfo(ConstantEvent.ACTION_CONNECT_ERROR, address));
    }

    /**
     * 断开连接
     *
     * @param address
     */
    @Override
    public void onDisconnected(@Nullable String address) {
        EventBus.getDefault().post(new EventBusInfo(ConstantEvent.ACTION_GATT_DISCONNECTED, address));

    }

    /**
     * 数据返回回调
     *
     * @param address
     * @param uuid
     * @param htBleData
     */
    @Override
    public void onReceive(@NotNull String address, @NotNull String uuid, @NotNull HTBleData htBleData) {
      /*  if (htBleData.getEventType() == 0 && htBleData.getEventCode() == 1) {
            EventBus.getDefault().post(new EventBusInfo(ConstantEvent.ACTION_DATA_AVAILABLE, address, uuid, htBleData));
        } else {
            EventBus.getDefault().post(new EventBusInfo(ConstantEvent.ACTION_QUERY_DATA_AVAILABLE, address, uuid, htBleData));
        }*/

        int eventType = htBleData.getEventType();
        int eventCode = htBleData.getEventCode();
        if (eventType == 0 && eventCode == 1) {
            EventBus.getDefault().post(new EventBusInfo(ConstantEvent.ACTION_DATA_AVAILABLE, address, uuid, htBleData));
        } else if (eventType == 4 && (eventCode == 2 || eventCode == 1)) {
            EventBus.getDefault().post(new EventBusInfo(ConstantEvent.ACTION_DATA_AVAILABLE, address, uuid, htBleData));
        } else {
            EventBus.getDefault().post(new EventBusInfo(ConstantEvent.ACTION_QUERY_DATA_AVAILABLE, address, uuid, htBleData));
        }


    }

    /**
     * 设备收到指令响应结果
     *
     * @param address
     * @param uuid
     */
    @Override
    public void onResponse(@NotNull String address, @NotNull String uuid, @NotNull String sequenceID, @NotNull int status) {
        EventBus.getDefault().post(new EventBusInfo(ConstantEvent.ACTION_DATA_RESPONSE, address, uuid, status));

    }
}
