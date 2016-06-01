package com.fireminder.semele.model;

import com.fireminder.semele.model.sql.Column;
import com.fireminder.semele.model.sql.SqlTable;

public class BleDeviceTable extends SqlTable {

  public static final String TABLE_NAME = "ble_devices";

  public static final String CONNECTED_STATE = "connected_state";
  public static final String NAME = "name";
  public static final String ADDRESS = "address";

  public BleDeviceTable() {
    super();
    addColumn(new Column(CONNECTED_STATE, Column.Type.INTEGER));
    addColumn(new Column(NAME, Column.Type.TEXT));
    addColumn(new Column(ADDRESS, Column.Type.TEXT));
  }

  @Override
  public String getTableName() {
    return TABLE_NAME;
  }
}
