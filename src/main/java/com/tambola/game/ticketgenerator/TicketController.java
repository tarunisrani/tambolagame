package com.tambola.game.ticketgenerator;

import com.tambola.game.ticketgenerator.model.TambolaTicketVO;
import com.tambola.game.ticketgenerator.service.TicketService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tambola/ticket")
public class TicketController {

  @Autowired
  TicketService ticketService;

  @GetMapping("/generate")
  @ResponseBody
  public List<TambolaTicketVO> getTicket(@RequestParam(defaultValue = "1") Integer ticketCount){
    return ticketService.generatePrintableTickets(ticketCount);
  }

}
