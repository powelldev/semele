package com.fireminder.semele.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.fireminder.semele.R;
import com.fireminder.semele.SemeleApp;
import com.fireminder.semele.model.sql.Column;
import com.fireminder.semele.model.sql.SqlTable;
import com.venmo.cursor.IterableCursorWrapper;

import java.io.PrintStream;

import rx.Observable;

public class BpmTable extends SqlTable {
  public static final String TABLE_NAME = "bpm";

  public static final String BPM = "bpm";
  public static final String CREATED = "created";
  public static final String ZONE = "zone";

  public BpmTable() {
    super();
    addColumn(new Column(BPM, Column.Type.INTEGER));
    addColumn(new Column(CREATED, Column.Type.INTEGER));
    addColumn(new Column(ZONE, Column.Type.INTEGER));
  }

  public static class Zone {

    public static final int PEAK = 1;
    public static final int CARDIO = 2;
    public static final int FAT_BURN = 3;
    public static final int WARMUP = 4;
    public static final int REST = 5;

    public static int getZoneFromBpm(int bpm, int age) {
      int max = 220 - age;
      double percentageOfMax = (double) bpm / (double) max;
      if (percentageOfMax >= .90) {
        return Zone.PEAK;
      } else if (percentageOfMax >= .75) {
        return Zone.CARDIO;
      } else if (percentageOfMax >= .50) {
        return Zone.FAT_BURN;
      } else if (percentageOfMax >= .25) {
        return Zone.WARMUP;
      } else {
        return Zone.REST;
      }
    }

    public static int toStringResId(int zone) {
      switch (zone) {
        case PEAK:
          return R.string.peak;
        case CARDIO:
          return R.string.cardio;
        case FAT_BURN:
          return R.string.fat_burn;
        case WARMUP:
          return R.string.warmup;
        case REST:
          return R.string.rest;
        default:
          throw new UnsupportedOperationException("Unknown zone int : "+ zone);
      }
    }
  }

  public void add(Bpm bpm) {
    ContentValues cv = new ContentValues();
    cv.put(BPM, bpm.bpm);
    cv.put(CREATED, bpm.timestamp);
    cv.put(ZONE, bpm.zone);
    SemeleApp.get().getContentResolver().insert(getUri(), cv);
  }


  public static class Bpm implements Parcelable {
    public final int bpm;
    public final long timestamp;
    public final int zone;

    public Bpm(int bpm, long timestamp, int zone) {
      this.bpm = bpm;
      this.timestamp = timestamp;
      this.zone = zone;
    }


    @Override
    public String toString() {
      return "Bpm{" +
          "bpm=" + bpm +
          ", timestamp=" + timestamp +
          ", zone=" + zone +
          '}';
    }

    @Override
    public int describeContents() {
      return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
      dest.writeInt(this.bpm);
      dest.writeLong(this.timestamp);
      dest.writeInt(this.zone);
    }

    protected Bpm(Parcel in) {
      this.bpm = in.readInt();
      this.timestamp = in.readLong();
      this.zone = in.readInt();
    }

    public static final Parcelable.Creator<Bpm> CREATOR = new Parcelable.Creator<Bpm>() {
      @Override
      public Bpm createFromParcel(Parcel source) {
        return new Bpm(source);
      }

      @Override
      public Bpm[] newArray(int size) {
        return new Bpm[size];
      }
    };
  }

  public static class BpmCursor extends IterableCursorWrapper<Bpm> {

    public BpmCursor(Cursor cursor) {
      super(cursor);
    }

    @Override
    public Bpm peek() {
      int bpm = getInteger(BPM, 0);
      long time = getLong(CREATED, 0);
      int zone = getInteger(ZONE, 0);
      return new Bpm(bpm, time, zone);
    }
  }

  public BpmCursor getAll() {
    Cursor c = SemeleApp.get()
        .getContentResolver().query(getUri(), null, null, null, null);
    return new BpmCursor(c);
  }
  public BpmCursor getAllBetween(long start, long end) {
    Cursor c = SemeleApp.get()
        .getContentResolver().query(getUri(), null, CREATED + " >= ? and ? >= " + CREATED,
            new String[] {"" + start, "" + end}, CREATED + " asc");
    return new BpmCursor(c);
  }

  public void dumpToFile(String filename, PrintStream printStream) {
    Cursor c = SemeleApp.get()
        .getContentResolver().query(getUri(), null, null, null, null);

    Observable.just(c)
        .map(BpmCursor::new)
        .flatMap(Observable::from)
        .map(Object::toString)
        .doOnTerminate(c::close)
        .subscribe(printStream::println);
  }

  @Override
  public String getTableName() {
    return TABLE_NAME;
  }
}
