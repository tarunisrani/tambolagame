package com.tambola.game;

import static com.tambola.game.Utils.getRetrofit;

import com.google.gson.JsonObject;
import java.util.concurrent.CompletionStage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

@Component
public class MessagingClient {

  private final MessagingClientInterface messagingClientInterface;

  @Autowired
  public MessagingClient() {
    Retrofit retrofit = getRetrofit("https://fcm.googleapis.com/fcm/");
    this.messagingClientInterface = retrofit.create(MessagingClientInterface.class);
  }

  public CompletionStage<JsonObject> sendMessage(NotificationMessage notificationMessage) {
    return HttpClientUtil.toCompletableFuture(this.messagingClientInterface
        .sendMessage(notificationMessage));
  }

  public CompletionStage<JsonObject> createGroup(NotificationGroup notificationGroup) {
    return HttpClientUtil.toCompletableFuture(this.messagingClientInterface
        .createNotificationGroup(notificationGroup));
  }

  public CompletionStage<JsonObject> addUserToNotification(NotificationGroupAdd notificationGroupAdd) {
    return HttpClientUtil.toCompletableFuture(this.messagingClientInterface
        .addUserToNotification(notificationGroupAdd));
  }

  private interface MessagingClientInterface {
    @POST("send")
    @Headers({
        "Authorization: key=AAAATeDzL0E:APA91bGyv7fYOeMCRFk7D2UQycnEidg56kmsmCJR-qsUL8P5GEiMlaV2gPzzejFx6yUHZrcIfbBS9tAnvm7lGA_4ETmQQPwQS1pIcw9RQDIM9VYJoQGPQqb_lojgVXY6dYyF2OnZ6X8R",
        "project_id: 334486515521",
        "Content-Type: application/json"
    })
    Call<JsonObject> sendMessage(@Body NotificationMessage notificationMessage);

    @POST("notification")
    @Headers({
        "Authorization: key=AAAATeDzL0E:APA91bGyv7fYOeMCRFk7D2UQycnEidg56kmsmCJR-qsUL8P5GEiMlaV2gPzzejFx6yUHZrcIfbBS9tAnvm7lGA_4ETmQQPwQS1pIcw9RQDIM9VYJoQGPQqb_lojgVXY6dYyF2OnZ6X8R",
        "project_id: 334486515521",
        "Content-Type: application/json"
    })
    Call<JsonObject> createNotificationGroup(@Body NotificationGroup notificationGroup);

    @POST("notification")
    @Headers({
        "Authorization: key=AAAATeDzL0E:APA91bGyv7fYOeMCRFk7D2UQycnEidg56kmsmCJR-qsUL8P5GEiMlaV2gPzzejFx6yUHZrcIfbBS9tAnvm7lGA_4ETmQQPwQS1pIcw9RQDIM9VYJoQGPQqb_lojgVXY6dYyF2OnZ6X8R",
        "project_id: 334486515521",
        "Content-Type: application/json"
    })
    Call<JsonObject> addUserToNotification(@Body NotificationGroupAdd notificationGroupAdd);
  }
}