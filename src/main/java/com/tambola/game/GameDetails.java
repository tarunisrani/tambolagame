package com.tambola.game;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class GameDetails {

  private Long gameID;
  private String ownerName;
  private String gameType;
  private UserContext ownerDetails;
  private List<UserContext> players;

}
