package com.fireminder.semele.model;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.fireminder.semele.model.sql.SqlTable;

import java.util.HashMap;

import timber.log.Timber;

public class SemeleContentProvider extends ContentProvider {

  public static final String HEALTH_DB_NAME = "health.db";
  public static final int HEALTH_DB_VERSION = 1;
  private static final String TAG = "SemeleContentProvider";

  private static final int SESSION_ID = 0;
  private static final int BLE_DEVICE_ID = 1;
  private static final int USER_ID = 2;
  private static final int BPM_ID = 3;
  private static final int SESSION_METADATA_ID = 4;
  private static final int CALORIES_ID = 5;

  public static final String AUTHORITY = "com.fireminder.semele.provider";
  public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY);

  private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

  private static HashMap<Integer, String> idTableMap = new HashMap<>();

  static {
    sUriMatcher.addURI(AUTHORITY, SessionTable.TABLE_NAME, SESSION_ID);
    sUriMatcher.addURI(AUTHORITY, SessionMetadataTable.TABLE_NAME, SESSION_METADATA_ID);
    sUriMatcher.addURI(AUTHORITY, BpmTable.TABLE_NAME, BPM_ID);
    sUriMatcher.addURI(AUTHORITY, UserTable.TABLE_NAME, USER_ID);
    idTableMap.put(SESSION_ID, SessionTable.TABLE_NAME);
    idTableMap.put(SESSION_METADATA_ID, SessionMetadataTable.TABLE_NAME);
    idTableMap.put(USER_ID, UserTable.TABLE_NAME);
    idTableMap.put(BPM_ID, BpmTable.TABLE_NAME);
  }

  private SQLiteDatabase healthDb;

  @Override
  public boolean onCreate() {
    HealthDbHelper healthDbHelper = new HealthDbHelper(getContext());
    healthDb = healthDbHelper.getWritableDatabase();
    return true;
  }

  private SqlTable getTableFromUriThrowIfEmpty(@NonNull Uri uri) {
    int tableId = sUriMatcher.match(uri);
    SqlTable table = HealthDbHelper.tables.get(tableId);
    if (table == null) {
      throw new IllegalArgumentException("No table exists for uri: " + uri);
    }
    return table;
  }

  @Override
  public Cursor query(Uri uri, String[] projection, String selection,
                      String[] selectionArgs, String sortOrder) {
    SqlTable table = getTableFromUriThrowIfEmpty(uri);
    SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
    builder.setTables(table.getTableName());

    Cursor cursor = builder.query(healthDb, projection, selection, selectionArgs, null, null, sortOrder);

    if (getContext() != null) {
      cursor.setNotificationUri(getContext().getContentResolver(), uri);
    }

    return cursor;
  }

  @Override
  public String getType(Uri uri) {
    return null;
  }

  @Override
  public Uri insert(Uri uri, ContentValues values) {
    final SqlTable table = getTableFromUriThrowIfEmpty(uri);
    final long id = healthDb.insertOrThrow(table.getTableName(), null, values);
    final Uri insertedUri = table.getUri().buildUpon().appendPath(String.valueOf(id)).build();
    notifyChange(insertedUri);
    return insertedUri;
  }

  private void notifyChange(Uri uri) {
    getContext().getContentResolver().notifyChange(uri, null);
  }

  @Override
  public int delete(Uri uri, String selection, String[] selectionArgs) {
    final SqlTable table = getTableFromUriThrowIfEmpty(uri);
    int count = 0;
    try {
      count = healthDb.delete(table.getTableName(), selection, selectionArgs);
    } catch (Exception ex) {
      Timber.e(ex, "delete: " + selection + selectionArgs, ex);
    }
    return count;
  }

  @Override
  public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
    final SqlTable table = getTableFromUriThrowIfEmpty(uri);
    int count = 0;
    try {
      count = healthDb.update(table.getTableName(), values, selection, selectionArgs);
    } catch (Exception ex) {
      Timber.e(ex, "delete: " + selection + selectionArgs, ex);
    }
    return count;
  }

  private static class HealthDbHelper extends SQLiteOpenHelper {

    public static HashMap<Integer, SqlTable> tables = new HashMap<>();
    static {
      tables.put(USER_ID, new UserTable());
      tables.put(SESSION_ID, new SessionTable());
      tables.put(SESSION_METADATA_ID, new SessionMetadataTable());
      tables.put(BPM_ID, new BpmTable());
      tables.put(CALORIES_ID, new CaloriesBurnedTable());
      tables.put(BLE_DEVICE_ID, new BleDeviceTable());
    }

    // Exposed for testing upgrades
    public HealthDbHelper(Context context, int version) {
      super(context, HEALTH_DB_NAME, null, version);
    }

    public HealthDbHelper(Context context) {
      super(context, HEALTH_DB_NAME, null, HEALTH_DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
      for (SqlTable model : tables.values()) {
        db.execSQL(model.getCreateCommand());
      }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
      // Not yet needed
    }
  }

}
