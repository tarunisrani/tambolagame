package com.tambola.game;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class UserCreationRequest {

  private String mobNumber;
  private String userName;
  private String notificationKey;
}
