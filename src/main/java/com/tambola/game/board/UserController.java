package com.tambola.game.board;

import com.tambola.game.CreateUserResponse;
import com.tambola.game.UserContext;
import com.tambola.game.UserCreationRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tambola")
public class UserController {

  @Autowired
  private UserService userService;

  @PostMapping("/user")
  public CreateUserResponse createNewUser(@RequestBody UserCreationRequest request){
    UserContext userContext = UserContext.builder()
        .userName(request.getUserName())
        .notificationKey(request.getNotificationKey())
        .mobileNumber(request.getMobNumber())
        .build();
    return userService.createUser(userContext);
  }
}
