package com.tambola.game;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class NotificationGroupAdd {

  private String operation;
  private String notification_key_name;
  private String notification_key;
  private List<String> registration_ids;
}
