package com.fireminder.semele.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.SparseArray;

import com.fireminder.semele.SemeleApp;
import com.fireminder.semele.model.sql.Column;
import com.fireminder.semele.model.sql.SqlTable;
import com.venmo.cursor.IterableCursorWrapper;

import java.util.ArrayList;
import java.util.List;

import static com.fireminder.semele.model.BpmTable.Zone;

public class SessionMetadataTable extends SqlTable {

  public static final String TABLE_NAME = "sessions_metadata";

  public static final String SESSION_ID = "session_id";
  public static final String ZONE_1_TIME = "zone_1_time";
  public static final String ZONE_2_TIME = "zone_2_time";
  public static final String ZONE_3_TIME = "zone_3_time";
  public static final String ZONE_4_TIME = "zone_4_time";
  public static final String ZONE_5_TIME = "zone_5_time";
  public static final String CALORIES_BURNED_TOTAL = "calories_burned";
  public static final String DURATION = "dudration";
  public static final String AVG_BPM = "avg_bpm";
  public static final String AVG_CAL_PER_MIN = "avg_cpm";

  public SessionMetadataTable() {
    super();

    addColumn(new Column(SESSION_ID, Column.Type.INTEGER, null, false, true));
    addColumn(new Column(ZONE_1_TIME, Column.Type.INTEGER));
    addColumn(new Column(ZONE_2_TIME, Column.Type.INTEGER));
    addColumn(new Column(ZONE_3_TIME, Column.Type.INTEGER));
    addColumn(new Column(ZONE_4_TIME, Column.Type.INTEGER));
    addColumn(new Column(ZONE_5_TIME, Column.Type.INTEGER));
    addColumn(new Column(CALORIES_BURNED_TOTAL, Column.Type.INTEGER));
    addColumn(new Column(DURATION, Column.Type.INTEGER));
    addColumn(new Column(AVG_BPM, Column.Type.INTEGER));
    addColumn(new Column(AVG_CAL_PER_MIN, Column.Type.INTEGER));

    addForeignKey(SESSION_ID, SessionTable.TABLE_NAME, SessionTable.ID);
  }

  @Override
  public String getTableName() {
    return TABLE_NAME;
  }

  public void add(SessionMetadata metadata) {
    ContentValues cv = new ContentValues();
    cv.put(SESSION_ID, metadata.id);
    cv.put(ZONE_1_TIME, metadata.zoneTimePeak);
    cv.put(ZONE_2_TIME, metadata.zoneTimeCardio);
    cv.put(ZONE_3_TIME, metadata.zoneTimeFatBurn);
    cv.put(ZONE_4_TIME, metadata.zoneTimeWarmup);
    cv.put(ZONE_5_TIME, metadata.zoneTimeRest);
    //cv.put(CALORIES_BURNED_TOTAL, metadata);
    cv.put(DURATION, metadata.duration);
    cv.put(AVG_BPM, metadata.bpmAvg);
    //cv.put(AVG_CAL_PER_MIN, metadata);
    SemeleApp.get().getContentResolver().insert(getUri(), cv);
  }

  public boolean exists(long sessionId) {
    Cursor cursor = null;
    try {
      cursor = SemeleApp.get().getContentResolver().query(getUri(), null, SESSION_ID + " = ? ", new String[]{"" + sessionId}, null);
      return cursor.moveToFirst();
    } finally {
      if (cursor != null) {
        cursor.close();
      }
    }
  }

  public static class SessionMetadata {
    public final long duration;
    public final double bpmAvg;
    public final int zoneTimePeak;
    public final int zoneTimeCardio;
    public final int zoneTimeFatBurn;
    public final int zoneTimeWarmup;
    public final int zoneTimeRest;
    public final long id;

