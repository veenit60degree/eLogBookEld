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

import com.htstart.htsdk.bluetooth.HTBleData;
import com.messaging.logistic.Globally;

import java.nio.charset.Charset;
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
        String utfData = "";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            utfData = new String(data, StandardCharsets.UTF_8);
            //utfData = new String(data, Charset.forName("UTF-8"));
        }
        String[] array = utfData.split("events:");
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


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static String decodeDataChange(BluetoothGattCharacteristic characteristic, String name, String mac){

        String decodedData = "";
        byte[] data = characteristic.getValue();
        String str = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            str = new String(data, StandardCharsets.UTF_8);
        }
        String[] array = str.split("events:");
        if (array != null) {
            if (array.length > 1) {
                String finalData = array[1];
                String splitData = finalData.replaceAll("\\[", "").replaceAll("\\]", "").
                        replaceAll("\\{", "").replaceAll("\\}", "");
                String[] arrayData = splitData.split("[,]");
                if (arrayData.length > 20) {
                    String date = arrayData[3];
                    String finalDate = date.substring(0, 2) + "-" + date.substring(2, 4) + "-" + date.substring(4, 6);
                    String time = arrayData[4];
                    String finalTime = time.substring(0, 2) + ":" + time.substring(2, 4) + ":" + time.substring(4, 6);
                    String accDateTime = arrayData[5];
                    String finalaccDateTime = accDateTime.substring(0, 2) + "-" + accDateTime.substring(2, 4) + "-" + accDateTime.substring(4, 6) + " " + accDateTime.substring(6, 8) + ":" + accDateTime.substring(8, 10) + ":" + accDateTime.substring(10, 12);
                    if(arrayData[12].length() > 4) {
                        Globally.LATITUDE = arrayData[12];
                        Globally.LONGITUDE = arrayData[13];
                    }
                    decodedData = "<b>Device Name:</b> " + name + "<br/>" +
                            "<b>MAC Address:</b> " + mac + "<br/><br/>" +
                            "<b>Sequence Id:</b> " + arrayData[0] + "<br/>" +
                            "<b>Event Type:</b> " + arrayData[1] + "<br/>" +
                            "<b>Event Code:</b> " + arrayData[2] + "<br/>" +
                            "<b>Date:</b> " + finalDate + "<br/>" +
                            "<b>Time:</b> " + finalTime + "<br/>" +
                            "<b>Latest ACC ON time:</b> " + finalaccDateTime + "<br/>" +
                            "<b>Event Data:</b> " + arrayData[6] + "<br/>" +
                            "<b>Vehicle Speed:</b> " + arrayData[7] + "<br/>" +
                            "<b>Engine Speed:</b> " + arrayData[8] + "<br/>" +
                            "<b>Odometer:</b> " + arrayData[9] + "<br/>" +
                            "<b>Engine Hours:</b> " + arrayData[10] + "<br/>" +
                            "<b>VIN Number:</b> " + arrayData[11] + "<br/>" +
                            "<b>Latitude:</b> " + arrayData[12] + "<br/>" +
                            "<b>Longitude:</b> " + arrayData[13] + "<br/>" +
                            "<b>Distance since Last located:</b> " + arrayData[14] + "<br/>" +
                            "<b>Malfunction Indicator Status:</b> " + arrayData[15] + "<br/>" +
                            "<b>Diagnostic Event Indicator Status:</b> " + arrayData[16] + "<br/>" +
                            "<b>Driver ID:</b> " + arrayData[17] + "<br/>" +
                            "<b>Version:</b>" + arrayData[18] + "<br/>" +
                            "<b>Event Checksum:</b> " + arrayData[19] + "<br/>" +
                            "<b>Event Data Checksum:</b>" + arrayData[20];

                }
            }
        }


        return decodedData;
    }


    public static String decodeDataChange(HTBleData data, String address){

        String decodedData = "";
        String accDateTime = data.getRTCDate() + data.getRTCTime();

        decodedData = // "<b>Device Name:</b> " + name + "<br/>" +
                 "<b>MAC Address:</b> " + address + "<br/><br/>" +
                "<b>Sequence Id:</b> " + data.getSequenceID() + "<br/>" +
                "<b>Event Type:</b> " + data.getEventType() + "<br/>" +
                "<b>Event Code:</b> " + data.getEventCode()+ "<br/>" +
                "<b>Date:</b> " + data.getRTCDate() + "<br/>" +
                "<b>Time:</b> " + data.getRTCTime() + "<br/>" +
                "<b>Latest ACC ON time:</b> " + accDateTime + "<br/>" +
                "<b>Event Data:</b> " + data.getEventData() + "<br/>" +
                "<b>Vehicle Speed:</b> " + data.getVehicleSpeed() + "<br/>" +
                "<b>Engine RPM:</b> " + data.getEngineSpeed() + "<br/>" +
                "<b>Odometer:</b> " + data.getOdoMeter() + "<br/>" +
                "<b>Engine Hours:</b> " + data.getEngineHours() + "<br/>" +
                "<b>VIN Number:</b> " + data.getVIN_Number() + "<br/>" +
                "<b>Latitude:</b> " + data.getLatitude() + "<br/>" +
                "<b>Longitude:</b> " + data.getLongitude() + "<br/>" +
                "<b>Distance since Last located:</b> " + data.getDistanceSinceLast() + "<br/>" +
                //"<b>Malfunction Indicator Status:</b> " + arrayData[15] + "<br/>" +
                //"<b>Diagnostic Event Indicator Status:</b> " + arrayData[16] + "<br/>" +
                "<b>Driver ID:</b> " + data.getDriverID() + "<br/>" +
                "<b>Version:</b>" + data.getVersion() + "<br/>" ;
//               + "<b>Event Checksum:</b> " + data.getEv + "<br/>" +
        //"<b>Event Data Checksum:</b>" + arrayData[20];


        return decodedData;
    }



}
