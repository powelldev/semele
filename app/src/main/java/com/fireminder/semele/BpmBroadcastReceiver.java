package com.fireminder.semele;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.fireminder.semele.model.BpmTable;

public class BpmBroadcastReceiver extends BroadcastReceiver {

  public static final String ACTION_HEART_RATE = "com.fireminder.semele.BpmBroadcastReceiver.BPM";
  public static final String EXTRA_HEART_RATE = "extra-heart-rate";

  // TODO inject
  BpmTable bpmModel = new BpmTable();

  @Override
  public void onReceive(Context context, Intent intent) {
    BpmTable.Bpm bpm = intent.getParcelableExtra(EXTRA_HEART_RATE);
    bpmModel.add(bpm);
  }

}
