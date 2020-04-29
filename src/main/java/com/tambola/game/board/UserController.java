package com.tambola.game.board;

import com.tambola.game.CreateUserResponse;
import com.tambola.game.UserContext;
import com.tambola.game.UserCreationRequest;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

  @GetMapping("/users")
  public List<UserContext> getUserList(@RequestParam("game_id") Integer gameID){
    return userService.getUserList(gameID);
  }
}
