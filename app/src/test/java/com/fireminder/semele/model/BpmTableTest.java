package com.fireminder.semele.model;

import com.fireminder.semele.BuildConfig;
import com.fireminder.semele.RxJavaTestRunner;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;

@Config(manifest = "app/src/main/AndroidManifest.xml", sdk = 21, constants = BuildConfig.class)
@RunWith(RxJavaTestRunner.class)
public class BpmTableTest {

  BpmTable table;


  @Before
  public void setUp() throws Exception {
    table = new BpmTable();
  }
  @Test
  public void testGetAllBetween() throws Exception {
    // Empty table should have no values
    BpmTable.BpmCursor bpmCursor = (table.getAllBetween(0, Long.MAX_VALUE));
    Assert.assertEquals(0, bpmCursor.getCount());

    table.add(new BpmTable.Bpm(1, 1435269987, 1));

    bpmCursor = table.getAllBetween(1435267825, 1435351509);
    Assert.assertEquals(1, bpmCursor.getCount());
  }
}