package com.fireminder.semele.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothManager;
import android.content.Context;

import com.fireminder.semele.HeartRateListener;
import com.fireminder.semele.SemeleApp;

import java.util.UUID;

import timber.log.Timber;

public class BleGattManager {

  public static UUID CLIENT_CHARACTERISTIC_CONFIG = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
  public static UUID HEART_RATE_SERVICE = UUID.fromString("0000180d-0000-1000-8000-00805f9b34fb");
  public static UUID HEART_RATE_MEASUREMENT = UUID.fromString("00002a37-0000-1000-8000-00805f9b34fb");

  private BluetoothManager btManager;
  private BluetoothAdapter btAdapter;
  private BluetoothDevice btDevice;
  private BluetoothGatt btGatt;

  private HeartRateListener listener;
  private BluetoothGattCallback callback;

  /*
  public BleGattManager(final Context context, final HeartRateListener listener) {
    btManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
    this.listener = listener;
  }
  */

  public BleGattManager(final Context context, BluetoothGattCallback btGattCallback) {
    btManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
    this.callback = btGattCallback;
  }

  /*

  private BluetoothGattCallback mBtGattCallback = new BluetoothGattCallback() {

    @Override
    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
      Timber.v("onConnectionStateChange: status:" + status + " state: " + newState);

      switch (newState) {
        case BluetoothProfile.STATE_CONNECTED:
          Timber.v("connected, starting service discovery");
          gatt.discoverServices();
          break;
        case BluetoothProfile.STATE_DISCONNECTED:
          Timber.v("disconnected");
          // TODO Alert app at large we're disconnected.
          break;
      }
    }

    @Override
    public void onServicesDiscovered(BluetoothGatt gatt, int status) {
      Timber.e("onServiceDiscovered: status:" + status);
      BluetoothGattService heartService = gatt.getService(HEART_RATE_SERVICE);

      if (heartService == null) {
        Timber.e("Device has no heart service :(");
        // TODO log this device's address as having no heart service
        // so we can prompt user to avoid selecting this again
        return;
      }

      // HRM requires BtGatt#setCharacteristicNotification and the Client Characteristic Config descriptor
      // to be enabled to start updates.
      BluetoothGattCharacteristic characteristic = heartService.getCharacteristic(HEART_RATE_MEASUREMENT);
      gatt.setCharacteristicNotification(characteristic, true);
      BluetoothGattDescriptor descriptor = characteristic.getDescriptor(CLIENT_CHARACTERISTIC_CONFIG);
      descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
      gatt.writeDescriptor(descriptor);
      Timber.d("Subscribing to heart rate measurements.");
    }

    @Override
    public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
      Timber.e("onCharacteristicRead: " + characteristic);
    }

    @Override
    public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
      Timber.e("onCharacteristicWrite: " + characteristic);
    }

    @Override
    public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
      Timber.e("onCharacteristicChanged: " + characteristic);
      if (HeartRateClient.isHeartRateMeasurement(characteristic)) {
        listener.onHeartRateRead(HeartRateClient.parseBpm(characteristic));
      } else {
        Timber.e("unknown characteristic change, uuid:" + characteristic.getUuid());
      }
    }
  };
  */

  public boolean connect(String deviceAddress) {
    if (btManager == null) {
      Timber.e("btManager is null");
      return false;
    }

    btAdapter = btManager.getAdapter();

    if (btAdapter == null) {
      Timber.e("btAdapter is null");
      return false;
    }

    try {
      btDevice = btAdapter.getRemoteDevice(deviceAddress);
    } catch (IllegalArgumentException e) {
      Timber.e(deviceAddress + " is an invalid address.");
      return false;
    }

    btGatt = btDevice.connectGatt(SemeleApp.get(), true, callback);
    return true;
  }

  public void disconnect() {
    if (btGatt != null) {
      btGatt.disconnect();
    }
  }

}
