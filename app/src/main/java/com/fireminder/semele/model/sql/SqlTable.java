package com.fireminder.semele.model.sql;

import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Pair;

import com.fireminder.semele.model.SemeleContentProvider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class SqlTable {

  private List<Column> columns = new ArrayList<>();
  private HashMap<String, Pair<String, String>> foreignKeyMap = new HashMap<>();

  public static final String ID = BaseColumns._ID;

  public SqlTable() {
    columns.add(new Column(ID, Column.Type.INTEGER, null, true, false));
  }

  public void addForeignKey(String name, String tableReference, String keyReference) {
    foreignKeyMap.put(name, new Pair<>(tableReference, keyReference));
  }

  public String getCreateCommand() {
    StringBuilder command = new StringBuilder();
    command.append("CREATE TABLE IF NOT EXISTS ")
        .append(getTableName())
        .append(" (");
    for (Column column : columns) {
      command.append(column.getCreateCommand())
          .append(", ");
    }

    for (String name : foreignKeyMap.keySet()) {
      Pair<String, String> tableKeyPair = foreignKeyMap.get(name);
      command.append(" FOREIGN KEY ( ")
          .append(name)
          .append(") REFERENCES ")
          .append(tableKeyPair.first)
           .append("(").append(tableKeyPair.second)
          .append("), ");
    }

    command.replace(command.length() - 2, command.length(), "); ");
    return command.toString();
  }

  public abstract String getTableName();

  protected void addColumn(Column column) {
    columns.add(column);
  }

  public Uri getUri() {
    return SemeleContentProvider.CONTENT_URI.buildUpon().appendPath(getTableName()).build();
  }

  public static int getInt(final Cursor cursor, final String columnName) {
    return cursor.getInt(cursor.getColumnIndex(columnName));
  }

  public static boolean getBool(final Cursor cursor, final String columnName) {
    return cursor.getInt(cursor.getColumnIndex(columnName)) == 1;
  }

}
