package com.fireminder.semele;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fireminder.semele.model.SessionMetadataTable;
import com.fireminder.semele.model.SessionTable;

import java.util.ArrayList;
import java.util.List;

import static com.fireminder.semele.model.SessionMetadataTable.SessionMetadata;
import static com.fireminder.semele.model.SessionTable.Session;

public class SessionListActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_session_list);
    RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
    recyclerView.setLayoutManager(new LinearLayoutManager(this));
    SessionAdapter adapter = new SessionAdapter();
    adapter.add(new SessionTable().getAll(), new SessionMetadataTable().getAll());
    recyclerView.setAdapter(adapter);
  }



  private static class SessionViewHolder extends RecyclerView.ViewHolder {
    TextView sessionInfoView;

    public SessionViewHolder(View view) {
      super(view);
      sessionInfoView = (TextView) view.findViewById(R.id.sessionInfo);
    }

    public void bindView(Session session, SessionMetadata sessionMetadata) {
      sessionInfoView.setText(session.toString() + "\n" + sessionMetadata.toString());
    }
  }

  private static class SessionAdapter extends RecyclerView.Adapter<SessionViewHolder> {

    List<Session> sessions = new ArrayList<>();
    List<SessionMetadata> sessionMetaList = new ArrayList<>();

    @Override
    public SessionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      LayoutInflater inflater = LayoutInflater.from(parent.getContext());
      return new SessionViewHolder(inflater.inflate(R.layout.row_session_info, parent, false));
    }

    @Override
    public void onBindViewHolder(SessionViewHolder holder, int position) {
      holder.bindView(sessions.get(position), sessionMetaList.get(position));
    }

    @Override
    public int getItemCount() {
      return sessionMetaList.size();
    }

    public void add(List<Session> sessions, List<SessionMetadata> sessionMetadatas) {
      sessionMetaList = sessionMetadatas;
      this.sessions = sessions;
      notifyDataSetChanged();
    }
  }
}

