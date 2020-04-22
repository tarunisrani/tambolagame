package com.tambola.game.game;

import com.google.common.collect.Sets;
import com.tambola.game.Game;
import com.tambola.game.GameNumberDAO;
import com.tambola.game.GameTicket;
import com.tambola.game.GameTicketDAO;
import com.tambola.game.User;
import com.tambola.game.ticketgenerator.model.TambolaTicketVO;
import com.tambola.game.ticketgenerator.service.RandomNumberGenerator;
import com.tambola.game.ticketgenerator.service.TicketService;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GameService {

  @Autowired
  private TicketService ticketService;

  @Autowired
  private GameTicketDAO gameTicketDAO;

  @Autowired
  private GameNumberDAO gameNumberDAO;

  public Game createGame(Integer playerCount, String gameType, User user){
    Integer gameID = new SecureRandom().nextInt(Integer.MAX_VALUE);
    List<TambolaTicketVO> tambolaTicketVOS = new ArrayList<>();
    if(gameType.equalsIgnoreCase("CLOSED")){
      tambolaTicketVOS = ticketService.generatePrintableTickets(playerCount);
    }

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
    Optional<GameTicket> ticket = gameTicketDAO
        .getTicketForByGameIDAndUser(gameID, mobileNumber);
    if(ticket.isPresent()){
      return ticket.get().getTicket();
    }
    Optional<GameTicket> availableTicket = gameTicketDAO.getAvailableTicket(gameID);
    if(availableTicket.isPresent()) {
      Integer ticketID = gameTicketDAO
          .assignTicketToUser(gameID, availableTicket.get().getTicketID(), mobileNumber);
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
    return nextNumber;
  }
}
