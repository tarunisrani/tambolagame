package com.tambola.game.board;

import com.tambola.game.ticketgenerator.model.TambolaTicket;
import com.tambola.game.ticketgenerator.service.TicketService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tambola/board")
public class BoardController {

  @Autowired
  BoardService boardService;

}
