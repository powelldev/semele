package com.fireminder.semele.util;

import com.facebook.stetho.dumpapp.ArgsHelper;
import com.facebook.stetho.dumpapp.DumpException;
import com.facebook.stetho.dumpapp.DumperContext;
import com.facebook.stetho.dumpapp.DumperPlugin;
import com.fireminder.semele.model.BpmTable;

import java.io.PrintStream;
import java.util.Iterator;

public class DbDumperPlugin implements DumperPlugin {

  private static final String CMD_CAT = "cat";

  @Override
  public String getName() {
    return "db";
  }

  @Override
  public void dump(DumperContext dumpContext) throws DumpException {
    PrintStream writer = dumpContext.getStdout();
    Iterator<String> argsIter = dumpContext.getArgsAsList().iterator();

    String command = ArgsHelper.nextOptionalArg(argsIter, null);

    if (CMD_CAT.equalsIgnoreCase(command)) {
      cat(writer);
    }
  }

  void cat(final PrintStream writer) {
      BpmTable bpmModel = new BpmTable();
      bpmModel.dumpToFile("bpm.txt", writer);
  }
}
