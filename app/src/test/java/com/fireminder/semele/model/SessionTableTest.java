package com.fireminder.semele.model;

import android.database.Cursor;

import com.fireminder.semele.BuildConfig;
import com.fireminder.semele.RxJavaTestRunner;
import com.fireminder.semele.SemeleApp;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;

@Config(manifest = "app/src/main/AndroidManifest.xml", sdk = 21, constants = BuildConfig.class)
@RunWith(RxJavaTestRunner.class)
public class SessionTableTest {

  SessionTable table;

  @Before
  public void setup() throws Exception {
    table = new SessionTable();
  }

  @Test
  public void create_session() {
    final int sessionId = table.add(1000000000L, 1);
    table.end(sessionId, 1000030000L);

    Cursor cursor = SemeleApp.get().getContentResolver().query(table.getUri(), null, SessionTable.ID + " = ? ", new String[]{"" + sessionId}, null);

    long eTs = cursor.getInt(cursor.getColumnIndex(SessionTable.END_TIME));
    long sTs = cursor.getInt(cursor.getColumnIndex(SessionTable.START_TIME));
    long uId = cursor.getInt(cursor.getColumnIndex(SessionTable.USER_ID));

    Assert.assertEquals(1000000000L, sTs);
    Assert.assertEquals(1000030000L, eTs);
    Assert.assertEquals(1, uId);
  }

  @Test
  public void add_returnsLatestId() throws Exception {
    Assert.assertEquals(1, table.add(1000000000L, 1 /* user id */));
    Assert.assertEquals(2, table.add(1000000000L, 4 /* user id */));
  }
}