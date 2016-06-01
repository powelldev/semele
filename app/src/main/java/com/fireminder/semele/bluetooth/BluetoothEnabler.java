package com.fireminder.semele.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;

public class BluetoothEnabler {

  private static final int ENABLE_BT_REQUEST_CODE = 0;

  private BluetoothAdapter btAdapter;
  private BluetoothScanner.BluetoothScannerImpl scanner;

  public BluetoothEnabler(BluetoothAdapter adapter) {
    btAdapter = adapter;
  }

  public boolean isBtEnabled() {
    if (btAdapter == null) {
      return false;
    }
    return btAdapter.isEnabled();
  }

  public void requestBtEnable(Activity activity) {
    final Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
    activity.startActivityForResult(intent, ENABLE_BT_REQUEST_CODE);
  }

}
