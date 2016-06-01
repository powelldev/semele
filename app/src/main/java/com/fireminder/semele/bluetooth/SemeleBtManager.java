package com.fireminder.semele.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;

import static com.fireminder.semele.bluetooth.BluetoothScanner.*;

public class SemeleBtManager {

  BluetoothEnabler enabler;
  BluetoothScanner scanner;

  public SemeleBtManager(Context context) {
    BluetoothManager manager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
    BluetoothAdapter adapter = manager.getAdapter();
    enabler = new BluetoothEnabler(adapter);
    scanner = new BluetoothScanner(adapter);
  }

  public boolean isBtEnabled() {
    return enabler.isBtEnabled();
  }

  public void fireBtEnableIntent(Activity activity) {
    enabler.requestBtEnable(activity);
  }

  public void startScan(BleDeviceFoundCallback callback) {
    scanner.startScan(callback);
  }

  public void stopScan() {
    scanner.stopScan();
  }

}
