package com.fireminder.semele.util;

import android.bluetooth.BluetoothGattCharacteristic;

import java.util.UUID;

public class HeartRateClient {

  public final static UUID UUID_HEART_RATE_MEASUREMENT = UUID.fromString("00002a37-0000-1000-8000-00805f9b34fb");

  public static int parseBpm(BluetoothGattCharacteristic characteristic) {
    int flag = characteristic.getProperties();

    int format = (flag & 0x01) != 0 ? BluetoothGattCharacteristic.FORMAT_UINT16 :
        BluetoothGattCharacteristic.FORMAT_UINT8;

    final int heartRate = characteristic.getIntValue(format, 1);
    return heartRate;
  }

  public static boolean isHeartRateMeasurement(BluetoothGattCharacteristic characteristic) {
    return UUID_HEART_RATE_MEASUREMENT.equals(characteristic.getUuid());
  }
}
