package com.tambola.game;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class UserCreationRequest {

  private String mobileNumber;
  private String userName;
  private String notificationKey;
  private String profilePic;
}