    public SessionMetadata(SessionsStats sessionsStats) {
      this.id = sessionsStats.session.id;
      this.duration = sessionsStats.duration;
      this.bpmAvg = sessionsStats.getBpmAvg();
      this.zoneTimePeak = sessionsStats.map.get(Zone.PEAK);
      this.zoneTimeCardio = sessionsStats.map.get(Zone.CARDIO);
      this.zoneTimeFatBurn = sessionsStats.map.get(Zone.FAT_BURN);
      this.zoneTimeWarmup = sessionsStats.map.get(Zone.WARMUP);
      this.zoneTimeRest = sessionsStats.map.get(Zone.REST);
    }

    public SessionMetadata(int zone1Time,
                           int zone2Time,
                           int zone3Time,
                           int zone4Time,
                           int zone5Time,
                           int duration,
                           double bpmAvg,
                           long id) {
      zoneTimePeak = zone1Time;
      zoneTimeCardio = zone2Time;
      zoneTimeFatBurn = zone3Time;
      zoneTimeWarmup = zone4Time;
      zoneTimeRest = zone5Time;
      this.duration = duration;
      this.id = id;
      this.bpmAvg = bpmAvg;
    }

    @Override
    public String toString() {
      return "SessionMetadata{" +
          "id=" + id +
          ", zoneTimeRest=" + zoneTimeRest +
          ", zoneTimeWarmup=" + zoneTimeWarmup +
          ", zoneTimeFatBurn=" + zoneTimeFatBurn +
          ", zoneTimeCardio=" + zoneTimeCardio +
          ", zoneTimePeak=" + zoneTimePeak +
          ", bpmAvg=" + bpmAvg +
          ", duration=" + duration +
          '}';
    }
  }

  static class MetadataCursor extends IterableCursorWrapper<SessionMetadata> {

    public MetadataCursor(Cursor cursor) {
      super(cursor);
    }

    @Override
    public SessionMetadata peek() {

      int sessionId = getInteger(SESSION_ID, -1);
      int zone1Time = getInteger(ZONE_1_TIME, -1);
      int zone2Time = getInteger(ZONE_2_TIME, -1);
      int zone3Time = getInteger(ZONE_3_TIME, -1);
      int zone4Time = getInteger(ZONE_4_TIME, -1);
      int zone5Time = getInteger(ZONE_5_TIME, -1);
      int duration = getInteger(DURATION, -1);
      double bpmAvg = getDouble(AVG_BPM, -1);
      return new SessionMetadata(zone1Time, zone2Time, zone3Time, zone4Time, zone5Time, duration, bpmAvg, sessionId);
    }
  }
  public List<SessionMetadata> getAll() {
    List<SessionMetadata> sessions = new ArrayList<>();
    Cursor cursor = SemeleApp.get().getContentResolver().query(getUri(), null, null, null, null);
    MetadataCursor metadataCursor = new MetadataCursor(cursor);
    for (SessionMetadata metadata : metadataCursor) {
      sessions.add(metadata);
    }
    return sessions;
  }
  public static class SessionsStats {

    private SparseArray<Integer> map = new SparseArray<>();
    private final long duration;
    private final SessionTable.Session session;
    private UserTable.User user;

    public SessionsStats(SessionTable.Session session) {
      this.session = session;
      duration = session.endTime - session.startTime;
      map.put(Zone.PEAK, 0);
      map.put(Zone.CARDIO, 0);
      map.put(Zone.FAT_BURN, 0);
      map.put(Zone.WARMUP, 0);
      map.put(Zone.REST, 0);
    }

    public void setUser(UserTable.User user) {
      this.user = user;
    }

    public void add(BpmTable.Bpm bpm) {
      Integer i = map.get(bpm.zone);
      i++;
      map.put(bpm.zone, i);
      bpmAvg = calcAverage(bpmAvg, bpmCount, bpm.bpm);
      bpmCount++;
    }

    private double bpmAvg = 0;
    private int bpmCount = 0;

    public double getBpmAvg() {
      return bpmAvg;
    }


    private double calcAverage(double previousAverage, int previousCount, double incoming) {
      double weightedSum = previousAverage * previousCount;
      weightedSum += incoming;
      return (double) weightedSum / (previousCount+1);
    }

  }
}
