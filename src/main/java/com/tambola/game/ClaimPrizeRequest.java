package com.tambola.game;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ClaimPrizeRequest {
  private Integer gameID;
  private String prizeName;
  private List<Integer> selectedNUmber;
  private String mobileNumber;
}
