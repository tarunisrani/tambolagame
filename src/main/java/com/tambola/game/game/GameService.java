package com.tambola.game.game;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import com.google.gson.JsonObject;
import com.tambola.game.Game;
import com.tambola.game.GameTicket;
import com.tambola.game.MessagingClient;
import com.tambola.game.NotificationGroup;
import com.tambola.game.NotificationGroupAdd;
import com.tambola.game.NotificationMessage;
import com.tambola.game.UserContext;
import com.tambola.game.ticketgenerator.model.TambolaTicketVO;
import com.tambola.game.ticketgenerator.service.RandomNumberGenerator;
import com.tambola.game.ticketgenerator.service.TicketService;
import java.security.SecureRandom;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletionStage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GameService {

  @Autowired
  private TicketService ticketService;

  @Autowired
  private GameTicketDAO gameTicketDAO;

  @Autowired
  private MessagingClient messagingClient;

  @Autowired
  private GameNumberDAO gameNumberDAO;

  @Autowired
  private GameDAO gameDAO;

  @Autowired
  private UserDAO userDAO;

  public Game createGame(Integer playerCount, String gameType, UserContext user){

    UserContext userContext = userDAO.getUserByMob(user.getMobileNumber())
        .orElseThrow(RuntimeException::new);

    List<Integer> gameIds = gameDAO.getGameIds();
    Integer gameID = new SecureRandom().nextInt(Integer.MAX_VALUE);
//    Integer gameID = 486935401;
    while(gameIds.contains(gameID)){
      gameID = new SecureRandom().nextInt(Integer.MAX_VALUE);
    }

    gameDAO.addGame(gameID, userContext.getUserID());

    CompletionStage<JsonObject> createGroup = messagingClient.createGroup(NotificationGroup.builder()
        .notification_key_name(String.format("TB_%d", gameID))
        .operation("create")
        .registration_ids(ImmutableList.of(String.valueOf(userContext.getNotificationKey())))
        .build());

    JsonObject createGroupResponse = createGroup.toCompletableFuture().join();
    System.out.println(createGroupResponse);
    if(createGroupResponse.has("notification_key")){
      gameDAO.updateNotificationKey(gameID, createGroupResponse.get("notification_key").getAsString());
    }

    List<TambolaTicketVO> tambolaTicketVOS = ticketService.generatePrintableTickets(playerCount);

    for(int ticketID=0;ticketID<tambolaTicketVOS.size();ticketID++){
      gameTicketDAO.addTickets(gameID, ticketID+1, tambolaTicketVOS.get(ticketID));
    }

    return Game.builder()
        .gameID(gameID.longValue())
        .ownerName(user.getUserName())
        .ticketList(tambolaTicketVOS)
        .build();
  }

  public TambolaTicketVO assignTicket(String mobileNumber, Integer gameID) {
    UserContext userContext = userDAO.getUserByMob(mobileNumber).orElseThrow(RuntimeException::new);
    Game game = gameDAO.getGameByID(gameID).orElseThrow(RuntimeException::new);

    Optional<GameTicket> ticket = gameTicketDAO
        .getTicketForByGameIDAndUser(gameID, mobileNumber);
    if(ticket.isPresent()){
      return ticket.get().getTicket();
    }
    Optional<GameTicket> availableTicket = gameTicketDAO.getAvailableTicket(gameID);
    if(availableTicket.isPresent()) {
      Integer ticketID = gameTicketDAO
          .assignTicketToUser(gameID, availableTicket.get().getTicketID(), mobileNumber);

      JsonObject addUserResponse = messagingClient.addUserToNotification(NotificationGroupAdd.builder()
          .notification_key_name(String.format("TB_%d", gameID))
          .notification_key(game.getNotificationKey())
          .operation("add")
          .registration_ids(ImmutableList.of(String.valueOf(userContext.getNotificationKey())))
          .build()).toCompletableFuture().join();
      System.out.println(addUserResponse);

      return gameTicketDAO.getTicketForByGameIDAndTicketID(gameID, ticketID).getTicket();
    }else{
      throw new RuntimeException("No ticket available");
    }
  }

  public Integer getNewNumber(Integer gameID){
    RandomNumberGenerator numberGenerator = RandomNumberGenerator.getInstance();
    List<Integer> numbers = gameNumberDAO.getNumbers(gameID);
    Set<Integer> numberSet = Sets.newHashSet(numbers);
    Integer nextNumber = numberGenerator.generateNextNumber();
    while(numberSet.contains(nextNumber)){
      nextNumber = numberGenerator.generateNextNumber();
    }
    numberSet.add(nextNumber);
    gameNumberDAO.addNumber(gameID, numberSet);

    sendNumberToPlayers(gameID, nextNumber);

    return nextNumber;
  }

  private void sendNumberToPlayers(Integer gameID, Integer nextNumber) {
    String notificationKey = gameDAO.getGameNotificationKey(gameID).orElseThrow(
        RuntimeException::new);

    NotificationMessage message = NotificationMessage.builder()
        .to(notificationKey)
        .data(ImmutableMap.of("number", String.valueOf(nextNumber)))
        .build();
    JsonObject sendMessageResponse = messagingClient.sendMessage(message).toCompletableFuture().join();
    System.out.println(sendMessageResponse);
  }
}
