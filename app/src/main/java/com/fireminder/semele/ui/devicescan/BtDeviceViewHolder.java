package com.fireminder.semele.ui.devicescan;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.fireminder.semele.R;
import com.fireminder.semele.bluetooth.SemeleBluetoothDevice;

public class BtDeviceViewHolder extends RecyclerView.ViewHolder {

  private final TextView address;
  private final TextView name;

  public BtDeviceViewHolder(View itemView) {
    super(itemView);
    address = (TextView) itemView.findViewById(R.id.address);
    name = (TextView) itemView.findViewById(R.id.name);
  }

  public void bindView(final SemeleBluetoothDevice device, View.OnClickListener onClickListener) {
    address.setText(device.address);
    name.setText(device.name);
    itemView.setOnClickListener(onClickListener);
  }
}
