package com.fireminder.semele;

import android.app.Notification;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Pair;

import com.fireminder.semele.model.BpmTable;
import com.fireminder.semele.model.SessionTable;
import com.fireminder.semele.model.UserTable;
import com.google.common.collect.EvictingQueue;

import java.util.Queue;

import timber.log.Timber;

public class ActiveSessionService extends Service {

  public ActiveSessionService() {}
  public static final String CMD_START_NEW_SESSION = "cmd-start-new-session";
  public static final String CMD_END_SESSION = "cmd-end-session";

  // broadcast onHRMConnected starts this service
  // onHRMdisconnected ends this service
  // ending the service causes the session log to be saved
  // and the SessionCreator process to be queued


  // bpm updates and running average need to be passed to this thing
  // best mechanism?
  // watching bpm table? SST, but too slow (potentially)
  // directly broadcast updates? X


  private ActiveSessionNotificationManager activeSessionNotificationManager;
  Queue<Integer> heartRateDataQueue = EvictingQueue.create(5);

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    if (intent == null) {
      Timber.e("Received null intent");
      return START_NOT_STICKY;
    }
    if (activeSessionNotificationManager == null) {
      activeSessionNotificationManager = new ActiveSessionNotificationManager();
    }
    final String action  = intent.getAction();
    switch (action) {
      case CMD_START_NEW_SESSION:
        startSession();
        break;
      case CMD_END_SESSION:
        endSession();
        stopSelf(startId);
        break;
      default:
        Timber.e("Unknown action: " + action);
        break;
    }
    return super.onStartCommand(intent, flags, startId);
  }

  int sessionId = -1;

  public void startSession() {
    if (sessionId != -1) {
      Timber.wtf("sessionId already initiated - why are we trying to instantiate anaother one?");
      throw new RuntimeException("check wtf");
    }
    // TODO inject these
    SessionTable table = new SessionTable();
    UserTable userTable = new UserTable();

    sessionId = table.add(System.currentTimeMillis(), userTable.currentUser().userId);
    IntentFilter fitler = new IntentFilter(BpmBroadcastReceiver.ACTION_HEART_RATE);
    registerReceiver(broadcastReceiver, fitler);
    startForeground(31338, activeSessionNotificationManager.getBaseNotification());
  }


  public void endSession() {
    if (sessionId == -1) {
      Timber.wtf("tryign to end session that hasn't started.");
      throw new RuntimeException("check wtf");
    }
    SessionTable table = new SessionTable();
    table.end(sessionId, System.currentTimeMillis());
    unregisterReceiver(broadcastReceiver);
    stopForeground(true);
  }

  public void updateNotification(BpmTable.Bpm bpm) {
    heartRateDataQueue.add(bpm.bpm);
    Pair<Integer, Integer> minMax = getMinMax(heartRateDataQueue);

    String bpmVal = String.valueOf(bpm.bpm);
    String zoneVale = getString(BpmTable.Zone.toStringResId(bpm.zone));

    Notification note = activeSessionNotificationManager.updateNotification(bpmVal + " bpm",
        zoneVale, "5 sec * MIN: " + minMax.first + " MAX: " + minMax.second);

    startForeground(31338, note);
  }

  public Pair<Integer, Integer> getMinMax(Queue<Integer> queue) {
    int min = Integer.MAX_VALUE;
    int max = Integer.MIN_VALUE;
    for (Integer i : heartRateDataQueue) {
      if (i > max) {
        max = i;
      }
      if (i < min) {
        min = i;
      }
    }
    return new Pair<>(min, max);
  }

  BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
      BpmTable.Bpm bpm = intent.getParcelableExtra(BpmBroadcastReceiver.EXTRA_HEART_RATE);
      updateNotification(bpm);
    }
  };

  @Override
  public IBinder onBind(Intent intent) {
    throw new UnsupportedOperationException("Not yet implemented");
  }
}
