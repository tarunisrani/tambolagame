package com.tambola.game;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class UserContext {

  private Integer userID;
  private String userName;
  private String mobileNumber;
  private String notificationKey;
}
