package com.tambola.game;

import lombok.Builder;

@Builder
public class GamePrize {
  private Integer gameID;
  private String prizeName;
  private Long prizeAmount;
  private String prizeWinner;
}
