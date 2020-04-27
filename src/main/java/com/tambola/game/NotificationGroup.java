package com.tambola.game;

import java.util.List;
import java.util.Map;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class NotificationGroup {

  private String operation;
  private String notification_key_name;
  private List<String> registration_ids;
}
