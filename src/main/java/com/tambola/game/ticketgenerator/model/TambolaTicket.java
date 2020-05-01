package com.tambola.game.ticketgenerator.model;

import static com.tambola.game.ticketgenerator.service.TicketService.COLUMN_SIZE;
import static com.tambola.game.ticketgenerator.service.TicketService.ROW_SIZE;

import java.util.Arrays;
import java.util.List;

public class TambolaTicket {

  private List<Integer>[] ticketNumberSet;

  public Integer[][] getTicket() {
    return ticket;
  }

  private Integer[][] ticket = new Integer[ROW_SIZE][COLUMN_SIZE];

  public TambolaTicket(List<Integer>[] ticketNumberSet) {
    this.ticketNumberSet = ticketNumberSet;
  }

  public void arrangeNumbers(){
    for(int i=0;i< COLUMN_SIZE;i++){
      List<Integer> numberSet = ticketNumberSet[i];
      for(int j=0;j<numberSet.size();j++){
        Integer number = numberSet.get(j);
        ticket[j][i] = number;
      }
    }
  }

  public void printTicket(){
    for(int i=0;i<ROW_SIZE;i++){
      for(int j=0;j<COLUMN_SIZE;j++){
        String str = ticket[i][j] == null || ticket[i][j] == 0?"  ":String.valueOf(ticket[i][j]);
        System.out.printf("%4s|", str);
      }
      System.out.println();
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof TambolaTicket)) {
      return false;
    }
    TambolaTicket that = (TambolaTicket) o;
    return Arrays.equals(ticketNumberSet, that.ticketNumberSet);
  }

  @Override
  public int hashCode() {

    int result = Arrays.hashCode(ticketNumberSet);
    result = 31 * result + Arrays.hashCode(ticket);
    return result;
  }
}
