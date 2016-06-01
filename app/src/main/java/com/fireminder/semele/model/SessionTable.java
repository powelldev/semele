package com.fireminder.semele.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import com.fireminder.semele.SemeleApp;
import com.fireminder.semele.model.sql.Column;
import com.fireminder.semele.model.sql.SqlTable;
import com.venmo.cursor.IterableCursorWrapper;

import java.util.ArrayList;
import java.util.List;

public class SessionTable extends SqlTable {

  public static final String TABLE_NAME = "sessions";
  public static final String TAG = "SessionModel";

  public static final String START_TIME = "start_time";
  public static final String END_TIME = "end_time";
  public static final String USER_ID = "user_id";

  public SessionTable() {
    super();
    addColumn(new Column(START_TIME, Column.Type.INTEGER));
    addColumn(new Column(END_TIME, Column.Type.INTEGER));
    addColumn(new Column(USER_ID, Column.Type.INTEGER));

    addForeignKey(USER_ID, UserTable.TABLE_NAME, UserTable.ID);
  }

  @Override
  public String getTableName() {
    return TABLE_NAME;
  }

  /**
   * Record a new session starting.
   * @param startingTimestamp timestamp at which this session has started.
   * @param userId user associated with this session
   * @return the SESSION_ID associated with the new entry.
   */
  public synchronized int add(long startingTimestamp, int userId) {
    ContentValues cv = new ContentValues();
    cv.put(START_TIME, startingTimestamp);
    cv.put(USER_ID, userId);
    Uri uri = SemeleApp.get().getContentResolver().insert(getUri(), cv);
    final String id = uri.getLastPathSegment();
    return Integer.parseInt(id);
  }


  public void end(int sessionId, long timestamp) {
    ContentValues cv = new ContentValues();
    cv.put(END_TIME, timestamp);
    SemeleApp.get().getContentResolver().update(getUri(), cv, ID + " = ? ", new String[]{"" + sessionId});
  }

  static class SessionCursor extends IterableCursorWrapper<Session> {

    public SessionCursor(Cursor cursor) {
      super(cursor);
    }

    @Override
    public Session peek() {
      long startTime = getLong(START_TIME, -1);
      long endTime = getLong(END_TIME, -1);
      long userId = getLong(USER_ID, -1);
      long id = getLong(ID, -1);
      return new Session(startTime, endTime, id, userId);
    }
  }

  public List<Session> getAll() {
    List<Session> sessions = new ArrayList<>();
    Cursor cursor = SemeleApp.get().getContentResolver().query(getUri(), null, null, null, null);
    SessionCursor sessionCursor = new SessionCursor(cursor);
    for (Session session : sessionCursor) {
      sessions.add(session);
    }
    cursor.close();
    return sessions;
  }

  public static class Session {
    public final long startTime;
    public final long endTime;
    public final long id;
    public final long userId;

    public Session(long startTime, long endTime, long id, long userId) {
      this.startTime = startTime;
      this.endTime = endTime;
      this.id = id;
      this.userId = userId;
    }
  }
}
