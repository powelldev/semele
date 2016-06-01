package com.fireminder.semele.model;

import com.fireminder.semele.BuildConfig;
import com.fireminder.semele.RxJavaTestRunner;
import com.fireminder.semele.SessionCreator;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLog;

import static com.fireminder.semele.model.UserTable.User;

@Config(manifest = "app/src/main/AndroidManifest.xml", sdk = 21, constants = BuildConfig.class)
@RunWith(RxJavaTestRunner.class)
public class SessionModelTest {

  SessionCreator creator;

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    ShadowLog.stream = System.out;


    BpmTable bpmModel = new BpmTable();
    for (int i = 0; i < 150; i++) {
      bpmModel.add(new BpmTable.Bpm(i, 1000000000L + i, BpmTable.Zone.getZoneFromBpm(i, 25)));

    }

    creator = new SessionCreator(new UserTable(), new BpmTable());
  }

  @Test
  public void foo() {
    UserTable userModel = new UserTable();
    userModel.addUser(25, 0, 0, 0, true);
    User user = userModel.get(1);
    SessionTable.Session session = new SessionTable.Session(1000000000L, 1000000149L, 0, user.userId);
    SessionMetadataTable.SessionMetadata metadata = creator.create(session);
    Assert.assertEquals(74.5, metadata.bpmAvg);
    Assert.assertEquals(149, metadata.duration);
    Assert.assertEquals(0, metadata.zoneTimePeak);
    Assert.assertEquals(49, metadata.zoneTimeRest);
  }

  /**
   * What is a session?
   * an interval of time over which we have heart rate data
   * from 12:01pm->12:31pm
   * @12:01:01 bpm: 134
   * @12:01:02 bpm: 135
   * ...
   * @12:30:59 bpm: 68
   *
   * from which we can generate meta-data
   *
   * int z1cnt... z5cnt
   * duration = 12:31 - 12:01
   * for each bpm in bpmmodel from t =  12:01 to 12:31
   * assign bpm a session_id
   * if bpm in particular zone, inc zone counter
   * keep running average
   *
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
   */
}
