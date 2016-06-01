package com.fireminder.semele.ui.devicescan;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fireminder.semele.R;
import com.fireminder.semele.bluetooth.SemeleBluetoothDevice;

import java.util.ArrayList;
import java.util.List;

public class BleDeviceListAdapter extends RecyclerView.Adapter<BtDeviceViewHolder> {

  List<SemeleBluetoothDevice> devices = new ArrayList<>();
  final DeviceScanPresenter presenter;

  public BleDeviceListAdapter(DeviceScanPresenter presenter) {
    this.presenter = presenter;
  }

  @Override
  public BtDeviceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    final View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_bt_device, parent, false);
    return new BtDeviceViewHolder(v);
  }

  @Override
  public void onBindViewHolder(BtDeviceViewHolder holder, final int position) {
    holder.bindView(devices.get(position), v -> presenter.connectTo(devices.get(position)));
  }

  @Override
  public int getItemCount() {
    return devices.size();
  }

  public void add(SemeleBluetoothDevice device) {
    if (!devices.contains(device)) {
      devices.add(device); // avoid duplicates
    }
    this.notifyDataSetChanged();
  }

  public void clear() {
    devices.clear();
    this.notifyDataSetChanged();
  }
}
