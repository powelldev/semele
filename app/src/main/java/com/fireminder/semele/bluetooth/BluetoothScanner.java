package com.fireminder.semele.bluetooth;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;

import timber.log.Timber;

public class BluetoothScanner {

  private BluetoothAdapter btAdapter;
  private BluetoothScannerImpl scanner;

  public BluetoothScanner(BluetoothAdapter adapter) {
    btAdapter = adapter;
  }

  public void startScan(BleDeviceFoundCallback callback) {
    if (scanner == null) {
      scanner = BluetoothScannerImpl.ScannerFactory.getScannerForSdkInt(Build.VERSION.SDK_INT, btAdapter);
    }
    scanner.scan(callback);
  }

  public void stopScan() {
    if (scanner == null) {
      Timber.d("Bluetooth scanner not started, can't stop.");
      return;
    }
    scanner.stopScan();
  }

  public interface BleDeviceFoundCallback {
    void foundDevice(BluetoothDevice device);
  }

  public abstract static class BluetoothScannerImpl {

    private static final int SCAN_TIMEOUT = 10 * 1000;

    private final Handler handler;
    private boolean isScanning = false;

    public BluetoothScannerImpl() {
      handler = new Handler(Looper.getMainLooper());
    }

    public final void scan(final BleDeviceFoundCallback callback) {
      isScanning = true;
      startScan(callback);
      handler.postDelayed(this::cancel, SCAN_TIMEOUT);
    }

    public void cancel() {
      isScanning = false;
      stopScan();
    }


    abstract void startScan(BleDeviceFoundCallback callback);
    abstract void stopScan();

    public static class KitkatBtScannerImpl extends BluetoothScannerImpl {

      private final BluetoothAdapter adapter;

      private BluetoothAdapter.LeScanCallback leScanCallback;

      public KitkatBtScannerImpl(BluetoothAdapter adapter) {
        super();
        this.adapter = adapter;
      }
      @Override
      void startScan(final BleDeviceFoundCallback callback) {
        leScanCallback = (device, rssi, scanRecord) -> callback.foundDevice(device);
        adapter.startLeScan(leScanCallback);
      }

      @Override
      void stopScan() {
        adapter.stopLeScan(leScanCallback);
      }

      @Override
      public void cancel() {
        adapter.stopLeScan(leScanCallback);
      }

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static class LollipopBtScannerImpl extends BluetoothScannerImpl {

      private final android.bluetooth.le.BluetoothLeScanner scanner;
      private ScanCallback scanCallback;

      public LollipopBtScannerImpl(BluetoothAdapter adapter) {
        super();
        scanner = adapter.getBluetoothLeScanner();
      }

      @Override
      void startScan(final BleDeviceFoundCallback callback) {
        scanCallback = new ScanCallback() {
          @Override
          public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            callback.foundDevice(result.getDevice());
          }
        };

        scanner.startScan(scanCallback);
      }

      @Override
      void stopScan() {
        scanner.stopScan(scanCallback);
      }

      @Override
      public void cancel() {
        scanner.stopScan(scanCallback);
      }
    }

    public static class ScannerFactory {
      public static BluetoothScannerImpl getScannerForSdkInt(int sdkInt, BluetoothAdapter adapter) {
        if (sdkInt >= Build.VERSION_CODES.LOLLIPOP) {
          return new LollipopBtScannerImpl(adapter);
        } else {
          return new KitkatBtScannerImpl(adapter);
        }
      }
    }

  }
}
