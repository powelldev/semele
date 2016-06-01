package com.fireminder.semele.ui.currentsession;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.fireminder.semele.BpmBroadcastReceiver;
import com.fireminder.semele.R;
import com.fireminder.semele.bluetooth.BleService;
import com.fireminder.semele.model.UserTable;

import static com.fireminder.semele.model.BpmTable.Bpm;
import static com.fireminder.semele.model.BpmTable.Zone;

public class CurrentSessionActivity extends AppCompatActivity
    implements CurrentSessionController.SessionView {

  private CurrentSessionController controller;

  private TextView bpmTv;
  private TextView zoneTv;

  private BroadcastReceiver heartRateReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
      if (controller != null) {
        Bpm bpm = intent.getParcelableExtra(BpmBroadcastReceiver.EXTRA_HEART_RATE);
        controller.process(bpm);
      }
    }
  };

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_current_session);
    controller = new CurrentSessionController(new UserTable(), this);
    bpmTv = (TextView) findViewById(R.id.bpm);
    zoneTv = (TextView) findViewById(R.id.zone);
  }

  @Override
  protected void onResume() {
    super.onResume();

    SharedPreferences preferences = this.getSharedPreferences("semele-pref", Context.MODE_PRIVATE);
    String deviceAddress = preferences.getString("ble-device-address", "");

    startService(BleService.connectIntent(this, deviceAddress));

    IntentFilter filter = new IntentFilter(BpmBroadcastReceiver.ACTION_HEART_RATE);
    registerReceiver(heartRateReceiver, filter);
  }

  @Override
  protected void onPause() {
    super.onPause();
    unregisterReceiver(heartRateReceiver);
  }

  @Override
  public void populate(Bpm bpm) {
    zoneTv.setText(Zone.toStringResId(bpm.zone));
    bpmTv.setText(bpm.bpm + " bpm");
  }
}

