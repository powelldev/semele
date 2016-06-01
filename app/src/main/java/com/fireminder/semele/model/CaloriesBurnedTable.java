package com.fireminder.semele.model;

import com.fireminder.semele.model.sql.Column;
import com.fireminder.semele.model.sql.SqlTable;

public class CaloriesBurnedTable extends SqlTable {
public static final String TABLE_NAME = "calories_burned";

public static final String SESSION_ID = "session_id";
public static final String CALORIES_BURNED = "calories_burned";
public static final String CREATED = "created";

  public CaloriesBurnedTable() {
    super();
    addColumn(new Column(SESSION_ID, Column.Type.INTEGER));
    addColumn(new Column(CALORIES_BURNED, Column.Type.INTEGER));
    addColumn(new Column(CREATED, Column.Type.DATE));

    addForeignKey(SESSION_ID, SessionTable.TABLE_NAME, SessionTable.ID);
  }

  @Override
  public String getTableName() {
    return TABLE_NAME;
  }
}
