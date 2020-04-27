package com.tambola.game;

import java.util.Map;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class NotificationMessage {

  private String to;
  private Map<String, String> data;
}