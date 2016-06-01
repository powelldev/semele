package com.fireminder.semele.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.text.TextUtils;

import com.fireminder.semele.R;
import com.fireminder.semele.SemeleApp;

/**
 * Wrapper class for BluetoothDevice
 */
public class SemeleBluetoothDevice {

  public final String name;
  public final String address;

  public SemeleBluetoothDevice(String name, String address) {
    this.name = name;
    this.address = address;
  }

  public SemeleBluetoothDevice(BluetoothDevice bluetoothDevice) {
    this.name = TextUtils.isEmpty(bluetoothDevice.getName()) ?
        SemeleApp.get().getString(R.string.unknown_device) :
        bluetoothDevice.getName();
    this.address = bluetoothDevice.getAddress();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    SemeleBluetoothDevice that = (SemeleBluetoothDevice) o;

    if (name != null ? !name.equals(that.name) : that.name != null) return false;
    return !(address != null ? !address.equals(that.address) : that.address != null);

  }

  @Override
  public int hashCode() {
    int result = name != null ? name.hashCode() : 0;
    result = 31 * result + (address != null ? address.hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    return "SemeleBluetoothDevice{" +
        "name='" + name + '\'' +
        ", address='" + address + '\'' +
        '}';
  }
}
