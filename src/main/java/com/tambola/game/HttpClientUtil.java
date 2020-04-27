package com.tambola.game;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public final class HttpClientUtil {
  public static <T> CompletableFuture<T> toCompletableFuture(Call<T> call) {
    CompletableFuture<T> cf = new CompletableFuture();
    call.enqueue(convertToCallback(cf));
    return cf;
  }

  private static <T> Callback<T> convertToCallback(final CompletableFuture<T> cf) {
    return new Callback<T>() {
      public void onResponse(Call<T> call, Response<T> response) {
        if (response.isSuccessful()) {
          cf.complete(response.body());
        } else {
          try {
            cf.completeExceptionally(new ExecutionException("Request processing failed", new Throwable(response.errorBody().string())));
          } catch (IOException var4) {
            cf.completeExceptionally(new Throwable("Request processing failed"));
          }
        }

      }

      public void onFailure(Call<T> call, Throwable throwable) {
        cf.completeExceptionally(throwable);
      }
    };
  }

  private HttpClientUtil() {
    throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
  }
}