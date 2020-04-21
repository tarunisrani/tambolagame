package com.tambola.game.game;

import com.tambola.game.Game;
import com.tambola.game.User;
import com.tambola.game.ticketgenerator.model.TambolaTicketVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tambola")
public class GameController {

  @Autowired
  GameService gameService;

  @PostMapping("/game")
  public Game createGame(@RequestParam("player_count") Integer playerCount,
      @RequestParam("game_type") String gameType,
      @RequestBody User user){
    return gameService.createGame(playerCount, gameType, user);
  }

  @PostMapping("/game/ticket/assign")
  @ResponseBody
  public TambolaTicketVO assignTicket(@RequestParam("mob_no") String mobileNumber, @RequestParam("game_id") Integer gameID ){
    return gameService.assignTicket(mobileNumber, gameID);
  }
}
