package com.tambola.game.ticketgenerator.model;

import static com.tambola.game.ticketgenerator.service.TicketService.COLUMN_SIZE;
import static com.tambola.game.ticketgenerator.service.TicketService.ROW_SIZE;

public class TambolaTicketVO {

  private String[][] ticket = new String[ROW_SIZE][COLUMN_SIZE];

  public TambolaTicketVO(Integer[][] ticket) {
    generatePrintalbeTicket(ticket);
  }

  public void generatePrintalbeTicket(Integer[][] ticket){
    for(int i=0;i<ROW_SIZE;i++){
      for(int j=0;j<COLUMN_SIZE;j++){
        String str = ticket[i][j] == null?"  ":String.valueOf(ticket[i][j]);
        this.ticket[i][j] = str;
      }
    }
  }
}
