package com.tambola.game.utils;

import com.google.common.collect.ImmutableList;
import java.util.List;

public class TryCatchUtils {

  public static <T> T tryCatch(Run<T> run, Catch<T> catchException, List<Class> exceptionsToCatch,
      Finally finallyAction) {
    try {
      return run.run();
    } catch (Throwable e) {
      for (Class exception :
          exceptionsToCatch) {
        if (exception.isAssignableFrom(e.getClass())) {
          return catchException.catchException(e);
        }
      }
      throw e;
    } finally {
      finallyAction.finallyAction();
    }
  }

  public static <T> T tryCatch(Run<T> run, Catch<T> catchException, List<Class> exceptionsToCatch) {
    return tryCatch(run, catchException, exceptionsToCatch, () -> {
    });
  }

  public static <T> T tryCatch(Run<T> run, Catch<T> catchException) {
    return tryCatch(run, catchException, ImmutableList.of(Throwable.class), () -> {
    });
  }

  @FunctionalInterface
  public interface Run<T> {

    T run();
  }

  @FunctionalInterface
  public interface Catch<T> {

    T catchException(Throwable e);
  }

  @FunctionalInterface
  public interface Finally {

    void finallyAction();
  }

}
