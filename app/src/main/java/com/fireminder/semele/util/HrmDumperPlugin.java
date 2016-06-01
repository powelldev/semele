package com.fireminder.semele.util;

import android.content.Intent;

import com.facebook.stetho.dumpapp.ArgsHelper;
import com.facebook.stetho.dumpapp.DumpException;
import com.facebook.stetho.dumpapp.DumperContext;
import com.facebook.stetho.dumpapp.DumperPlugin;
import com.fireminder.semele.BpmBroadcastReceiver;
import com.fireminder.semele.SemeleApp;
import com.fireminder.semele.model.BpmTable;

import java.io.PrintStream;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.schedulers.Schedulers;

public class HrmDumperPlugin implements DumperPlugin {

  private static final String CMD_SEND = "send";

  @Override
  public String getName() {
    return "hrm";
  }

  @Override
  public void dump(DumperContext dumpContext) throws DumpException {
    PrintStream writer = dumpContext.getStdout();
    Iterator<String> argsIter = dumpContext.getArgsAsList().iterator();

    String command = ArgsHelper.nextOptionalArg(argsIter, null);

    if (CMD_SEND.equalsIgnoreCase(command)) {
      send();
    }
  }

  static void broadcastUpdate(BpmTable.Bpm bpm) {
    Intent intent = new Intent(BpmBroadcastReceiver.ACTION_HEART_RATE);
    intent.putExtra(BpmBroadcastReceiver.EXTRA_HEART_RATE, bpm);
    SemeleApp.get().sendBroadcast(intent);
  }

  void send() {
    Observable.interval(1, TimeUnit.SECONDS, Schedulers.newThread())
        .take(100, TimeUnit.SECONDS)
        .map(l -> l += 100)
        .map(Long::intValue)
        .map((Integer i) -> new BpmTable.Bpm(i, System.currentTimeMillis(), BpmTable.Zone.getZoneFromBpm(i, 25)))
        .subscribe(HrmDumperPlugin::broadcastUpdate);
  }
}
