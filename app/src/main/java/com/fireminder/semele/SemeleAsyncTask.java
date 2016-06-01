package com.fireminder.semele;

import android.os.AsyncTask;

public abstract class SemeleAsyncTask extends AsyncTask<Void, Void, Boolean> {

  @Override
  protected Boolean doInBackground(Void... params) {
    return doInBackground();
  }

  @Override
  protected void onPostExecute(Boolean aBoolean) {
    if (aBoolean) {
      onSuccess();
    } else {
      onFailure();
    }
  }

  // override to implement
  public void onSuccess() {}

  // override to implement
  public void onFailure() {}

  public abstract boolean doInBackground();

  public void submit() {
    this.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null);
  }
}

