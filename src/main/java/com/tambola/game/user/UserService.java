package com.tambola.game.user;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonObject;
import com.tambola.game.AudioMessageRequest;
import com.tambola.game.CreateUserResponse;
import com.tambola.game.Game;
import com.tambola.game.MessagingClient;
import com.tambola.game.NotificationMessage;
import com.tambola.game.SendMessageRequest;
import com.tambola.game.UserContext;
import com.tambola.game.game.GameService;
import com.tambola.game.game.UserDAO;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

  @Autowired
  private UserDAO userDAO;

  @Autowired
  private GameService gameService;

  @Autowired
  private MessagingClient messagingClient;

  public CreateUserResponse createUser(UserContext user){
    return new CreateUserResponse(userDAO.addUser(user));
  }

  public JsonObject sendMessage(Integer gameID, String mobileNumber,
      SendMessageRequest request){
    Game gameDetails = gameService.getGameDetails(gameID);
    UserContext userContext = userDAO.getUserByMob(mobileNumber).orElseThrow(RuntimeException::new);

    Map<String, Object> data = new ImmutableMap.Builder<String, Object>()
        .put("action", "BROADCAST")
        .put("message", request.getMessage())
        .put("senderName", userContext.getUserName())
        .put("senderMobNo", mobileNumber)
        .build();

    NotificationMessage message = NotificationMessage.builder()
        .to(gameDetails.getNotificationKey())
        .data(data)
        .build();
    return messagingClient.sendMessage(message).toCompletableFuture().join();
  }

  public List<UserContext> getUserList(Integer gameID){
    return userDAO.getUsersForGameID(gameID);
  }

  public String getNotificationKeyForUser(String mobileNumber){
    return userDAO.getUserByMob(mobileNumber).map(UserContext::getNotificationKey).orElseThrow(()-> new RuntimeException());
  }

  public String getNotificationKeyForUserById(Integer userID){
    return userDAO.getUserById(userID).map(UserContext::getNotificationKey).orElseThrow(()-> new RuntimeException());
  }

  public JsonObject updateAudioMessageUrl(Integer gameID, String mobileNumber,
      AudioMessageRequest request){
    Game gameDetails = gameService.getGameDetails(gameID);
    UserContext userContext = userDAO.getUserByMob(mobileNumber).orElseThrow(RuntimeException::new);
    Map<String, Object> data = new ImmutableMap.Builder<String, Object>()
        .put("action", "AUDIO")
        .put("url", request.getUrl())
        .put("senderName", userContext.getUserName())
        .put("senderMobNo", mobileNumber)
        .build();

    NotificationMessage message = NotificationMessage.builder()
        .to(gameDetails.getNotificationKey())
        .data(data)
        .build();
    return messagingClient.sendMessage(message).toCompletableFuture().join();
  }
}
