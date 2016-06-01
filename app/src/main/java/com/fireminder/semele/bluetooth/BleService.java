package com.fireminder.semele.bluetooth;

import android.app.Service;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import com.fireminder.semele.BpmBroadcastReceiver;
import com.fireminder.semele.HeartRateListener;
import com.fireminder.semele.SemeleApp;
import com.fireminder.semele.model.BpmTable;

import timber.log.Timber;

import static com.fireminder.semele.bluetooth.BleStatus.*;

/**
 * Class responsible for initiating connection to the BleDevice.
 */
public class BleService extends Service {

  public BleService() {}

  public static final String ACTION_CONNECT = "connect";
  public static final String ACTION_DISCONNECT = "disconnect";
  public static final String EXTRA_DEVICE_ADDRESS= "extra-device-address";

  public static Intent connectIntent(Context context, String deviceAddress) {
    Intent intent = new Intent(context, BleService.class);
    intent.setAction(ACTION_CONNECT);
    intent.putExtra(EXTRA_DEVICE_ADDRESS, deviceAddress);
    return intent;
  }

  public static Intent disconnectIntent(Context context) {
    Intent intent = new Intent(context, BleService.class);
    intent.setAction(ACTION_DISCONNECT);
    return intent;
  }

  BleGattController controller;
  PrefsManager prefsManager;

  private boolean running;

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    if (running) {
      Timber.v("Already running, ignoring...");
      return START_NOT_STICKY;
    }

    Timber.d("Starting up!");
    running = true;

    //bleGattManager = new BleGattManager(getApplicationContext(), heartRateListener);
    //BleGattManager bleGattManager = new BleGattManager(getApplicationContext(), bluetoothGattCallback);
    controller = new BleGattController(bleGattManager, this);

    switch (intent.getAction()) {

      case ACTION_CONNECT: {
        final String deviceAddress = intent.getStringExtra(EXTRA_DEVICE_ADDRESS);
        controller.connect(deviceAddress);
        break;
      }

      case ACTION_DISCONNECT: {
        controller.disconnect();
        break;
      }
    }

    return START_STICKY;
  }

  HeartRateListener heartRateListener = new HeartRateListener() {
    @Override
    public void onHeartRateRead(int bpmVal) {
      int age = prefsManager.getCurrentUserAge();
      BpmTable.Bpm bpm = new BpmTable.Bpm(bpmVal, System.currentTimeMillis(), BpmTable.Zone.getZoneFromBpm(bpmVal, age));
      final Intent intent = new Intent(BpmBroadcastReceiver.ACTION_HEART_RATE);
      intent.putExtra(BpmBroadcastReceiver.EXTRA_HEART_RATE, bpm);
      SemeleApp.get().sendBroadcast(intent);
    }

    @Override
    public void onEnergyExpenditureRead(int ee) {
      throw new AssertionError("Not supported yet");
    }
  };

  private void broadcastConnectionState(final String state) {
    Intent intent = new Intent(ACTION_BLE_CONNECTION_STATE);
    intent.putExtra(EXTRA_STATE, state);
    this.sendBroadcast(intent);
  }

  private void broadcastCharacteristicChange(final SmBtGattCharacteristic characteristic) {
    Intent intent = new Intent(ACTION_BT_GATT_CHAR_CHANGE);
    intent.putExtra(EXTRA_CHARACTERISTIC, characteristic);
    this.sendBroadcast(intent);

  }

  @Override
  public IBinder onBind(Intent intent) {
    throw new AssertionError("Not supported");
  }

  static class BleGattController {
    BleGattManager bleGattManager;
    BleService service;

    public BleGattController(BleGattManager bleGattManager, BleService service) {
      this.bleGattManager = bleGattManager;
      this.service = service;
    }

    public void connect(final String deviceAddress) {
      if (bleGattManager.connect(deviceAddress)) {
        service.broadcastConnectionState(DEVICE_CONNECTED);
      } else {
        service.broadcastConnectionState(DEVICE_NOT_CONNECTED);
      }
    }

    public void disconnect() {
      bleGattManager.disconnect();
      service.broadcastConnectionState(DEVICE_NOT_CONNECTED);
    }

    BluetoothGattCallback bluetoothGattCallback = new BluetoothGattCallback() {
      @Override
      public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
        switch (newState) {
          case BluetoothGatt.STATE_CONNECTED:
            broadcastConnectionState(DEVICE_GATT_CONNECTED);
            break;
          case BluetoothGatt.STATE_DISCONNECTED:
            broadcastConnectionState(DEVICE_GATT_NOT_CONNECTED);
            break;
          default:
            Timber.i("Detected newState that was not broadcasted: " + newState);
            break;
        }
      }

      @Override
      public void onServicesDiscovered(BluetoothGatt gatt, int status) {
        broadcastConnectionState(SERVICES_DISCOVERED);

      }

      @Override
      public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        // BluetoothGattCharacteristic isn't Parcelable for some reason,
        // parse out relevant info and broadcast that instead.
        broadcastCharacteristicChange(new SmBtGattCharacteristic(characteristic));

      }
    };

  }
}
