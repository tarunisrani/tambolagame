package com.tambola.game;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class GamePrizeRequest {
  private Integer gameID;
  private List<String> prizeName;
}
