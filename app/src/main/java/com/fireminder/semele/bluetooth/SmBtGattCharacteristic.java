package com.fireminder.semele.bluetooth;

import android.bluetooth.BluetoothGattCharacteristic;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.UUID;

public class SmBtGattCharacteristic implements Parcelable {
  final UUID uuid;
  final int permissions;
  final int properties;

  public SmBtGattCharacteristic(BluetoothGattCharacteristic characteristic) {
    this.uuid = characteristic.getUuid();
    this.permissions = characteristic.getPermissions();
    this.properties = characteristic.getProperties();
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeSerializable(this.uuid);
    dest.writeInt(this.permissions);
    dest.writeInt(this.properties);
  }

  protected SmBtGattCharacteristic(Parcel in) {
    this.uuid = (UUID) in.readSerializable();
    this.permissions = in.readInt();
    this.properties = in.readInt();
  }

  public static final Parcelable.Creator<SmBtGattCharacteristic> CREATOR = new Parcelable.Creator<SmBtGattCharacteristic>() {
    @Override
    public SmBtGattCharacteristic createFromParcel(Parcel source) {
      return new SmBtGattCharacteristic(source);
    }

    @Override
    public SmBtGattCharacteristic[] newArray(int size) {
      return new SmBtGattCharacteristic[size];
    }
  };
}
