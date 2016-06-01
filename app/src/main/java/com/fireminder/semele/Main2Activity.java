package com.fireminder.semele;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.fireminder.semele.model.BpmTable;
import com.fireminder.semele.model.SessionMetadataTable;
import com.fireminder.semele.model.SessionTable;
import com.fireminder.semele.model.UserTable;

public class Main2Activity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main2);

    ((Button) findViewById(R.id.action)).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        UserTable table = new UserTable();
        if (table.currentUser() == null) {
          table.addUser(25, 0, 0, System.currentTimeMillis(), true);
        }
        Intent intent = new Intent(Main2Activity.this, ActiveSessionService.class);
        intent.setAction(ActiveSessionService.CMD_START_NEW_SESSION);
        SemeleApp.get().startService(intent);
      }
    });

    ((Button) findViewById(R.id.action2)).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent intent = new Intent(Main2Activity.this, ActiveSessionService.class);
        intent.setAction(ActiveSessionService.CMD_END_SESSION);
        SemeleApp.get().startService(intent);
      }
    });

    ((Button) findViewById(R.id.action3)).setOnClickListener(v -> {
      SessionCreator creator = new SessionCreator(new UserTable(), new BpmTable());
      creator.createAndInsert(new SessionTable(), new SessionMetadataTable());
    });
  }

}
