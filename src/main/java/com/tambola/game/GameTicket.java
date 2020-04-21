package com.tambola.game;

import com.tambola.game.ticketgenerator.model.TambolaTicketVO;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class GameTicket {
  private Integer gameID;
  private Integer ticketID;
  private TambolaTicketVO ticket;
  private String assignTo;

}
