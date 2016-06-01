package com.fireminder.semele.ui.currentsession;

import com.fireminder.semele.model.BpmTable;
import com.fireminder.semele.model.UserTable;

public class CurrentSessionController {

  private static final String TAG = "CurrentSessionControl";

  private final SessionView view;
  private final UserTable userModel;

  private UserTable.User currentUser = null;

  public CurrentSessionController(UserTable userModel, SessionView view) {
    this.view = view;
    this.userModel = userModel;
  }

  // Tell the view to populate itself with the correct zone and BPM
  // information. If we don't have a current active user, update asynchronously,
  // then update once loaded.
  public void process(final BpmTable.Bpm bpm) {

    view.populate(bpm);//fromBpm(bpm, currentUser));
    /*
    if (currentUser == null) {
      userModel.currentUser(new UserModel.UserFoundListener() {
        @Override
        public void onUserFound(UserModel.User user) {
          currentUser = user;
          if (view != null) {
            view.populate(bpm, fromBpm(bpm, currentUser));
          }
        }

        @Override
        public void noUserExists() {
          Log.e(TAG, "No current user found");
          throw new RuntimeException("Not user exists: this shouldn't happen");
        }

      });
    } else {
      view.populate(bpm, fromBpm(bpm, currentUser));
    }

      */
  }

  interface SessionView {
    void populate(BpmTable.Bpm bpm);
  }

}
