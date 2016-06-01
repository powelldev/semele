package com.fireminder.semele;

public interface HeartRateListener {
  void onHeartRateRead(int bpm);
  void onEnergyExpenditureRead(int ee);
}
