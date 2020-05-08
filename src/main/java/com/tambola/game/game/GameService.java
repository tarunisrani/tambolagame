package com.tambola.game.game;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import com.google.gson.JsonObject;
import com.tambola.game.Game;
import com.tambola.game.GamePrize;
import com.tambola.game.GamePrizeRequest;
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
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;
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
  private GamePrizeDAO gamePrizeDAO;

  @Autowired
  private UserDAO userDAO;

  public Game createGame(Integer playerCount, String gameType, UserContext user){

    UserContext userContext = userDAO.getUserByMob(user.getMobileNumber())
        .orElseThrow(RuntimeException::new);

    List<Integer> gameIds = gameDAO.getGameIds();
    Integer gameID = new SecureRandom().nextInt(999999);
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

  public void markGameAsFinished(Integer gameID, String mobileNumber){
    UserContext userContext = userDAO.getUserByMob(mobileNumber).orElseThrow(RuntimeException::new);
    Game game = gameDAO.getGameByID(gameID).orElseThrow(RuntimeException::new);
    if(game.getOwnerID().intValue()!=userContext.getUserID()){
      throw new RuntimeException("Not an owner of game");
    }
    gameDAO.updateGameStatus(gameID, "FINISHED");
    informPlayerForGameEnd(game.getNotificationKey());
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

      informPlayerForNewJoiner(userContext.getUserName(), game.getNotificationKey());

      return gameTicketDAO.getTicketForByGameIDAndTicketID(gameID, ticketID).getTicket();
    }else{
      throw new RuntimeException("No ticket available");
    }
  }

  private void informPlayerForNewJoiner(String userName, String notificationKey) {
    NotificationMessage message = NotificationMessage.builder()
        .to(notificationKey)
        .data(ImmutableMap.of("newUser", userName))
        .build();
    messagingClient.sendMessage(message);
  }
  private void informPlayerForGameEnd(String notificationKey) {
    NotificationMessage message = NotificationMessage.builder()
        .to(notificationKey)
        .data(ImmutableMap.of("game", "END"))
        .build();
    messagingClient.sendMessage(message);
  }

  public Integer getNewNumber(Integer gameID, String mobileNumber){
    UserContext userContext = userDAO.getUserByMob(mobileNumber).orElseThrow(RuntimeException::new);
    Game game = gameDAO.getGameByID(gameID).orElseThrow(RuntimeException::new);
    if(game.getOwnerID().intValue()!=userContext.getUserID()){
      throw new RuntimeException("Not an owner of game");
    }

    RandomNumberGenerator numberGenerator = RandomNumberGenerator.getInstance();
    LinkedList<Integer> numbers = getGeneratedNumbers(gameID);
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

  public LinkedList<Integer> getGeneratedNumbers(Integer gameID) {
    return gameNumberDAO.getNumbers(gameID);
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

  public Game getGameDetails(Integer gameID) {
    return gameDAO.getGameByID(gameID).orElseThrow(RuntimeException::new);
  }

  public void addPrizes(GamePrizeRequest request){
    Set<String> collect = new HashSet<>(request.getPrizeName());
    collect.forEach(prizeName-> gamePrizeDAO.addGamePrize(request.getGameID(), prizeName, 0));
  }

  public List<GamePrize> getPrizeList(Integer gameID){
    return gamePrizeDAO.getPrizesByGameID(gameID);
  }

  public void updateWinnerOfPrize(Integer gameID, String prizeName, String winnerName){
    gamePrizeDAO.updatePrizeStatus(gameID, prizeName, winnerName);
    informPlayerAboutPrizeUpdate(gameID, prizeName, winnerName);
  }

  private void informPlayerAboutPrizeUpdate(Integer gameID, String prizeName,
      String winnerName) {
    String notificationKey = gameDAO.getGameNotificationKey(gameID).orElseThrow(
        RuntimeException::new);

    Map<String, Object> data = new ImmutableMap.Builder<String, Object>()
        .put("action", "PRIZE")
        .put("playerName", winnerName)
        .put("prizeName", prizeName)
        .build();

    NotificationMessage message = NotificationMessage.builder()
        .to(notificationKey)
        .data(data)
        .build();
    JsonObject sendMessageResponse = messagingClient.sendMessage(message).toCompletableFuture().join();
    System.out.println(sendMessageResponse);
  }

  private void informPlayerAboutAlarm(Integer gameID, String mobileNumber) {
    String notificationKey = gameDAO.getGameNotificationKey(gameID).orElseThrow(
        RuntimeException::new);

    UserContext userContext = userDAO.getUserByMob(mobileNumber).orElseThrow(RuntimeException::new);
    Map<String, Object> data = new ImmutableMap.Builder<String, Object>()
        .put("action", "ALARM")
        .put("playerName", userContext.getUserName())
        .build();

    NotificationMessage message = NotificationMessage.builder()
        .to(notificationKey)
        .data(data)
        .build();
    JsonObject sendMessageResponse = messagingClient.sendMessage(message).toCompletableFuture().join();
    System.out.println(sendMessageResponse);
  }

  public void claimPrize(Integer gameID, String prizeName, String mobileNumber, List<Integer> selectedNumbers){
    Game game = gameDAO.getGameByID(gameID).orElseThrow(RuntimeException::new);
    LinkedList<Integer> numbers = gameNumberDAO.getNumbers(gameID);
    boolean allMatch = selectedNumbers.stream().allMatch(numbers::contains);
    UserContext gameOwner = userDAO.getUserById(game.getOwnerID().intValue())
        .orElseThrow(RuntimeException::new);
    informPlayersAboutClaim(game.getNotificationKey(), mobileNumber, prizeName);
    informGameOwnerAboutClaim(gameID, gameOwner.getNotificationKey(), selectedNumbers, allMatch, mobileNumber, prizeName);
  }

  private void informGameOwnerAboutClaim(Integer gameID, String notificationKey,
      List<Integer> selectedNumbers, boolean allMatch, String mobileNumber, String prizeName) {
    GameTicket gameTicket = gameTicketDAO.getTicketForByGameIDAndUser(gameID, mobileNumber)
        .orElseThrow(RuntimeException::new);
    UserContext userContext = userDAO.getUserByMob(mobileNumber).orElseThrow(RuntimeException::new);

    Map<String, Object> data = new ImmutableMap.Builder<String, Object>()
        .put("action", "VERIFY")
        .put("selectedNumbers", selectedNumbers)
        .put("allMatch", allMatch)
        .put("ticket", gameTicket.getTicketID())
        .put("prizeName", prizeName)
        .put("playerName", userContext.getUserName())
        .build();

    NotificationMessage message = NotificationMessage.builder()
        .to(notificationKey)
        .data(data)
        .build();
    JsonObject sendMessageResponse = messagingClient.sendMessage(message).toCompletableFuture().join();
    System.out.println(sendMessageResponse);
  }

  private void informPlayersAboutClaim(String notificationKey, String mobileNumber,
      String prizeName) {
    UserContext userContext = userDAO.getUserByMob(mobileNumber).orElseThrow(RuntimeException::new);
    Map<String, Object> data = new ImmutableMap.Builder<String, Object>()
        .put("action", "CLAIM")
        .put("playerName", userContext.getUserName())
        .put("prizeName", prizeName)
        .build();
    NotificationMessage message = NotificationMessage.builder()
        .to(notificationKey)
        .data(data)
        .build();
    JsonObject sendMessageResponse = messagingClient.sendMessage(message).toCompletableFuture().join();
    System.out.println(sendMessageResponse);
  }

  public GameTicket getTicket(Integer gameID, Integer ticketID) {
    return gameTicketDAO.getTicketForByGameIDAndTicketID(gameID, ticketID);
  }

  public void raiseAlarm(Integer gameID, String mobileNumber) {
    informPlayerAboutAlarm(gameID, mobileNumber);
  }
}
