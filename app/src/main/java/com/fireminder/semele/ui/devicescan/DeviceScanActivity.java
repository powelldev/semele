package com.fireminder.semele.ui.devicescan;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.fireminder.semele.R;
import com.fireminder.semele.bluetooth.SemeleBluetoothDevice;

import timber.log.Timber;

public class DeviceScanActivity extends AppCompatActivity implements
    DeviceScanPresenter.IDeviceScanView,
    View.OnClickListener {

  private DeviceScanPresenter presenter;

  private Button actionButton;
  private TextView instructionsTv;
  private BleDeviceListAdapter deviceListAdapter;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_device_scan);

    presenter = new DeviceScanPresenter(this);
    presenter.bindView(this);

    actionButton = (Button) findViewById(R.id.action);
    instructionsTv = (TextView) findViewById(R.id.instructions);
    actionButton.setOnClickListener(this);

    deviceListAdapter = new BleDeviceListAdapter(presenter);
    RecyclerView foundDevicesListView = (RecyclerView) findViewById(R.id.recyclerView);
    foundDevicesListView.setAdapter(deviceListAdapter);
    foundDevicesListView.setLayoutManager(new LinearLayoutManager(this));
  }

  @Override
  protected void onResume() {
    super.onResume();
    IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
    registerReceiver(bluetoothStateUpdateReceiver, filter);
    presenter.onResume(this);
  }

  @Override
  protected void onPause() {
    super.onPause();
    unregisterReceiver(bluetoothStateUpdateReceiver);
  }

  @Override
  public void onClick(View v) {
    presenter.actionClicked(this);
  }

  @Override
  public void updateView(boolean isBtEnabled) {
    if (isBtEnabled) {
      actionButton.setText("Start pairing");
      instructionsTv.setText("");
    } else {
      actionButton.setText("Enable Bluetooth");
      instructionsTv.setText("Bluetooth is disabled. Semele needs Bluetooth to begin the pairing process");
    }
  }

  @Override
  public void addDevice(SemeleBluetoothDevice device) {
    deviceListAdapter.add(device);
  }

  @Override
  public void clearList() {
    deviceListAdapter.clear();
  }

  private final BroadcastReceiver bluetoothStateUpdateReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
      if (intent == null) {
        Timber.i("received null intent");
        return;
      }

      final String action = intent.getAction();

      if (TextUtils.isEmpty(action)) {
        Timber.e("Received null or empty action");
        return;
      }

      if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
        final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
        presenter.stateChanged(state);
      }
    }
  };

}

