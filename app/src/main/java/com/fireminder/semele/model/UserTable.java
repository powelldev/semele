package com.fireminder.semele.model;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.support.annotation.Nullable;

import com.fireminder.semele.SemeleApp;
import com.fireminder.semele.SemeleAsyncTask;
import com.fireminder.semele.model.sql.Column;
import com.fireminder.semele.model.sql.SqlTable;
import com.venmo.cursor.IterableCursorWrapper;

import rx.Observable;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class UserTable extends SqlTable {
  public static final String TABLE_NAME = "users";

  public static final String AGE = "age";
  public static final String HEIGHT = "height";
  public static final String WEIGHT = "weight";
  public static final String DATE_MODIFIED = "date_modified";
  public static final String IS_CURRENT = "is_current";

  public UserTable() {
    super();
    addColumn(new Column(AGE, Column.Type.INTEGER));
    addColumn(new Column(HEIGHT, Column.Type.INTEGER));
    addColumn(new Column(WEIGHT, Column.Type.INTEGER));
    addColumn(new Column(DATE_MODIFIED, Column.Type.INTEGER));
    addColumn(new Column(IS_CURRENT, Column.Type.BOOLEAN));
  }

  public User get(long userId) {
    ContentResolver contentResolver = SemeleApp.get().getContentResolver();
    Cursor cursor = null;
    try {
      cursor = contentResolver.query(getUri(), null, ID + " = ?", new String[]{"" + userId}, null);
      if (!cursor.moveToFirst()) {
        Timber.e("No current user exists");
        return null;
      }
      return new UserCursor(cursor).peek();
    } finally {
      if (cursor != null) {
        cursor.close();
      }
    }
  }

  public static class UserCursor extends IterableCursorWrapper {
    public UserCursor(Cursor cursor) {
      super(cursor);
    }

    @Override
    public User peek() {
      int age = getInteger(AGE, -1);
      int heightCm = getInteger(HEIGHT, -1);
      int weightGrams = getInteger(WEIGHT, -1);
      int dateModified = getInteger(DATE_MODIFIED, -1);
      boolean isCurrent = getBoolean(IS_CURRENT, false);
      int userId = getInteger(ID, -1);
      return new User(age, heightCm, weightGrams, dateModified, isCurrent, userId);
    }
  }


  public static class User {
    public final int age;
    public final int heightCm;
    public final int weightGrams;
    public final long dateModified;
    public final boolean isCurrent;
    public final int userId;

    User(int age, int heightCm, int weightGrams, long dateModified, boolean isCurrent, int userId) {
      this.age = age;
      this.heightCm = heightCm;
      this.weightGrams = weightGrams;
      this.dateModified = dateModified;
      this.isCurrent = isCurrent;
      this.userId = userId;
    }

    public ContentValues toContentValues() {
      ContentValues cv = new ContentValues();
      cv.put(AGE, age);
      cv.put(HEIGHT, heightCm);
      cv.put(WEIGHT, weightGrams);
      cv.put(DATE_MODIFIED, dateModified);
      cv.put(IS_CURRENT, isCurrent);
      return cv;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      User user = (User) o;

      if (age != user.age) return false;
      if (heightCm != user.heightCm) return false;
      if (weightGrams != user.weightGrams) return false;
      if (dateModified != user.dateModified) return false;
      if (isCurrent != user.isCurrent) return false;
      return userId == user.userId;

    }

    @Override
    public int hashCode() {
      int result = age;
      result = 31 * result + heightCm;
      result = 31 * result + weightGrams;
      result = 31 * result + (int) (dateModified ^ (dateModified >>> 32));
      result = 31 * result + (isCurrent ? 1 : 0);
      result = 31 * result + userId;
      return result;
    }

    @Override
    public String toString() {
      return "User{" +
          "age=" + age +
          ", heightCm=" + heightCm +
          ", weightGrams=" + weightGrams +
          ", dateModified=" + dateModified +
          ", isCurrent=" + isCurrent +
          ", userId=" + userId +
          '}';
    }
  }

  public void addUser(int age, int heightCm, int weightGrams, long dateModified, boolean isCurrent) {
    ContentResolver contentResolver = SemeleApp.get().getContentResolver();
    ContentValues cv = new ContentValues();
    cv.put(AGE, age);
    cv.put(HEIGHT, heightCm);
    cv.put(WEIGHT, weightGrams);
    cv.put(DATE_MODIFIED, dateModified);
    cv.put(IS_CURRENT, isCurrent);

    if (isCurrent) {
      // Mark all other records as not current
      ContentValues notCurrentCv = new ContentValues();
      notCurrentCv.put(IS_CURRENT, false);
      contentResolver.update(getUri(), notCurrentCv, null, null);
    }

    contentResolver.insert(getUri(), cv);
  }

  @Nullable
  public User currentUser() {
    ContentResolver contentResolver = SemeleApp.get().getContentResolver();
    Cursor cursor = null;
    try {
      cursor = contentResolver.query(getUri(), null, IS_CURRENT + " IS ?", new String[]{"1"}, null);
      if (!cursor.moveToFirst()) {
        Timber.e("No current user exists");
        return null;
      }
      return new UserCursor(cursor).peek();
    } finally {
      if (cursor != null) {
        cursor.close();
      }
    }
  }


  public void currentUserAsync(final UserFoundListener callback) {
    new SemeleAsyncTask() {
      User currentUser;
      @Override
      public boolean doInBackground() {
        ContentResolver contentResolver = SemeleApp.get().getContentResolver();
        Cursor cursor = contentResolver.query(getUri(), null, "WHERE " + IS_CURRENT + " IS ?", new String[] {"true"}, null);
        if (cursor.moveToFirst()) {
          currentUser = new UserCursor(cursor).peek();
        } else {
          onFailure();
        }
        cursor.close();
        return true;
      }

      @Override
      public void onSuccess() {
        callback.onUserFound(currentUser);
      }

      @Override
      public void onFailure() {
        callback.noUserExists();
      }

    }.submit();
  }

  public interface UserFoundListener {
    void onUserFound(User user);
    void noUserExists();
  }

  public Observable<User> getUserObs(long userId) {
    return Observable.just(userId)
        .observeOn(Schedulers.io())
        .flatMap(s -> Observable.just(new UserTable().get(userId)));
  }

  @Override
  public String getTableName() {
    return TABLE_NAME;
  }
}
