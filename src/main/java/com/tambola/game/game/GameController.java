package com.tambola.game.game;

import com.tambola.game.ClaimPrizeRequest;
import com.tambola.game.Game;
import com.tambola.game.GamePrize;
import com.tambola.game.GamePrizeRequest;
import com.tambola.game.GameTicket;
import com.tambola.game.UserContext;
import com.tambola.game.ticketgenerator.model.TambolaTicketVO;
import java.util.LinkedList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tambola")
public class GameController {

  @Autowired
  private GameService gameService;

  @PostMapping("/game")
  public Game createGame(@RequestParam("player_count") Integer playerCount,
      @RequestParam(value = "game_type", required = false) String gameType,
      @RequestBody UserContext user){
    return gameService.createGame(playerCount, gameType, user);
  }

  @PostMapping("/game/prize")
  public void addPrizesToGame(@RequestBody GamePrizeRequest request){
    gameService.addPrizes(request);
  }

  @GetMapping("/game/{gameID}")
  public Game getGameDetail(@PathVariable("gameID") Integer gameID){
    return gameService.getGameDetails(gameID);
  }

  @PutMapping("/game/{gameID}")
  public void finishGame(@PathVariable("gameID") Integer gameID, @RequestParam("mob_no") String mobileNumber){
    gameService.markGameAsFinished(gameID, mobileNumber);
  }

  @PostMapping("/game/ticket/assign")
  @ResponseBody
  public TambolaTicketVO assignTicket(@RequestParam("mob_no") String mobileNumber, @RequestParam("game_id") Integer gameID ){
    return gameService.assignTicket(mobileNumber, gameID);
  }

  @GetMapping("/game/nextnumber")
  public Integer getNewNumber(@RequestParam("game_id") Integer gameID, @RequestParam("mob_no") String mobileNumber){
    return gameService.getNewNumber(gameID, mobileNumber);
  }

  @GetMapping("/game/allnumbers")
  public LinkedList<Integer> getAllNumbers(@RequestParam("game_id") Integer gameID){
    return gameService.getGeneratedNumbers(gameID);
  }

  @PostMapping("/game/claim")
  public void claimPrize(@RequestBody ClaimPrizeRequest request){
    gameService.claimPrize(request.getGameID(), request.getPrizeName(), request.getMobileNumber(), request.getSelectedNUmber());
  }

  @GetMapping("/game/prize")
  public List<GamePrize> getPrizeList(@RequestParam("game_id") Integer gameID){
    return gameService.getPrizeList(gameID);
  }

  @PutMapping("/game/prize")
  public void updatePrizeStatus(@RequestParam("game_id") Integer gameID,
      @RequestParam("prize_name") String prizeName,
      @RequestParam("winner_name") String winnerName){
    gameService.updateWinnerOfPrize(gameID, prizeName, winnerName);
  }

  @GetMapping("/game/ticket/{ticketID}")
  @ResponseBody
  public GameTicket getTicket(@RequestParam("game_id") Integer gameID, @RequestParam("ticket_id") Integer ticketID){
    return gameService.getTicket(gameID, ticketID);
  }

  @PostMapping("/game/alarm")
  public void raisAlarm(@RequestParam("game_id") Integer gameID, @RequestParam("mob_no") String mobileNumber){
    gameService.raiseAlarm(gameID, mobileNumber);
  }
}
