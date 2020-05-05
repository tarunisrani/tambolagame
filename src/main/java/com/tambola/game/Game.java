package com.tambola.game;

import com.tambola.game.ticketgenerator.model.TambolaTicketVO;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class Game {

  private Long gameID;
  private Long boardID;
  private String notificationKey;
  private String ownerName;
  private String ownerMobileNumber;
  private String status;
  private Long ownerID;
  private String gameType;
  private List<TambolaTicketVO> ticketList;

}
