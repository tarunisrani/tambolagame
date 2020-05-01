package com.tambola.game.ticketgenerator.service;

import com.tambola.game.ticketgenerator.model.TambolaTicket;
import com.tambola.game.ticketgenerator.model.TambolaTicketVO;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class TicketService {

  private static final Integer MAX_NUMBERS = 15;
  public static final int COLUMN_SIZE = 9;
  public static final int ROW_SIZE = 3;


  public List<TambolaTicket> generateTicket(int numberOfTickets){
    List<TambolaTicket> ticketList = new ArrayList<>();
    Set<TambolaTicket> generated = new HashSet<>();
    while(numberOfTickets>0){
      List<Integer>[] ticketNumberSet = generateSingleTicket();
      TambolaTicket ticket = new TambolaTicket(ticketNumberSet);
      if(!generated.contains(ticket)){
        ticketList.add(ticket);
        generated.add(ticket);
        numberOfTickets--;
      }
    }
    return ticketList;
  }

  public List<TambolaTicketVO> generatePrintableTickets(int numberOfTickets) {
    return generateTicket(numberOfTickets).stream().map(tambolaTicket -> {
      tambolaTicket.arrangeNumbers();
      tambolaTicket.printTicket();
      return new TambolaTicketVO(tambolaTicket.getTicket());
    }).collect(
        Collectors.toList());
  }

  private List<Integer>[] generateSingleTicket() {
    Boolean isValidTicket = false;
    List<Integer>[] ticket = null;
    while(Boolean.FALSE.equals(isValidTicket)){
      ticket = generateTicket();
      for(List<Integer> list : ticket){
        Collections.sort(list);
      }
      isValidTicket = checkIfTicketIsValid(ticket);
    }
    return ticket;
  }

  private Boolean checkIfTicketIsValid(List<Integer>[] ticket) {
    int count = 0;
    List<Integer> rowFirst = new ArrayList<>();
    List<Integer> rowSecond = new ArrayList<>();
    List<Integer> rowThird = new ArrayList<>();
    for(int i=0;i< COLUMN_SIZE;i++){
      count += ticket[i].stream().filter(el-> el!=0).count();
      List<Integer> integers = ticket[i];
      if(integers.isEmpty()){
        continue;
      }
      if(integers.get(0)!=null && integers.get(0)!=0){
        rowFirst.add(integers.get(0));
      }
      if(integers.size()>=2 && integers.get(1)!=null && integers.get(1)!=0){
        rowSecond.add(integers.get(1));
      }
      if(integers.size()>=3 && integers.get(2)!=null && integers.get(2)!=0){
        rowThird.add(integers.get(2));
      }
    }
    return count == 15 && rowFirst.size()==5 && rowSecond.size()==5 && rowThird.size()==5;
  }

  public List<Integer>[] generateTicket(){
    List<Integer>[] ticket = new List[COLUMN_SIZE];
    for(int i=0;i< COLUMN_SIZE;i++){
      ticket[i] = new ArrayList<>();
    }
    LinkedList<Integer> generatedNumber = new LinkedList<>();
    LinkedList<Integer> numberSequence = new LinkedList<>();

    List<Integer>[] rows = new List[3];
    for(int i=0;i< 3;i++){
      rows[i] = new ArrayList<>();
    }

    int count = 0;
    RandomNumberGenerator instance = RandomNumberGenerator.getInstance();
    while((rows[0].size()+rows[1].size()+rows[2].size())<MAX_NUMBERS){
      Integer newNumber = instance.generateNextNumber();
      numberSequence.add(newNumber);
      if(newNumber>=1 && newNumber<10){
        if (singleNumberOperation(ticket[0], generatedNumber, rows, newNumber)) {
          continue;
        }
        if(rows[ticket[0].size()%3].size()>=5){
          fillRowsWithZeros(ticket);
        }
      }else if(newNumber>=10 && newNumber<20){
        if (singleNumberOperation(ticket[1], generatedNumber, rows, newNumber)) {
          continue;
        }
        if(rows[ticket[1].size()%3].size()>=5){
          fillRowsWithZeros(ticket);
        }
      }else if(newNumber>=20 && newNumber<30){
        if (singleNumberOperation(ticket[2], generatedNumber, rows, newNumber)) {
          continue;
        }
        if(rows[ticket[2].size()%3].size()>=5){
          fillRowsWithZeros(ticket);
        }
      }else if(newNumber>=30 && newNumber<40){
        if (singleNumberOperation(ticket[3], generatedNumber, rows, newNumber)) {
          continue;
        }
        if(rows[ticket[4].size()%3].size()>=5){
          fillRowsWithZeros(ticket);
        }
      }else if(newNumber>=40 && newNumber<50){
        if (singleNumberOperation(ticket[4], generatedNumber, rows, newNumber)) {
          continue;
        }
        if(rows[ticket[4].size()%3].size()>=5){
          fillRowsWithZeros(ticket);
        }
      }else if(newNumber>=50 && newNumber<60){
        if (singleNumberOperation(ticket[5], generatedNumber, rows, newNumber)) {
          continue;
        }
        if(rows[ticket[5].size()%3].size()>=5){
          fillRowsWithZeros(ticket);
        }
      }else if(newNumber>=60 && newNumber<70){
        if (singleNumberOperation(ticket[6], generatedNumber, rows, newNumber)) {
          continue;
        }
        if(rows[ticket[6].size()%3].size()>=5){
          fillRowsWithZeros(ticket);
        }
      }else if(newNumber>=70 && newNumber<80){
        if (singleNumberOperation(ticket[7], generatedNumber, rows, newNumber)) {
          continue;
        }
        if(rows[ticket[7].size()%3].size()>=5){
          fillRowsWithZeros(ticket);
        }
      }else if(newNumber>=80 && newNumber<=90){
        if (singleNumberOperation(ticket[8], generatedNumber, rows, newNumber)) {
          continue;
        }
        if(rows[ticket[8].size()%3].size()>=5){
          fillRowsWithZeros(ticket);
        }
      }
      count++;
    }
    System.out.println(numberSequence);
    System.out.println(generatedNumber);
    return ticket;
  }

  private void fillRowsWithZeros(List<Integer>[] ticket) {
    for(List<Integer> integers : ticket){
      if(integers.isEmpty()){
        integers.add(0);
      }
    }
  }

  private boolean singleNumberOperation(List<Integer> integers, LinkedList<Integer> generatedNumber,
      List<Integer>[] rows, Integer newNumber) {
    if(integers.size() >= 3 || containsCloserNumber(generatedNumber, newNumber, 3) || generatedNumber.contains(newNumber) || (rows[integers.size()%3].size()>=5)){
      return true;
    }
    /*if(integers.size() == 3 || containsCloserNumber(generatedNumber, newNumber, 3) || generatedNumber.contains(newNumber)){
      return true;
    }*/
    generatedNumber.add(newNumber);
    integers.add(newNumber);
    Collections.sort(integers);
    rows[integers.size()-1%3].add(integers.get(integers.size()-1));

    return false;
  }

  private boolean containsCloserNumber(LinkedList<Integer> generatedNumber, Integer newNumber, Integer offset){
    Integer lowerLimit = newNumber - offset;
    Integer upperLimit = newNumber + offset;

//    return generatedNumber.stream().anyMatch(el -> el>=lowerLimit && el<=upperLimit);
    return false;
  }
}
