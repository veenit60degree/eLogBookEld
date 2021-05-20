/*
 * Copyright (C) 2013 youten
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ble.util;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.nio.charset.StandardCharsets;

/**
 * Util for Bluetooth Low Energy
 */
public class BleUtil {

    private BleUtil() {
        // Util
    }

    /** check if BLE Supported device */
    public static boolean isBLESupported(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);
    }

    /** get BluetoothManager */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static BluetoothManager getManager(Context context) {
        return (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
    }

    public static String convertStringToHex(String str) {

        StringBuffer sb = new StringBuffer();
        //Converting string to character array
        char ch[] = str.toCharArray();
        for (int i = 0; i < ch.length; i++) {
            String hexString = Integer.toHexString(ch[i]);
            sb.append(hexString);
        }
        String result = sb.toString();
        System.out.println(result);

        return result;
    }


    public static byte[] invertStringToBytes(String value){
        int len = value.length()/2;
        if (len > 0){
            byte[] bytes = new byte[len];
            for (int i=0; i<len; i++){
                Integer val = Integer.valueOf(value.substring(i * 2, i * 2 + 2), 16);
                bytes[i] = val.byteValue();
            }
            return bytes;
        }
        return null;
    }



    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static String[] decodeDataChange(BluetoothGattCharacteristic characteristic) {

        String[] arrayData = null;
        byte[] data = characteristic.getValue();
        String str = "";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            str = new String(data, StandardCharsets.UTF_8);
        }
        String[] array = str.split("events:");
        if (array != null) {
            if (array.length > 1) {
                String finalData = array[1];
                String splitData = finalData.replaceAll("\\[", "").replaceAll("\\]", "").
                        replaceAll("\\{", "").replaceAll("\\}", "");
               arrayData = splitData.split("[,]");
            }
        }
        return arrayData;
    }


}
