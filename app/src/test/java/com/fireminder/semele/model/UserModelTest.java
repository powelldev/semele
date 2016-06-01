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

import static com.fireminder.semele.model.UserTable.*;

@Config(manifest = "app/src/main/AndroidManifest.xml", sdk = 21, constants = BuildConfig.class)
@RunWith(RxJavaTestRunner.class)
public class UserModelTest {

  UserTable model;

  @Before
  public void setup() {
    model = new UserTable();
  }

  @Test
  public void testAdd_update_current_user() {
    // Since the second user is marked as current, it should override the previous user's current status
    model.addUser(26, 170, 2100, System.currentTimeMillis(), true);
    model.addUser(25, 170, 2100, System.currentTimeMillis(), true);

    Cursor c = SemeleApp.get().getContentResolver().query(model.getUri(), null, null, null, DATE_MODIFIED + " ASC");
    UserCursor userCursor = new UserCursor(c);
    userCursor.moveToFirst();
    User user1 = userCursor.peek();
    userCursor.moveToNext();
    User user2 = userCursor.peek();

    Assert.assertEquals(true, user2.isCurrent);
    Assert.assertEquals(false, user1.isCurrent);

    Assert.assertEquals(model.currentUser(), user2);
  }

  @Test
  public void testAdd() {
    model.addUser(25, 170, 2100, System.currentTimeMillis(), true);
    Cursor c = SemeleApp.get().getContentResolver().query(model.getUri(), null, null, null, null);

    c.moveToFirst();
    int age = c.getInt(c.getColumnIndex(AGE));

    Assert.assertEquals(25, age);
  }

  @Test
  public void get() {
    model.addUser(25, 170, 2100, System.currentTimeMillis(), true);
    Assert.assertNotNull(model.get(1));
  }


}
