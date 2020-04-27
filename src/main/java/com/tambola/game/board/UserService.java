package com.tambola.game.board;

import com.tambola.game.CreateUserResponse;
import com.tambola.game.UserContext;
import com.tambola.game.game.UserDAO;
import com.tambola.game.ticketgenerator.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

  @Autowired
  private UserDAO userDAO;

  public CreateUserResponse createUser(UserContext user){
    return new CreateUserResponse(userDAO.addUser(user));
  }

  public String getNotificationKeyForUser(String mobileNumber){
    return userDAO.getUserByMob(mobileNumber).map(UserContext::getNotificationKey).orElseThrow(()-> new RuntimeException());
  }

  public String getNotificationKeyForUserById(Integer userID){
    return userDAO.getUserById(userID).map(UserContext::getNotificationKey).orElseThrow(()-> new RuntimeException());
  }
}
