package com.fireminder.semele.ui.devicescan;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.fireminder.semele.SemeleApp;
import com.fireminder.semele.bluetooth.SemeleBluetoothDevice;
import com.fireminder.semele.bluetooth.SemeleBtManager;
import com.fireminder.semele.ui.currentsession.CurrentSessionActivity;

import java.lang.ref.WeakReference;

import timber.log.Timber;

import static com.fireminder.semele.bluetooth.BluetoothScanner.BleDeviceFoundCallback;

public class DeviceScanPresenter {

  SemeleBtManager smBluetoothManager;
  IDeviceScanView deviceScanView;
  WeakReference<Context> context;

  public DeviceScanPresenter(final Context context) {
    smBluetoothManager = new SemeleBtManager(context);
    this.context = new WeakReference<>(context);
  }

  public void bindView(IDeviceScanView view) {
    deviceScanView = view;
  }

  public void stateChanged(final int bluetoothAdapterState) {
    switch(bluetoothAdapterState) {
      case BluetoothAdapter.STATE_OFF:
        deviceScanView.updateView(false);
        break;
      case BluetoothAdapter.STATE_ON:
        deviceScanView.updateView(true);
        break;
    }
  }

  public void actionClicked(Activity activity) {
    deviceScanView.clearList();
    if (smBluetoothManager.isBtEnabled()) {
      smBluetoothManager.startScan(bleDeviceFoundCallback);
    } else {
      smBluetoothManager.fireBtEnableIntent(activity);
    }
  }

  final BleDeviceFoundCallback bleDeviceFoundCallback = new BleDeviceFoundCallback() {
    @Override
    public void foundDevice(BluetoothDevice device) {
      deviceScanView.addDevice(new SemeleBluetoothDevice(device));
    }
  };

  public void onResume(Activity activity) {
    deviceScanView.updateView(smBluetoothManager.isBtEnabled());
    actionClicked(activity);
  }

  public void connectTo(SemeleBluetoothDevice bluetoothDevice) {
    Timber.d("bluetoothDevice: " + bluetoothDevice.toString());
    SharedPreferences.Editor preferences = SemeleApp.get().getSharedPreferences("semele-pref", Context.MODE_PRIVATE).edit();
    preferences.putString("ble-device-address", bluetoothDevice.address);
    // Avoid race condition by calling commit, since the next Activity will use sharedPrefs immidiately.
    preferences.commit();

    Intent intent = new Intent(context.get(), CurrentSessionActivity.class);
    context.get().startActivity(intent);
  }

  interface IDeviceScanView {
    void updateView(boolean isBtEnabled);
    void addDevice(SemeleBluetoothDevice device);
    void clearList();
  }
}
