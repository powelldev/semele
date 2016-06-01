package com.fireminder.semele;

import org.junit.runners.model.InitializationError;
import org.robolectric.RobolectricGradleTestRunner;

import rx.Scheduler;
import rx.plugins.RxJavaPlugins;
import rx.plugins.RxJavaSchedulersHook;
import rx.schedulers.Schedulers;

/**
 * Override schedulers to all run on the same thread during tests.
 */
public class RxJavaTestRunner extends RobolectricGradleTestRunner {
  public RxJavaTestRunner(Class<?> klass) throws InitializationError {
    super(klass);

    RxJavaPlugins.getInstance().registerSchedulersHook(new RxJavaSchedulersHook() {
      @Override
      public Scheduler getNewThreadScheduler() {
        return Schedulers.immediate();
      }

      @Override
      public Scheduler getIOScheduler() {
        return Schedulers.immediate();
      }

      @Override
      public Scheduler getComputationScheduler() {
        return Schedulers.immediate();
      }
    });

  }
}
