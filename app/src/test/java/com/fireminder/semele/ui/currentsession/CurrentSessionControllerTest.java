package com.fireminder.semele.ui.currentsession;

import com.fireminder.semele.model.BpmTable;
import com.fireminder.semele.model.UserTable;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class CurrentSessionControllerTest {

  CurrentSessionController controller;

  @Mock
  UserTable userModel;

  @Mock
  CurrentSessionController.SessionView view;


  @Captor
  ArgumentCaptor<UserTable.UserFoundListener> listenerArgumentCaptor;

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    controller = new CurrentSessionController(userModel, view);
  }

  @Test
  public void testProcess() throws Exception {
    controller.process(new BpmTable.Bpm(56, System.currentTimeMillis(), 3));
    verify(userModel, times(1)).currentUserAsync(listenerArgumentCaptor.capture());
    listenerArgumentCaptor.getValue().onUserFound(null);
    verify(view, times(1)).populate(Mockito.any(BpmTable.Bpm.class));
  }

  @Test
  public void testFromBpm() throws Exception {

  }
}