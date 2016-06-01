package com.fireminder.semele;

import com.fireminder.semele.model.BpmTable;
import com.fireminder.semele.model.SessionMetadataTable;
import com.fireminder.semele.model.SessionTable;
import com.fireminder.semele.model.UserTable;

import java.util.List;

import timber.log.Timber;

import static com.fireminder.semele.model.BpmTable.*;
import static com.fireminder.semele.model.SessionMetadataTable.*;
import static com.fireminder.semele.model.SessionTable.*;
import static com.fireminder.semele.model.UserTable.*;

public class SessionCreator {

  private UserTable userTable;
  private BpmTable bpmModel;

  public SessionCreator(UserTable userTable, BpmTable bpmModel) {
    this.userTable = userTable;
    this.bpmModel = bpmModel;
  }

  public void createAndInsert(SessionTable sessionTable, SessionMetadataTable metadataTable) {
    List<SessionTable.Session> sessions = sessionTable.getAll();
    for (Session session : sessions) {
      if (!metadataTable.exists(session.id)) {
        metadataTable.add(create(session));
      }
    }

  }
  public SessionMetadata create(Session session) {
    User user = userTable.get(session.userId);
    SessionsStats sessionsStats = new SessionsStats(session);
    sessionsStats.setUser(user);
    Timber.v(session.toString());
    long stime = session.startTime;
    long etime = session.endTime;
    Timber.v("stime " + stime + " etime " + etime);
    for (Bpm bpm : bpmModel.getAll()) {
      if (bpm.timestamp >= stime && etime >= bpm.timestamp) {
        Timber.v(bpm.toString());
      }
    }
    BpmCursor cursor = bpmModel.getAllBetween(stime, etime);
    for (Bpm bpm : cursor) {
      sessionsStats.add(bpm);
      Timber.v("FOUND BY getallbetween: " + bpm.toString());
    }
    return new SessionMetadata(sessionsStats);
  }

}

