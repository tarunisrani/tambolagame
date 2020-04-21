package com.tambola.game;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class User {

  private Integer userID;
  private String userName;
}
